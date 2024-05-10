package com.dogeby.tagplayer.ui.component.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun LazyListState.OnFirstVisibleItemIndexChange(
    debounceDuration: Long = 500L,
    onFirstVisibleItemIndexChange: (Int) -> Unit,
) {
    val state = this
    LaunchedEffect(state) {
        snapshotFlow {
            state.firstVisibleItemIndex
        }
            .debounce(debounceDuration)
            .collectLatest {
                onFirstVisibleItemIndexChange(it)
            }
    }
}
