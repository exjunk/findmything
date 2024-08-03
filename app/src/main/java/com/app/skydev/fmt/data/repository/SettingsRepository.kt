package com.app.skydev.fmt.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.skydev.fmt.ui.theme.ColorScheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val isDarkThemeKey = booleanPreferencesKey("is_dark_theme")
    private val colorSchemeKey = stringPreferencesKey("color_scheme")

    fun isDarkThemeFlow(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[isDarkThemeKey] ?: false
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isDarkThemeKey] = isDark
        }
    }

    fun getColorSchemeFlow(): Flow<ColorScheme> {
        return context.dataStore.data.map { preferences ->
            ColorScheme.valueOf(preferences[colorSchemeKey] ?: ColorScheme.DEFAULT.name)
        }
    }

    suspend fun setColorScheme(scheme: ColorScheme) {
        context.dataStore.edit { preferences ->
            preferences[colorSchemeKey] = scheme.name
        }
    }
}