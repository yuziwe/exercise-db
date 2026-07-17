package com.exercisedb.app

import android.content.Context
import com.exercisedb.app.data.AssetExerciseRepository
import com.exercisedb.app.data.ExerciseRepository
import com.exercisedb.app.data.UserPreferencesRepository

class AppContainer(context: Context) {
    val exerciseRepository: ExerciseRepository = AssetExerciseRepository(context)
    val userPreferencesRepository = UserPreferencesRepository(context)
}
