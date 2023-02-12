package com.dogeby.tagplayer.domain.video

import javax.inject.Inject

class FormatSizeUseCase @Inject constructor() {

    operator fun invoke(size: Long): String {
        val (div, unitName) = when {
            size > 1_000_000_000L -> 1_000_000_000.0 to "GB"
            size > 1_000_000L -> 1_000_000.0 to "MB"
            size > 1_000L -> 1_000.0 to "KB"
            else -> 1.0 to "Byte"
        }
        return String.format("%.2f $unitName", (size / div))
    }
}
