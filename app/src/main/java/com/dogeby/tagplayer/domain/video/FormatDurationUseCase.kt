package com.dogeby.tagplayer.domain.video

import javax.inject.Inject

class FormatDurationUseCase @Inject constructor() {

    operator fun invoke(duration: Int): String {
        val seconds = duration / 1_000 % 60
        val minutes = duration / 60_000 % 60
        val hours = duration / 3_600_000 % 60
        return if (hours == 0) String.format("%02d:%02d", minutes, seconds)
        else String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
