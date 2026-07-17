package com.exercisedb.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exercisedb.app.data.ThemeMode
import com.exercisedb.app.presentation.ExerciseDbApp
import com.exercisedb.app.presentation.MainViewModel
import com.exercisedb.app.ui.theme.ExerciseDbTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        val container = (application as ExerciseDbApplication).container
        MainViewModel.Factory(
            exerciseRepository = container.exerciseRepository,
            preferencesRepository = container.userPreferencesRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (state.themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            ExerciseDbTheme(darkTheme = darkTheme) {
                ExerciseDbApp(state = state, viewModel = viewModel)
            }
        }
    }
}
