package com.app.skydev.fmt

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.app.skydev.fmt.data.repository.SettingsRepository
import com.app.skydev.fmt.ui.theme.ColorScheme
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    var isDarkTheme by mutableStateOf(false)
    var colorScheme by mutableStateOf(ColorScheme.DEFAULT)

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            settingsRepository.isDarkThemeFlow().collect { isDark ->
                isDarkTheme = isDark
            }
        }

        applicationScope.launch {
            settingsRepository.getColorSchemeFlow().collect { scheme ->
                colorScheme = scheme
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}