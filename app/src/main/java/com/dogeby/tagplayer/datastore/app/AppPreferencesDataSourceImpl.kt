package com.dogeby.tagplayer.datastore.app

import androidx.datastore.core.DataStore
import com.dogeby.tagplayer.AppPreferences
import com.dogeby.tagplayer.copy
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class AppPreferencesDataSourceImpl @Inject constructor(
    private val appPreferences: DataStore<AppPreferences>
) : AppPreferencesDataSource {

    private val appThemeModeDefaultValue = AppThemeMode.SYSTEM_SETTING

    override val appPreferencesData: Flow<AppPreferencesData> = appPreferences
        .data
        .map {
            AppPreferencesData(
                AppThemeMode.values().getOrElse(it.appThemeMode) {
                    appThemeModeDefaultValue
                }
            )
        }

    override suspend fun resetAppPreferences() {
        setAppThemeMode(appThemeModeDefaultValue)
    }

    override suspend fun setAppThemeMode(appThemeMode: AppThemeMode) {
        appPreferences.updateData {
            it.copy {
                this.appThemeMode = appThemeMode.ordinal
            }
        }
    }
}
