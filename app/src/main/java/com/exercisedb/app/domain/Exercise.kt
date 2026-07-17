package com.exercisedb.app.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val category: String,
    @SerialName("body_part") val bodyPart: String,
    val equipment: String,
    val instructions: LocalizedText,
    @SerialName("instruction_steps") val instructionSteps: LocalizedSteps,
    @SerialName("muscle_group") val muscleGroup: String,
    @SerialName("secondary_muscles") val secondaryMuscles: List<String>,
    val target: String,
    @SerialName("media_id") val mediaId: String,
    val image: String,
    @SerialName("gif_url") val gifPath: String,
    val attribution: String,
    @SerialName("created_at") val createdAt: String,
) {
    val imageUrl: String get() = "$MEDIA_BASE_URL$image"
    val gifUrl: String get() = "$MEDIA_BASE_URL$gifPath"

    companion object {
        const val MEDIA_BASE_URL =
            "https://raw.githubusercontent.com/hasaneyldrm/exercises-dataset/7455efae41b330c265e7cd4b78dfa848e7ce5ebd/"
    }
}

@Serializable
data class LocalizedText(
    val en: String = "",
    val es: String = "",
    val it: String = "",
    val tr: String = "",
    val ru: String = "",
    val zh: String = "",
    val hi: String = "",
    val pl: String = "",
    val ko: String = "",
    val fr: String = "",
) {
    fun get(language: String): String = when (language) {
        "es" -> es
        "it" -> it
        "tr" -> tr
        "ru" -> ru
        "zh" -> zh
        "hi" -> hi
        "pl" -> pl
        "ko" -> ko
        "fr" -> fr
        else -> en
    }.ifBlank { en }
}

@Serializable
data class LocalizedSteps(
    val en: List<String> = emptyList(),
    val es: List<String> = emptyList(),
    val it: List<String> = emptyList(),
    val tr: List<String> = emptyList(),
    val ru: List<String> = emptyList(),
    val zh: List<String> = emptyList(),
    val hi: List<String> = emptyList(),
    val pl: List<String> = emptyList(),
    val ko: List<String> = emptyList(),
    val fr: List<String> = emptyList(),
) {
    fun get(language: String): List<String> = when (language) {
        "es" -> es
        "it" -> it
        "tr" -> tr
        "ru" -> ru
        "zh" -> zh
        "hi" -> hi
        "pl" -> pl
        "ko" -> ko
        "fr" -> fr
        else -> en
    }.ifEmpty { en }
}

data class LanguageOption(val code: String, val label: String, val nativeLabel: String)

val supportedLanguages = listOf(
    LanguageOption("en", "English", "English"),
    LanguageOption("es", "Spanish", "Español"),
    LanguageOption("it", "Italian", "Italiano"),
    LanguageOption("tr", "Turkish", "Türkçe"),
    LanguageOption("ru", "Russian", "Русский"),
    LanguageOption("zh", "Chinese", "中文"),
    LanguageOption("hi", "Hindi", "हिन्दी"),
    LanguageOption("pl", "Polish", "Polski"),
    LanguageOption("ko", "Korean", "한국어"),
    LanguageOption("fr", "French", "Français"),
)
