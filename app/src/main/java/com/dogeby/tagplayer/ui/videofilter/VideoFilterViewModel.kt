package com.dogeby.tagplayer.ui.videofilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.preferences.AddTagFilterUseCase
import com.dogeby.tagplayer.domain.preferences.GetFilteredTagIdUseCase
import com.dogeby.tagplayer.domain.preferences.RemoveTagFilterUseCase
import com.dogeby.tagplayer.domain.tag.GetAllTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class VideoFilterViewModel @Inject constructor(
    getAllTagsUseCase: GetAllTagsUseCase,
    getFilteredTagIdUseCase: GetFilteredTagIdUseCase,
    private val addTagFilterUseCase: AddTagFilterUseCase,
    private val removeTagFilterUseCase: RemoveTagFilterUseCase,
) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(FlowPreview::class)
    val videoFilterUiState: StateFlow<VideoFilterUiState> = getAllTagsUseCase()
        .combine(getFilteredTagIdUseCase()) { tags: List<Tag>, filteredTagIds: List<Long> ->
            tags.map { tag ->
                VideoTagFilterUiState(
                    tagId = tag.id,
                    tagName = tag.name,
                    isFilteredTag = tag.id in filteredTagIds,
                )
            }
        }
        .combine(query.debounce(KEYWORD_INPUT_TIMEOUT)) { tagFilters: List<VideoTagFilterUiState>, query: String ->
            val tagFiltersFilteredByQuery =
                if (query.isNotBlank()) tagFilters.filter { it.tagName.contains(query) } else tagFilters
            if (tagFiltersFilteredByQuery.isEmpty()) return@combine VideoFilterUiState.Empty
            VideoFilterUiState.Success(
                tagFilters = tagFiltersFilteredByQuery,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoFilterUiState.Loading,
        )

    fun setQuery(query: String) {
        _query.value = query
    }

    fun clearQuery() {
        setQuery("")
    }

    fun addTagFilter(tagId: Long) {
        viewModelScope.launch {
            addTagFilterUseCase(tagId)
        }
    }

    fun removeTagFilter(tagId: Long) {
        viewModelScope.launch {
            removeTagFilterUseCase(tagId)
        }
    }

    companion object {

        private const val KEYWORD_INPUT_TIMEOUT = 200L
    }
}
