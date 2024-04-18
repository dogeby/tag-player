package com.dogeby.tagplayer.datastore.app

import kotlinx.coroutines.flow.Flow

interface AppPreferencesDataSource {

    val appPreferencesData: Flow<AppPreferencesData>

    suspend fun setAppThemeMode(appThemeMode: AppThemeMode)

    suspend fun setAutoRotation(isAutoRotation: Boolean)

    suspend fun setRejectedUpdateVersionCode(versionCode: Int)

    suspend fun resetAppPreferences()
}
