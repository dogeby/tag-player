package com.dogeby.tagplayer.datastore.app

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class AppPreferencesDataSourceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var appPreferencesDataSource: AppPreferencesDataSource

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun clear() {
        runBlocking { appPreferencesDataSource.resetAppPreferences() }
    }

    @Test
    fun getAppThemeMode_default() = runTest {
        val data = appPreferencesDataSource.appPreferencesData.first()

        Assert.assertEquals(AppThemeMode.SYSTEM_SETTING, data.appThemeMode)
    }

    @Test
    fun setAppThemeMode() = runTest {
        val appThemeMode = AppThemeMode.DARK

        appPreferencesDataSource.setAppThemeMode(appThemeMode)
        val data = appPreferencesDataSource.appPreferencesData.first()

        Assert.assertEquals(appThemeMode, data.appThemeMode)
    }

    @Test
    fun getAutoRotation_default() = runTest {
        val data = appPreferencesDataSource.appPreferencesData.first()

        Assert.assertEquals(false, data.autoRotation)
    }

    @Test
    fun setAutoRotation() = runTest {
        val autoRotation = true

        appPreferencesDataSource.setAutoRotation(autoRotation)
        val data = appPreferencesDataSource.appPreferencesData.first()

        Assert.assertEquals(autoRotation, data.autoRotation)
    }
}
