package com.exercisedb.app.data

import android.content.Context
import com.exercisedb.app.domain.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface ExerciseRepository {
    suspend fun getExercises(): List<Exercise>
}

class AssetExerciseRepository(
    private val context: Context,
    private val json: Json = Json { ignoreUnknownKeys = true },
) : ExerciseRepository {
    @Volatile
    private var cache: List<Exercise>? = null

    override suspend fun getExercises(): List<Exercise> = cache ?: withContext(Dispatchers.IO) {
        cache ?: context.assets.open(EXERCISES_FILE).bufferedReader().use { reader ->
            json.decodeFromString<List<Exercise>>(reader.readText()).also { cache = it }
        }
    }

    private companion object {
        const val EXERCISES_FILE = "exercises.json"
    }
}
