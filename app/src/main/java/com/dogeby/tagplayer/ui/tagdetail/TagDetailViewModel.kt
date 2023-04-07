package com.dogeby.tagplayer.ui.tagdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.tag.DeleteTagsUseCase
import com.dogeby.tagplayer.domain.tag.GetTagItemUseCase
import com.dogeby.tagplayer.domain.tag.ModifyTagNameUseCase
import com.dogeby.tagplayer.domain.video.UpdateVideoListUseCase
import com.dogeby.tagplayer.ui.navigation.TagDetailTagIdArgument
import com.dogeby.tagplayer.ui.tagsetting.TagNameEditDialogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TagDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getTagItemUseCase: GetTagItemUseCase,
    private val deleteTagsUseCase: DeleteTagsUseCase,
    private val modifyTagNameUseCase: ModifyTagNameUseCase,
    private val updateVideoListUseCase: UpdateVideoListUseCase,
) : ViewModel() {

    private val tagId: Long = checkNotNull(savedStateHandle[TagDetailTagIdArgument])

    @OptIn(ExperimentalCoroutinesApi::class)
    val tagDetailUiState = getTagItemUseCase(tagId).mapLatest { tagItemResult ->
        val tagItem = tagItemResult.getOrElse { return@mapLatest TagDetailUiState.Empty }
        TagDetailUiState.Success(
            tagId = tagItem.id,
            tagName = tagItem.name,
            videoItems = tagItem.videoItems,
        )
    }
        .onStart {
            TagDetailUiState.Loading
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TagDetailUiState.Loading
        )

    private val _tagNameEditDialogUiState: MutableStateFlow<TagNameEditDialogUiState> = MutableStateFlow(TagNameEditDialogUiState.Hide)
    val tagNameEditDialogUiState: StateFlow<TagNameEditDialogUiState> = _tagNameEditDialogUiState.asStateFlow()

    fun deleteTag() {
        viewModelScope.launch {
            deleteTagsUseCase(listOf(tagId))
        }
    }

    fun modifyTagName(name: String) {
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
                    setTagNameEditDialogVisibility(false)
                } else {
                    _tagNameEditDialogUiState.value = tagNameEditDialogUiStateValue.copy(
                        isError = true,
                        supportingTextResId = R.string.tagNameDuplicateNameError,
                    )
                }
            }
        }
    }

    fun setTagNameEditDialogVisibility(visibility: Boolean) {
        with(tagDetailUiState.value) {
            _tagNameEditDialogUiState.value = if (visibility && this is TagDetailUiState.Success) {
                TagNameEditDialogUiState.Show(tagId, tagName, false)
            } else {
                TagNameEditDialogUiState.Hide
            }
        }
    }

    fun updateVideoList() {
        viewModelScope.launch {
            updateVideoListUseCase()
        }
    }
}
