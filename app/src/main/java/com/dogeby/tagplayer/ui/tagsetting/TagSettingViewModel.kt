package com.dogeby.tagplayer.ui.tagsetting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.tag.AddTagToVideosUseCase
import com.dogeby.tagplayer.domain.tag.CreateTagUseCase
import com.dogeby.tagplayer.domain.tag.FindTagsUseCase
import com.dogeby.tagplayer.domain.tag.GetAllTagsUseCase
import com.dogeby.tagplayer.domain.tag.GetCommonTagsFromVideosUseCase
import com.dogeby.tagplayer.domain.tag.RemoveTagFromVideosUseCase
import com.dogeby.tagplayer.ui.navigation.VideoIdsArgument
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TagSettingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCommonTagsFromVideosUseCase: GetCommonTagsFromVideosUseCase,
    getAllTagsUseCase: GetAllTagsUseCase,
    findTagsUseCase: FindTagsUseCase,
    private val createTagUseCase: CreateTagUseCase,
    private val addTagToVideosUseCase: AddTagToVideosUseCase,
    private val removeTagFromVideosUseCase: RemoveTagFromVideosUseCase,
) : ViewModel() {

    private val videoIds: List<Long> = Gson().fromJson(
        checkNotNull<String>(savedStateHandle[VideoIdsArgument]),
        LongArray::class.java,
    ).toList()

    private val tagSearchKeyword = MutableStateFlow("")

    val commonTags: StateFlow<List<Tag>> = getCommonTagsFromVideosUseCase(videoIds)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val tagSearchResultUiState: StateFlow<TagSearchResultUiState> = tagSearchKeyword
        .debounce(KEYWORD_INPUT_TIMEOUT)
        .flatMapLatest { keyword ->
            if (keyword.isBlank()) {
                getAllTagsUseCase()
            } else {
                findTagsUseCase(keyword)
            }
        }
        .mapLatest { tags ->
            if (tags.isEmpty()) {
                if (tagSearchKeyword.value.isBlank()) {
                    TagSearchResultUiState.Empty
                } else {
                    TagSearchResultUiState.EmptySearchResult(tagSearchKeyword.value)
                }
            } else {
                val commonTagsHashSet = commonTags.value.toHashSet()
                val tagSearchResultItemUiStates = tags.map { TagSearchResultItemUiState(it.id, it.name, commonTagsHashSet.contains(it)) }
                TagSearchResultUiState.Success(tagSearchResultItemUiStates)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TagSearchResultUiState.Loading,
        )

    fun setTagSearchKeyword(keyword: String) {
        tagSearchKeyword.value = keyword
    }

    fun createTag(name: String) {
        viewModelScope.launch {
            createTagUseCase(name)
        }
    }

    fun addTagToVideos(tagId: Long) {
        viewModelScope.launch {
            addTagToVideosUseCase(tagId, videoIds)
        }
    }

    fun removeTagFromVideos(tagId: Long) {
        viewModelScope.launch {
            removeTagFromVideosUseCase(tagId, videoIds)
        }
    }

    companion object {

        private const val KEYWORD_INPUT_TIMEOUT = 500L
    }
}
