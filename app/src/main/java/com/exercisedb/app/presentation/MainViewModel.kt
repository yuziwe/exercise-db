package com.exercisedb.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.exercisedb.app.data.ExerciseRepository
import com.exercisedb.app.data.ThemeMode
import com.exercisedb.app.data.UserPreferences
import com.exercisedb.app.data.UserPreferencesRepository
import com.exercisedb.app.domain.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Destination { EXPLORE, FAVORITES, SETTINGS }

sealed interface ExerciseLoad {
    data object Loading : ExerciseLoad
    data class Ready(val exercises: List<Exercise>) : ExerciseLoad
    data class Failed(val message: String) : ExerciseLoad
}

data class MainUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val destination: Destination = Destination.EXPLORE,
    val query: String = "",
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val cards: List<ExerciseCardUi> = emptyList(),
    val selectedExercise: ExerciseDetailUi? = null,
    val languageCode: String = "en",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val strings: UiStrings = ExercisePresenter().strings("en"),
)

class MainViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val presenter: ExercisePresenter = ExercisePresenter(),
) : ViewModel() {
    private val load = MutableStateFlow<ExerciseLoad>(ExerciseLoad.Loading)
    private val query = MutableStateFlow("")
    private val category = MutableStateFlow<String?>(null)
    private val destination = MutableStateFlow(Destination.EXPLORE)
    private val selectedId = MutableStateFlow<String?>(null)

    private data class ContentState(
        val load: ExerciseLoad,
        val preferences: UserPreferences,
        val query: String,
        val category: String?,
    )

    private val content = combine(
        load,
        preferencesRepository.preferences,
        query,
        category,
        ::ContentState,
    )

    val uiState: StateFlow<MainUiState> = combine(content, destination, selectedId) {
            contentState,
            currentDestination,
            currentSelectedId,
        ->
        buildState(contentState, currentDestination, currentSelectedId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    init {
        loadExercises()
    }

    fun loadExercises() {
        viewModelScope.launch {
            load.value = ExerciseLoad.Loading
            load.value = runCatching { exerciseRepository.getExercises() }
                .fold(
                    onSuccess = ExerciseLoad::Ready,
                    onFailure = { ExerciseLoad.Failed(it.message ?: "Unable to load exercises") },
                )
        }
    }

    fun setQuery(value: String) {
        query.value = value
    }

    fun selectCategory(value: String?) {
        category.value = value
    }

    fun navigate(value: Destination) {
        selectedId.value = null
        destination.value = value
    }

    fun openExercise(id: String) {
        selectedId.value = id
    }

    fun closeExercise() {
        selectedId.value = null
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch { preferencesRepository.toggleFavorite(id) }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch { preferencesRepository.setLanguage(code) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
    }

    private fun buildState(
        content: ContentState,
        destination: Destination,
        selectedId: String?,
    ): MainUiState {
        val preferences = content.preferences
        val strings = presenter.strings(preferences.languageCode)
        val result = content.load
        if (result !is ExerciseLoad.Ready) {
            return MainUiState(
                isLoading = result is ExerciseLoad.Loading,
                error = (result as? ExerciseLoad.Failed)?.message,
                destination = destination,
                query = content.query,
                selectedCategory = content.category,
                languageCode = preferences.languageCode,
                themeMode = preferences.themeMode,
                strings = strings,
            )
        }

        val normalizedQuery = content.query.trim()
        val filtered = result.exercises.asSequence()
            .filter { exercise ->
                destination != Destination.FAVORITES || exercise.id in preferences.favoriteIds
            }
            .filter { exercise ->
                content.category == null || exercise.bodyPart == content.category
            }
            .filter { exercise ->
                normalizedQuery.isEmpty() || listOf(
                    exercise.name,
                    exercise.target,
                    exercise.equipment,
                    exercise.bodyPart,
                ).any { it.contains(normalizedQuery, ignoreCase = true) }
            }
            .map { presenter.card(it, it.id in preferences.favoriteIds) }
            .toList()

        val selected = result.exercises.firstOrNull { it.id == selectedId }?.let {
            presenter.detail(it, preferences.languageCode, it.id in preferences.favoriteIds)
        }

        return MainUiState(
            isLoading = false,
            destination = destination,
            query = content.query,
            selectedCategory = content.category,
            categories = result.exercises.map(Exercise::bodyPart).distinct().sorted(),
            cards = filtered,
            selectedExercise = selected,
            languageCode = preferences.languageCode,
            themeMode = preferences.themeMode,
            strings = strings,
        )
    }

    class Factory(
        private val exerciseRepository: ExerciseRepository,
        private val preferencesRepository: UserPreferencesRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainViewModel(exerciseRepository, preferencesRepository) as T
    }
}
