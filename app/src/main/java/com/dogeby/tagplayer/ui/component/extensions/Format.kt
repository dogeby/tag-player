package com.dogeby.tagplayer.ui.component.extensions

fun Long.formatToSimpleSize(): String {
    val (div, unitName) = when {
        this > 1_000_000_000L -> 1_000_000_000.0 to "GB"
        this > 1_000_000L -> 1_000_000.0 to "MB"
        this > 1_000L -> 1_000.0 to "KB"
        else -> 1.0 to "Byte"
    }
    return String.format("%.2f $unitName", (this / div))
}
