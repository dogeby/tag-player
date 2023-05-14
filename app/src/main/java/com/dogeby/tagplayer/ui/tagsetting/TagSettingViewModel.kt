package com.dogeby.tagplayer.ui.tagsetting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.data.tag.Tag
import com.dogeby.tagplayer.domain.tag.AddTagToVideosUseCase
import com.dogeby.tagplayer.domain.tag.CreateTagUseCase
import com.dogeby.tagplayer.domain.tag.DeleteTagsUseCase
import com.dogeby.tagplayer.domain.tag.FindTagsUseCase
import com.dogeby.tagplayer.domain.tag.GetAllTagsUseCase
import com.dogeby.tagplayer.domain.tag.GetCommonTagsFromVideosUseCase
import com.dogeby.tagplayer.domain.tag.ModifyTagNameUseCase
import com.dogeby.tagplayer.domain.tag.RemoveTagFromVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.SortedSet
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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
    private val deleteTagsUseCase: DeleteTagsUseCase,
    private val modifyTagNameUseCase: ModifyTagNameUseCase,
) : ViewModel() {

    private val tagSettingArgs: TagSettingArgs = TagSettingArgs(savedStateHandle)

    private val tagSearchKeyword = MutableStateFlow("")

    private val commonTags: StateFlow<SortedSet<Tag>> = getCommonTagsFromVideosUseCase(tagSettingArgs.videoIds)
        .map { tags -> tags.toSortedSet(compareBy { it.name }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = sortedSetOf(),
        )

    val tagInputChipTextFieldUiState: StateFlow<TagInputChipTextFieldUiState> = commonTags.combine(tagSearchKeyword) { tags: SortedSet<Tag>, keyword: String ->
        TagInputChipTextFieldUiState(tags.toList(), keyword)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TagInputChipTextFieldUiState(),
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
        .combine(commonTags) { foundTags, commonTags ->
            val keyword = tagSearchKeyword.value.trim()
            if (foundTags.isEmpty() && keyword.isBlank()) {
                return@combine TagSearchResultUiState.Empty
            }
            val tagSearchResultItemUiStates = foundTags.map {
                TagSearchResultItemUiState(
                    id = it.id,
                    name = it.name,
                    isIncluded = commonTags.contains(it),
                )
            }
            TagSearchResultUiState.Success(
                tags = tagSearchResultItemUiStates,
                keyword = keyword,
                isShowTagCreateText = keyword.isNotBlank() && foundTags.find { it.name == keyword } == null,
            )
        }
        .onStart {
            TagSearchResultUiState.Loading
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TagSearchResultUiState.Loading,
        )

    private val _tagNameEditDialogUiState: MutableStateFlow<TagNameEditDialogUiState> =
        MutableStateFlow(TagNameEditDialogUiState.Hide)
    val tagNameEditDialogUiState: StateFlow<TagNameEditDialogUiState> =
        _tagNameEditDialogUiState.asStateFlow()

    fun setTagSearchKeyword(keyword: String) {
        tagSearchKeyword.value = keyword
    }

    fun createTag(name: String) {
        viewModelScope.launch {
            createTagUseCase(name).onSuccess {
                addTagToVideos(it)
            }
        }
    }

    fun addTagToVideos(tagId: Long) {
        viewModelScope.launch {
            addTagToVideosUseCase(tagId, tagSettingArgs.videoIds)
            tagSearchKeyword.value = ""
        }
    }

    fun removeTagFromVideos(tagId: Long) {
        viewModelScope.launch {
            removeTagFromVideosUseCase(tagId, tagSettingArgs.videoIds)
        }
    }

    fun deleteTag(tagId: Long) {
        viewModelScope.launch {
            deleteTagsUseCase(listOf(tagId))
        }
    }

    fun modifyTagName(tagId: Long, name: String) {
        val tagNameEditDialogUiStateValue = tagNameEditDialogUiState.value
        if (tagNameEditDialogUiStateValue is TagNameEditDialogUiState.Show) {
            if (name.isBlank()) {
                _tagNameEditDialogUiState.value = tagNameEditDialogUiStateValue.copy(
                    isError = true,
                    supportingTextResId = R.string.tagNameBlankError,
                )
                return
            }
            viewModelScope.launch {
                if (modifyTagNameUseCase(tagId, name.trim()).isSuccess) {
                    hideTagNameEditDialog()
                } else {
                    _tagNameEditDialogUiState.value = tagNameEditDialogUiStateValue.copy(
                        isError = true,
                        supportingTextResId = R.string.tagNameDuplicateNameError,
                    )
                }
            }
        }
    }

    fun showTagNameEditDialog(tagId: Long, name: String) {
        _tagNameEditDialogUiState.value = TagNameEditDialogUiState.Show(tagId, name, false)
    }

    fun hideTagNameEditDialog() {
        _tagNameEditDialogUiState.value = TagNameEditDialogUiState.Hide
    }

    companion object {

        private const val KEYWORD_INPUT_TIMEOUT = 200L
    }
}
