package com.dogeby.tagplayer.domain.preferences.videolist

import com.dogeby.tagplayer.data.preferences.PreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class AddDirectoryFilterUseCase @Inject constructor(
    private val getFilteredDirectoryNameUseCase: GetFilteredDirectoryNameUseCase,
    private val preferencesRepository: PreferencesRepository,
) {

    suspend operator fun invoke(directoryName: String) {
        val filteredDirectory = getFilteredDirectoryNameUseCase().first()
        preferencesRepository.setDirectoryFilter(filteredDirectory + directoryName)
    }
}
