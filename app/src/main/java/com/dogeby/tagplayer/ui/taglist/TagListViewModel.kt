package com.dogeby.tagplayer.ui.taglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.domain.tag.GetTagItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class TagListViewModel @Inject constructor(
    getTagItemsUseCase: GetTagItemsUseCase,
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
}
