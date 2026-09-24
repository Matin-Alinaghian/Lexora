package com.lexora.app.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lexora.app.ui.navigation.Screen
import com.lexora.app.ui.screens.*
import com.lexora.app.ui.theme.*
import com.lexora.app.utils.LocalSoundManager
import com.lexora.app.utils.SoundManager

@Composable
private fun themeContainerColor(): Color {
    return if (ThemeManager.getTheme() == ThemeType.LIGHT) LightCard else DarkCard
}

@Composable
private fun themeBarColor(): Color {
    return if (ThemeManager.getTheme() == ThemeType.LIGHT) LightSurface else DarkCard
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LexoraMainContainer() {
    val navController = rememberNavController()
    val soundManager = LocalSoundManager.current

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Screen.Learn, "Learn", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook),
        BottomNavItem(Screen.Add, "Add", Icons.Filled.AddCircle, Icons.Outlined.AddCircle),
        BottomNavItem(Screen.Statistics, "Stats", Icons.Filled.BarChart, Icons.Outlined.BarChart),
        BottomNavItem(Screen.Settings, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route }

    val currentTheme = ThemeManager.getTheme()
    val scaffoldBg = if (currentTheme == ThemeType.LIGHT) LightBackground else DarkBackground
    val navBarBg = if (currentTheme == ThemeType.LIGHT) LightSurface else DarkCard

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = scaffoldBg,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = navBarBg,
                    contentColor = if (currentTheme == ThemeType.LIGHT) Color(0xFF1A1C1E) else TextPrimary,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                soundManager.playSound(SoundManager.SoundType.NAV_TAB)
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (currentTheme == ThemeType.LIGHT) CoffeeMocha else WarmOrange,
                                selectedTextColor = if (currentTheme == ThemeType.LIGHT) CoffeeMocha else WarmOrange,
                                unselectedIconColor = if (currentTheme == ThemeType.LIGHT) CoffeeBronze.copy(alpha = 0.6f) else TextTertiary,
                                unselectedTextColor = if (currentTheme == ThemeType.LIGHT) CoffeeBronze.copy(alpha = 0.6f) else TextTertiary,
                                indicatorColor = (if (currentTheme == ThemeType.LIGHT) CoffeeMocha else WarmOrange).copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(WindowInsets.systemBars),
            enterTransition = {
                fadeIn(animationSpec = tween(100)) + slideInHorizontally(
                    initialOffsetX = { 20 },
                    animationSpec = tween(100)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(80))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(100)) + slideInHorizontally(
                    initialOffsetX = { -20 },
                    animationSpec = tween(100)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(80))
            }
        ) {
                        composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Learn.route) {
                LearnScreen(navController = navController)
            }
            composable(Screen.Add.route) {
                AddScreen(navController = navController)
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }

                        composable(Screen.Vocabulary.route) {
                VocabularyScreen(navController = navController)
            }
            composable(Screen.Grammar.route) {
                GrammarScreen(navController = navController)
            }
            composable(Screen.Notes.route) {
                NotesScreen(navController = navController)
            }
            composable(Screen.Mistakes.route) {
                MistakesScreen(navController = navController)
            }

                        composable(
                route = Screen.WordDetail.route,
                arguments = listOf(navArgument("wordId") { type = NavType.LongType })
            ) {
                WordDetailScreen(navController = navController)
            }
            composable(
                route = Screen.GrammarDetail.route,
                arguments = listOf(navArgument("grammarId") { type = NavType.LongType })
            ) {
                GrammarDetailScreen(navController = navController)
            }
            composable(
                route = Screen.NoteDetail.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) {
                NoteDetailScreen(navController = navController)
            }

                        composable(Screen.AddWord.route) {
                AddEditWordScreen(navController = navController)
            }
            composable(
                route = Screen.EditWord.route,
                arguments = listOf(navArgument("wordId") { type = NavType.LongType })
            ) {
                AddEditWordScreen(navController = navController)
            }
            composable(Screen.AddGrammar.route) {
                AddEditGrammarScreen(navController = navController)
            }
            composable(
                route = Screen.EditGrammar.route,
                arguments = listOf(navArgument("grammarId") { type = NavType.LongType })
            ) {
                AddEditGrammarScreen(navController = navController)
            }
            composable(Screen.AddNote.route) {
                AddEditNoteScreen(navController = navController)
            }
            composable(
                route = Screen.EditNote.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) {
                AddEditNoteScreen(navController = navController)
            }

            composable(Screen.Dictionary.route) {
                DictionaryScreen(navController = navController)
            }
            composable(Screen.Quiz.route) {
                QuizScreen(navController = navController)
            }
            composable(
                route = Screen.LeitnerReview.route + "?mode={mode}",
                arguments = listOf(navArgument("mode") { defaultValue = "all" })
            ) {
                LeitnerReviewScreen(navController = navController)
            }

                        composable(Screen.Favorites.route) {
                FavoritesScreen(navController = navController)
            }
            composable(Screen.Search.route) {
                SearchScreen(navController = navController)
            }
        }
    }
}
