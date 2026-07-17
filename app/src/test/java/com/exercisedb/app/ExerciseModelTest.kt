package com.exercisedb.app

import com.exercisedb.app.domain.Exercise
import com.exercisedb.app.presentation.ExercisePresenter
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseModelTest {
    @Test
    fun `bundled dataset contains all exercises and languages`() {
        val dataset = File("src/main/assets/exercises.json")
        val exercises = Json.decodeFromString<List<Exercise>>(dataset.readText())
        val languageCodes = listOf("en", "es", "it", "tr", "ru", "zh", "hi", "pl", "ko", "fr")

        assertEquals(1_324, exercises.size)
        assertEquals(exercises.size, exercises.map(Exercise::id).distinct().size)
        assertTrue(exercises.all { exercise ->
            languageCodes.all { exercise.instructions.get(it).isNotBlank() }
        })
    }

    @Test
    fun `dataset record decodes and creates remote media urls`() {
        val exercise = Json.decodeFromString<Exercise>(sampleExercise)

        assertEquals("0001", exercise.id)
        assertEquals(listOf("Step one"), exercise.instructionSteps.get("en"))
        assertTrue(exercise.imageUrl.endsWith("/images/0001-demo.jpg"))
        assertTrue(exercise.gifUrl.endsWith("/videos/0001-demo.gif"))
    }

    @Test
    fun `presenter uses selected instruction language`() {
        val exercise = Json.decodeFromString<Exercise>(sampleExercise)

        val detail = ExercisePresenter().detail(exercise, "fr", isFavorite = true)

        assertEquals(listOf("Étape un"), detail.steps)
        assertTrue(detail.isFavorite)
    }

    private companion object {
        val sampleExercise = """
            {
              "id":"0001",
              "name":"sample press",
              "category":"chest",
              "body_part":"chest",
              "equipment":"barbell",
              "instructions":{"en":"Step one","es":"Paso uno","it":"Passo uno","tr":"Birinci adım","ru":"Шаг один","zh":"第一步","hi":"पहला चरण","pl":"Krok pierwszy","ko":"첫 단계","fr":"Étape un"},
              "instruction_steps":{"en":["Step one"],"es":["Paso uno"],"it":["Passo uno"],"tr":["Birinci adım"],"ru":["Шаг один"],"zh":["第一步"],"hi":["पहला चरण"],"pl":["Krok pierwszy"],"ko":["첫 단계"],"fr":["Étape un"]},
              "muscle_group":"pectorals",
              "secondary_muscles":["triceps"],
              "target":"chest",
              "media_id":"demo",
              "image":"images/0001-demo.jpg",
              "gif_url":"videos/0001-demo.gif",
              "attribution":"© Gym visual",
              "created_at":"2026-01-01T00:00:00Z"
            }
        """.trimIndent()
    }
}
