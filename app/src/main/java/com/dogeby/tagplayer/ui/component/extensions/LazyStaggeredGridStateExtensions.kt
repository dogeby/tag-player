package com.dogeby.tagplayer.ui.component.extensions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalFoundationApi::class, FlowPreview::class)
@Composable
fun LazyStaggeredGridState.OnFirstVisibleItemIndexChange(
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
