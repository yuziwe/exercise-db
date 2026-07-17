package com.exercisedb.app.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.exercisedb.app.data.ThemeMode
import com.exercisedb.app.domain.supportedLanguages
import com.exercisedb.app.ui.theme.extendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDbApp(state: MainUiState, viewModel: MainViewModel) {
    val exploreGridState = rememberLazyGridState()
    val favoritesGridState = rememberLazyGridState()

    state.selectedExercise?.let { detail ->
        BackHandler(onBack = { viewModel.closeExercise() })
        DetailScreen(
            detail = detail,
            strings = state.strings,
            onBack = viewModel::closeExercise,
            onFavorite = { viewModel.toggleFavorite(detail.id) },
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    when (state.destination) {
                        Destination.EXPLORE -> state.strings.explore
                        Destination.FAVORITES -> state.strings.favorites
                        Destination.SETTINGS -> state.strings.settings
                    },
                    fontWeight = FontWeight.Bold,
                )
            })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = state.destination == Destination.EXPLORE,
                    label = { Text(state.strings.explore, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    onClick = { viewModel.navigate(Destination.EXPLORE) },
                )
                NavigationBarItem(
                    selected = state.destination == Destination.FAVORITES,
                    label = { Text(state.strings.favorites, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    onClick = { viewModel.navigate(Destination.FAVORITES) },
                )
                NavigationBarItem(
                    selected = state.destination == Destination.SETTINGS,
                    label = { Text(state.strings.settings, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    onClick = { viewModel.navigate(Destination.SETTINGS) },
                )
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> ErrorState(
                    message = state.error,
                    retryLabel = state.strings.retry,
                    onRetry = viewModel::loadExercises,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.destination == Destination.SETTINGS -> SettingsScreen(
                    state = state,
                    onLanguage = viewModel::setLanguage,
                    onTheme = viewModel::setThemeMode,
                )

                else -> ExerciseListScreen(
                    state = state,
                    gridState = if (state.destination == Destination.FAVORITES) {
                        favoritesGridState
                    } else {
                        exploreGridState
                    },
                    onQuery = viewModel::setQuery,
                    onCategory = viewModel::selectCategory,
                    onOpen = viewModel::openExercise,
                    onFavorite = viewModel::toggleFavorite,
                )
            }
        }
    }
}


@Composable
private fun ExerciseListScreen(
    state: MainUiState,
    gridState: LazyGridState,
    onQuery: (String) -> Unit,
    onCategory: (String?) -> Unit,
    onOpen: (String) -> Unit,
    onFavorite: (String) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text(state.strings.searchHint) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = state.selectedCategory == null,
                    onClick = { onCategory(null) },
                    label = { Text(state.strings.all) },
                )
            }
            items(state.categories) { category ->
                FilterChip(
                    selected = state.selectedCategory == category,
                    onClick = { onCategory(category) },
                    label = { Text(category.replaceFirstChar { it.titlecase() }) },
                )
            }
        }
        if (state.cards.isEmpty()) {
            EmptyState(
                text = if (state.destination == Destination.FAVORITES) {
                    state.strings.noFavorites
                } else {
                    state.strings.noResults
                },
                modifier = Modifier.weight(1f),
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(164.dp),
                state = gridState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.cards, key = ExerciseCardUi::id) { exercise ->
                    ExerciseCard(exercise, onOpen, onFavorite)
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseCardUi,
    onOpen: (String) -> Unit,
    onFavorite: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onOpen(exercise.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Box {
            AsyncImage(
                model = exercise.imageUrl,
                contentDescription = exercise.name,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.2f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
            )
            FavoriteButton(
                favorite = exercise.isFavorite,
                onClick = { onFavorite(exercise.id) },
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
            )
        }
        Column(Modifier.padding(12.dp)) {
            Text(
                exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "${exercise.target} · ${exercise.equipment}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun FavoriteButton(favorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier.clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)),
    ) {
        Icon(
            imageVector = if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = if (favorite) MaterialTheme.extendedColors.favorite else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    detail: ExerciseDetailUi,
    strings: UiStrings,
    onBack: () -> Unit,
    onFavorite: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(detail.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            actions = { FavoriteButton(detail.isFavorite, onFavorite) },
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                AsyncImage(
                    model = detail.gifUrl,
                    contentDescription = detail.name,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Fit,
                )
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { AssistChip(onClick = {}, label = { Text(detail.bodyPart) }) }
                    item { AssistChip(onClick = {}, label = { Text(detail.target) }) }
                    item { AssistChip(onClick = {}, label = { Text(detail.equipment) }) }
                }
            }
            item { DetailFact(strings.target, detail.target) }
            item { DetailFact(strings.equipment, detail.equipment) }
            item { DetailFact(strings.bodyPart, detail.bodyPart) }
            item { DetailFact(strings.muscleGroup, detail.muscleGroup) }
            if (detail.secondaryMuscles.isNotEmpty()) {
                item { DetailFact(strings.secondaryMuscles, detail.secondaryMuscles.joinToString()) }
            }
            item {
                Text(strings.instructions, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                if (detail.steps.isNotEmpty()) {
                    detail.steps.forEachIndexed { index, step ->
                        Row(Modifier.padding(vertical = 7.dp)) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(step, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    Text(detail.instruction, style = MaterialTheme.typography.bodyLarge)
                }
            }
            item {
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                Text(detail.attribution, style = MaterialTheme.typography.labelMedium)
                Text(
                    strings.mediaNotice,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DetailFact(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SettingsScreen(
    state: MainUiState,
    onLanguage: (String) -> Unit,
    onTheme: (ThemeMode) -> Unit,
) {
    var languageMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val selectedLanguage = supportedLanguages.first { it.code == state.languageCode }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
    ) {
        item {
            Text(state.strings.language, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Box(Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { languageMenuExpanded = true },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(selectedLanguage.nativeLabel, fontWeight = FontWeight.Medium)
                            if (selectedLanguage.label != selectedLanguage.nativeLabel) {
                                Text(
                                    selectedLanguage.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Text("⌄", style = MaterialTheme.typography.titleLarge)
                    }
                }
                DropdownMenu(
                    expanded = languageMenuExpanded,
                    onDismissRequest = { languageMenuExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.92f),
                ) {
                    supportedLanguages.forEach { language ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(language.nativeLabel, fontWeight = FontWeight.Medium)
                                    if (language.label != language.nativeLabel) {
                                        Text(
                                            language.label,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            },
                            trailingIcon = {
                                if (language.code == state.languageCode) {
                                    Text("✓", color = MaterialTheme.colorScheme.primary)
                                }
                            },
                            onClick = {
                                languageMenuExpanded = false
                                onLanguage(language.code)
                            },
                        )
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(20.dp))
            Text(state.strings.appearance, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeChip(state.themeMode, ThemeMode.SYSTEM, state.strings.system, onTheme)
                ThemeChip(state.themeMode, ThemeMode.LIGHT, state.strings.light, onTheme)
                ThemeChip(state.themeMode, ThemeMode.DARK, state.strings.dark, onTheme)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ThemeChip(
    selected: ThemeMode,
    value: ThemeMode,
    label: String,
    onTheme: (ThemeMode) -> Unit,
) {
    FilterChip(selected = selected == value, onClick = { onTheme(value) }, label = { Text(label) })
}

@Composable
private fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(text, modifier = Modifier.padding(32.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorState(
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, color = MaterialTheme.colorScheme.error)
        TextButton(onClick = onRetry) { Text(retryLabel) }
    }
}
