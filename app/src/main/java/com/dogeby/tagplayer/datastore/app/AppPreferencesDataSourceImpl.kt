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
    private val autoRotationDefaultValue = false

    override val appPreferencesData: Flow<AppPreferencesData> = appPreferences
        .data
        .map {
            AppPreferencesData(
                AppThemeMode.values().getOrElse(it.appThemeMode) {
                    appThemeModeDefaultValue
                },
                it.autoRotation,
            )
        }

    override suspend fun resetAppPreferences() {
        setAppThemeMode(appThemeModeDefaultValue)
        setAutoRotation(autoRotationDefaultValue)
    }

    override suspend fun setAppThemeMode(appThemeMode: AppThemeMode) {
        appPreferences.updateData {
            it.copy {
                this.appThemeMode = appThemeMode.ordinal
            }
        }
    }

    override suspend fun setAutoRotation(isAutoRotation: Boolean) {
        appPreferences.updateData {
            it.copy {
                this.autoRotation = isAutoRotation
            }
        }
    }
}
