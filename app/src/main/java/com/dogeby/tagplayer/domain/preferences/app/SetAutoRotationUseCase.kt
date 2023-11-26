package com.dogeby.tagplayer.domain.preferences.app

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import javax.inject.Inject

class SetAutoRotationUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    suspend operator fun invoke(isAutoRotation: Boolean) {
        preferencesRepository.setAutoRotation(isAutoRotation)
    }
}
