package com.dogeby.tagplayer.ui.tagsetting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.tag.AddTagToVideosUseCase
import com.dogeby.tagplayer.domain.tag.CreateTagUseCase
import com.dogeby.tagplayer.domain.tag.FindTagsUseCase
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TagSettingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCommonTagsFromVideosUseCase: GetCommonTagsFromVideosUseCase,
    findTagsUseCase: FindTagsUseCase,
    private val createTagUseCase: CreateTagUseCase,
    private val addTagToVideosUseCase: AddTagToVideosUseCase,
    private val removeTagFromVideosUseCase: RemoveTagFromVideosUseCase,
) : ViewModel() {

    private val videoIds: List<Long> = Gson().fromJson(
        checkNotNull<String>(savedStateHandle[VideoIdsArgument]),
        LongArray::class.java,
    ).toList()

    private val _tagSearchKeyword = MutableStateFlow("")
    private val tagSearchKeyword = _tagSearchKeyword.asStateFlow()

    val commonTags: StateFlow<List<Tag>> = getCommonTagsFromVideosUseCase(videoIds)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val tagSearchResult: StateFlow<List<Tag>> = tagSearchKeyword
        .debounce(KEYWORD_INPUT_TIMEOUT)
        .flatMapLatest { keyword ->
            findTagsUseCase(keyword)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun setTagSearchKeyword(keyword: String) {
        _tagSearchKeyword.value = keyword
    }

    suspend fun createTag(name: String) {
        createTagUseCase(name)
    }

    suspend fun addTagToVideos(tagId: Long) {
        addTagToVideosUseCase(tagId, videoIds)
    }

    suspend fun removeTagFromVideos(tagId: Long) {
        removeTagFromVideosUseCase(tagId, videoIds)
    }

    companion object {

        private const val KEYWORD_INPUT_TIMEOUT = 500L
    }
}
