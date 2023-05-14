package com.dogeby.tagplayer.ui.videosearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogeby.tagplayer.domain.video.FindVideoUseCase
import com.dogeby.tagplayer.ui.videolist.VideoListUiState
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class VideoSearchViewModel @Inject constructor(
    findVideoUseCase: FindVideoUseCase,
) : ViewModel() {

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val videoSearchViewUiState: StateFlow<VideoSearchViewUiState> = query
        .debounce(KEYWORD_INPUT_TIMEOUT)
        .flatMapLatest {
            if (it.isBlank()) {
                return@flatMapLatest flow { emit(emptyList()) }
            }
            findVideoUseCase(it)
        }
        .map { videoItems ->
            if (videoItems.isEmpty()) {
                return@map if (query.value.isBlank()) VideoSearchViewUiState.QueryBlank else VideoSearchViewUiState.Empty
            }
            VideoSearchViewUiState.Success(VideoListUiState.Success(videoItems))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VideoSearchViewUiState.QueryBlank,
        )

    fun setQuery(query: String) {
        _query.value = query
    }

    fun clearQuery() {
        setQuery("")
    }

    companion object {

        private const val KEYWORD_INPUT_TIMEOUT = 200L
    }
}
