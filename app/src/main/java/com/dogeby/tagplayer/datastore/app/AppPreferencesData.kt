package com.dogeby.tagplayer.datastore.app

data class AppPreferencesData(
    val appThemeMode: AppThemeMode,
    val autoRotation: Boolean,
    val rejectedUpdateVersionCode: Int,
)
