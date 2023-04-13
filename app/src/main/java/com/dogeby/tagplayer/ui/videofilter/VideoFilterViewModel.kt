package com.dogeby.tagplayer.ui.videofilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.preferences.videolist.AddDirectoryFilterUseCase
import com.dogeby.tagplayer.domain.preferences.videolist.AddTagFilterUseCase
import com.dogeby.tagplayer.domain.preferences.videolist.GetFilteredDirectoryNameUseCase
import com.dogeby.tagplayer.domain.preferences.videolist.GetFilteredTagIdUseCase
import com.dogeby.tagplayer.domain.preferences.videolist.RemoveDirectoryFilterUseCase
import com.dogeby.tagplayer.domain.preferences.videolist.RemoveTagFilterUseCase
import com.dogeby.tagplayer.domain.tag.GetAllTagsUseCase
import com.dogeby.tagplayer.domain.video.GetAllDirectoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
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
    getAllDirectoriesUseCase: GetAllDirectoriesUseCase,
    getFilteredDirectoryNameUseCase: GetFilteredDirectoryNameUseCase,
    private val addTagFilterUseCase: AddTagFilterUseCase,
    private val removeTagFilterUseCase: RemoveTagFilterUseCase,
    private val addDirectoryFilterUseCase: AddDirectoryFilterUseCase,
    private val removeDirectoryFilterUseCase: RemoveDirectoryFilterUseCase,
) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val videoTagFilterItemUiStates: Flow<List<VideoTagFilterItemUiState>> = getAllTagsUseCase()
        .combine(getFilteredTagIdUseCase()) { tags: List<Tag>, filteredTagIds: List<Long> ->
            tags.map { tag ->
                VideoTagFilterItemUiState(
                    tagId = tag.id,
                    tagName = tag.name,
                    isFilteredTag = tag.id in filteredTagIds,
                )
            }
        }
    private val videoDirectoryFilterItemUiStates: Flow<List<VideoDirectoryFilterItemUiState>> =
        getAllDirectoriesUseCase()
            .combine(getFilteredDirectoryNameUseCase()) { directories: Set<String>, filteredDirectoryNames: List<String> ->
                directories.map {
                    VideoDirectoryFilterItemUiState(
                        name = it,
                        isFiltered = it in filteredDirectoryNames,
                    )
                }
            }

    @OptIn(FlowPreview::class)
    val videoFilterUiState: StateFlow<VideoFilterUiState> = combine(
        query.debounce(KEYWORD_INPUT_TIMEOUT),
        videoDirectoryFilterItemUiStates,
        videoTagFilterItemUiStates,
    ) { query, directoryFilters, tagFilters ->
        val directoryFiltersFilteredByQuery = directoryFilters.filter { it.name.contains(query) }
        val tagFiltersFilteredByQuery = tagFilters.filter { it.tagName.contains(query) }

        createVideoFilterUiState(directoryFiltersFilteredByQuery, tagFiltersFilteredByQuery)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = VideoFilterUiState.Loading,
    )

    private fun createVideoFilterUiState(
        directoryFilters: List<VideoDirectoryFilterItemUiState>,
        tagFilters: List<VideoTagFilterItemUiState>,
    ): VideoFilterUiState {
        if (directoryFilters.isEmpty() and tagFilters.isEmpty()) return VideoFilterUiState.Empty
        return VideoFilterUiState.Success(
            directoryFilterUiState = if (directoryFilters.isEmpty()) VideoDirectoryFilterUiState.Empty else VideoDirectoryFilterUiState.Success(directoryFilters),
            tagFilterUiState = if (tagFilters.isEmpty()) VideoTagFilterUiState.Empty else VideoTagFilterUiState.Success(tagFilters)
        )
    }

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

    fun addDirectoryFilter(name: String) {
        viewModelScope.launch {
            addDirectoryFilterUseCase(name)
        }
    }

    fun removeDirectoryFilter(name: String) {
        viewModelScope.launch {
            removeDirectoryFilterUseCase(name)
        }
    }

    companion object {

        private const val KEYWORD_INPUT_TIMEOUT = 200L
    }
}
