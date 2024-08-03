package com.app.skydev.fmt.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.skydev.fmt.data.repository.SettingsRepository
import com.app.skydev.fmt.ui.theme.ColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val _currentColorScheme = MutableStateFlow(ColorScheme.DEFAULT)
    val currentColorScheme: StateFlow<ColorScheme> = _currentColorScheme

    init {
        viewModelScope.launch {
            settingsRepository.isDarkThemeFlow().collect{theme->
                _isDarkTheme.value = theme
            }
             settingsRepository.getColorSchemeFlow().collect{items->
                 _currentColorScheme.value = items
            }
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkTheme(isDark)
            _isDarkTheme.value = isDark
        }
    }

    fun setColorScheme(scheme: ColorScheme) {
        viewModelScope.launch {
            settingsRepository.setColorScheme(scheme)
            _currentColorScheme.value = scheme
        }
    }
}