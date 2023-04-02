package com.dogeby.tagplayer.ui.taglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.R
import com.dogeby.tagplayer.domain.tag.CreateTagUseCase
import com.dogeby.tagplayer.domain.tag.GetTagItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TagListViewModel @Inject constructor(
    getTagItemsUseCase: GetTagItemsUseCase,
    private val createTagUseCase: CreateTagUseCase,
) : ViewModel() {

    val tagListUiState: StateFlow<TagListUiState> = getTagItemsUseCase()
        .map { tagItems ->
            if (tagItems.isEmpty()) return@map TagListUiState.Empty
            TagListUiState.Success(tagItems)
        }
        .onStart { emit(TagListUiState.Loading) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TagListUiState.Loading,
        )

    private val _tagCreateDialogUiState: MutableStateFlow<TagCreateDialogUiState> = MutableStateFlow(TagCreateDialogUiState.Hide)
    val tagCreateDialogUiState: StateFlow<TagCreateDialogUiState> = _tagCreateDialogUiState.asStateFlow()

    fun createTag(name: String) {
        val tagCreateDialogUiState = this.tagCreateDialogUiState.value

        if (tagCreateDialogUiState !is TagCreateDialogUiState.Show) return
        if (name.isBlank()) {
            _tagCreateDialogUiState.value = TagCreateDialogUiState.Show(true, R.string.tagNameBlankError)
            return
        }

        viewModelScope.launch {
            createTagUseCase(name.trim())
                .onSuccess {
                    setTagCreateDialogVisibility(false)
                }
                .onFailure {
                    _tagCreateDialogUiState.value = TagCreateDialogUiState.Show(
                        isError = true,
                        supportingTextResId = R.string.tagNameDuplicateNameError,
                    )
                }
        }
    }

    fun setTagCreateDialogVisibility(visibility: Boolean) {
        _tagCreateDialogUiState.value = if (visibility) TagCreateDialogUiState.Show() else TagCreateDialogUiState.Hide
    }
}
