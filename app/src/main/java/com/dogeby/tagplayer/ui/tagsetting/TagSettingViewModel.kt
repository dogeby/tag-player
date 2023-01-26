package com.dogeby.tagplayer.ui.tagsetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.tag.AddTagToVideosUseCase
import com.dogeby.tagplayer.domain.tag.CreateTagUseCase
import com.dogeby.tagplayer.domain.tag.FindTagsUseCase
import com.dogeby.tagplayer.domain.tag.GetCommonTagsFromVideosUseCase
import com.dogeby.tagplayer.domain.tag.RemoveTagFromVideosUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class TagSettingViewModel @AssistedInject constructor(
    getCommonTagsFromVideosUseCase: GetCommonTagsFromVideosUseCase,
    findTagsUseCase: FindTagsUseCase,
    @Assisted private val videoIds: List<Long>,
    private val createTagUseCase: CreateTagUseCase,
    private val addTagToVideosUseCase: AddTagToVideosUseCase,
    private val removeTagFromVideosUseCase: RemoveTagFromVideosUseCase,
) : ViewModel() {

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

        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            assistedFactory: Factory,
            videoIds: List<Long>,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TagSettingViewModel::class.java)) {
                    return assistedFactory.create(videoIds) as T
                }
                throw IllegalAccessException()
            }
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(videoIds: List<Long>): TagSettingViewModel
    }
}
