package com.dogeby.tagplayer.domain.preferences.app

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import com.dogeby.tagplayer.datastore.app.AppThemeMode
import javax.inject.Inject

class SetAppThemeModeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    suspend operator fun invoke(appThemeMode: AppThemeMode) {
        preferencesRepository.setAppThemeMode(appThemeMode)
    }
}
