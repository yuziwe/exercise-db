package com.exercisedb.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class UserPreferences(
    val languageCode: String = "en",
    val favoriteIds: Set<String> = emptySet(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

class UserPreferencesRepository(private val context: Context) {
    val preferences: Flow<UserPreferences> = context.dataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map(::toUserPreferences)

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { it[LANGUAGE] = code }
    }

    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { preferences ->
            val favorites = preferences[FAVORITES].orEmpty().toMutableSet()
            if (!favorites.add(id)) favorites.remove(id)
            preferences[FAVORITES] = favorites
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[THEME_MODE] = mode.name }
    }

    private fun toUserPreferences(preferences: Preferences) = UserPreferences(
        languageCode = preferences[LANGUAGE] ?: "en",
        favoriteIds = preferences[FAVORITES].orEmpty(),
        themeMode = preferences[THEME_MODE]
            ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.SYSTEM,
    )

    private companion object {
        val LANGUAGE = stringPreferencesKey("language")
        val FAVORITES = stringSetPreferencesKey("favorites")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
