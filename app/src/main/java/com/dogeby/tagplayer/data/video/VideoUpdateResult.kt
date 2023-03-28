package com.dogeby.tagplayer.data.video

sealed interface VideoUpdateResult {

    object Cached : VideoUpdateResult

    object Nothing : VideoUpdateResult
}
