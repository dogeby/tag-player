package com.dogeby.tagplayer.domain.preferences.app

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import com.dogeby.tagplayer.datastore.app.AppPreferencesData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAppPreferencesDataUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    operator fun invoke(): Flow<AppPreferencesData> {
        return preferencesRepository.appPreferencesData
    }
}
