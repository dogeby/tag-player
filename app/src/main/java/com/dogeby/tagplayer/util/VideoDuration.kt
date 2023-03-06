package com.dogeby.tagplayer.util

data class VideoDuration(
    val value: Long
) {

    override fun toString(): String {
        val seconds = value / 1_000 % 60
        val minutes = value / 60_000 % 60
        val hours = value / 3_600_000 % 60
        return if (hours == 0L) String.format("%02d:%02d", minutes, seconds)
        else String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
