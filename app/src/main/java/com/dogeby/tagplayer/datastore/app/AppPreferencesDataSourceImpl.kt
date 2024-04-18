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
    private val rejectedUpdateVersionCodeDefaultValue = 0

    override val appPreferencesData: Flow<AppPreferencesData> = appPreferences
        .data
        .map {
            AppPreferencesData(
                AppThemeMode.values().getOrElse(it.appThemeMode) {
                    appThemeModeDefaultValue
                },
                it.autoRotation,
                it.rejectedUpdateVersionCode,
            )
        }

    override suspend fun resetAppPreferences() {
        setAppThemeMode(appThemeModeDefaultValue)
        setAutoRotation(autoRotationDefaultValue)
        setRejectedUpdateVersionCode(rejectedUpdateVersionCodeDefaultValue)
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

    override suspend fun setRejectedUpdateVersionCode(versionCode: Int) {
        appPreferences.updateData {
            it.copy {
                this.rejectedUpdateVersionCode = versionCode
            }
        }
    }
}
