package com.exercisedb.app.presentation

import com.exercisedb.app.domain.Exercise

class ExercisePresenter {
    fun card(exercise: Exercise, isFavorite: Boolean) = ExerciseCardUi(
        id = exercise.id,
        name = exercise.name.toTitleCase(),
        imageUrl = exercise.imageUrl,
        bodyPart = exercise.bodyPart.toTitleCase(),
        equipment = exercise.equipment.toTitleCase(),
        target = exercise.target.toTitleCase(),
        isFavorite = isFavorite,
    )

    fun detail(exercise: Exercise, language: String, isFavorite: Boolean) = ExerciseDetailUi(
        id = exercise.id,
        name = exercise.name.toTitleCase(),
        gifUrl = exercise.gifUrl,
        bodyPart = exercise.bodyPart.toTitleCase(),
        equipment = exercise.equipment.toTitleCase(),
        target = exercise.target.toTitleCase(),
        muscleGroup = exercise.muscleGroup.toTitleCase(),
        secondaryMuscles = exercise.secondaryMuscles.map { it.toTitleCase() },
        instruction = exercise.instructions.get(language),
        steps = exercise.instructionSteps.get(language),
        attribution = exercise.attribution,
        isFavorite = isFavorite,
    )

    fun strings(language: String): UiStrings = uiStrings[language] ?: uiStrings.getValue("en")

    private fun String.toTitleCase() = replaceFirstChar { it.titlecase() }
}

data class ExerciseCardUi(
    val id: String,
    val name: String,
    val imageUrl: String,
    val bodyPart: String,
    val equipment: String,
    val target: String,
    val isFavorite: Boolean,
)

data class ExerciseDetailUi(
    val id: String,
    val name: String,
    val gifUrl: String,
    val bodyPart: String,
    val equipment: String,
    val target: String,
    val muscleGroup: String,
    val secondaryMuscles: List<String>,
    val instruction: String,
    val steps: List<String>,
    val attribution: String,
    val isFavorite: Boolean,
)

data class UiStrings(
    val explore: String,
    val favorites: String,
    val settings: String,
    val searchHint: String,
    val all: String,
    val noResults: String,
    val noFavorites: String,
    val language: String,
    val appearance: String,
    val system: String,
    val light: String,
    val dark: String,
    val target: String,
    val equipment: String,
    val bodyPart: String,
    val muscleGroup: String,
    val secondaryMuscles: String,
    val instructions: String,
    val retry: String,
    val mediaNotice: String,
)

private val uiStrings = mapOf(
    "en" to UiStrings("Explore", "Favorites", "Settings", "Search exercises", "All", "No exercises found", "Save exercises to find them here", "Language", "Appearance", "System", "Light", "Dark", "Target", "Equipment", "Body part", "Muscle group", "Secondary muscles", "Instructions", "Retry", "Media is loaded from GitHub and cached on this device."),
    "es" to UiStrings("Explorar", "Favoritos", "Ajustes", "Buscar ejercicios", "Todos", "No se encontraron ejercicios", "Guarda ejercicios para encontrarlos aquí", "Idioma", "Apariencia", "Sistema", "Claro", "Oscuro", "Objetivo", "Equipo", "Parte del cuerpo", "Grupo muscular", "Músculos secundarios", "Instrucciones", "Reintentar", "Los medios se cargan desde GitHub y se almacenan en caché."),
    "it" to UiStrings("Esplora", "Preferiti", "Impostazioni", "Cerca esercizi", "Tutti", "Nessun esercizio trovato", "Salva gli esercizi per trovarli qui", "Lingua", "Aspetto", "Sistema", "Chiaro", "Scuro", "Obiettivo", "Attrezzatura", "Parte del corpo", "Gruppo muscolare", "Muscoli secondari", "Istruzioni", "Riprova", "I media vengono caricati da GitHub e memorizzati nella cache."),
    "tr" to UiStrings("Keşfet", "Favoriler", "Ayarlar", "Egzersiz ara", "Tümü", "Egzersiz bulunamadı", "Egzersizleri buraya kaydedin", "Dil", "Görünüm", "Sistem", "Açık", "Koyu", "Hedef", "Ekipman", "Vücut bölgesi", "Kas grubu", "İkincil kaslar", "Talimatlar", "Tekrar dene", "Medya GitHub'dan yüklenir ve cihazda önbelleğe alınır."),
    "ru" to UiStrings("Обзор", "Избранное", "Настройки", "Поиск упражнений", "Все", "Упражнения не найдены", "Сохраняйте упражнения здесь", "Язык", "Оформление", "Система", "Светлая", "Тёмная", "Цель", "Оборудование", "Часть тела", "Группа мышц", "Вторичные мышцы", "Инструкции", "Повторить", "Медиа загружаются с GitHub и кэшируются на устройстве."),
    "zh" to UiStrings("探索", "收藏", "设置", "搜索动作", "全部", "未找到动作", "收藏动作后会显示在这里", "语言", "外观", "跟随系统", "浅色", "深色", "目标", "器材", "身体部位", "肌群", "辅助肌群", "说明", "重试", "媒体从 GitHub 加载并缓存在设备上。"),
    "hi" to UiStrings("खोजें", "पसंदीदा", "सेटिंग्स", "व्यायाम खोजें", "सभी", "कोई व्यायाम नहीं मिला", "व्यायाम सहेजें और उन्हें यहाँ पाएँ", "भाषा", "दिखावट", "सिस्टम", "हल्का", "गहरा", "लक्ष्य", "उपकरण", "शरीर का भाग", "मांसपेशी समूह", "सहायक मांसपेशियाँ", "निर्देश", "पुनः प्रयास", "मीडिया GitHub से लोड होकर डिवाइस पर कैश होता है।"),
    "pl" to UiStrings("Odkrywaj", "Ulubione", "Ustawienia", "Szukaj ćwiczeń", "Wszystkie", "Nie znaleziono ćwiczeń", "Zapisz ćwiczenia, aby znaleźć je tutaj", "Język", "Wygląd", "System", "Jasny", "Ciemny", "Cel", "Sprzęt", "Część ciała", "Grupa mięśni", "Mięśnie dodatkowe", "Instrukcje", "Ponów", "Multimedia są pobierane z GitHub i zapisywane w pamięci podręcznej."),
    "ko" to UiStrings("탐색", "즐겨찾기", "설정", "운동 검색", "전체", "운동을 찾을 수 없습니다", "운동을 저장하면 여기에 표시됩니다", "언어", "화면 모드", "시스템", "라이트", "다크", "목표", "장비", "신체 부위", "근육군", "보조 근육", "운동 방법", "다시 시도", "미디어는 GitHub에서 로드되어 기기에 캐시됩니다."),
    "fr" to UiStrings("Explorer", "Favoris", "Réglages", "Rechercher des exercices", "Tous", "Aucun exercice trouvé", "Enregistrez des exercices pour les retrouver ici", "Langue", "Apparence", "Système", "Clair", "Sombre", "Cible", "Équipement", "Partie du corps", "Groupe musculaire", "Muscles secondaires", "Instructions", "Réessayer", "Les médias sont chargés depuis GitHub et mis en cache sur l’appareil."),
)
