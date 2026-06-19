package com.brunofit.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.brunofit.app.ui.screen.*
import com.brunofit.app.ui.theme.*
import com.brunofit.app.ui.viewmodel.*

sealed class NavTab(val route: String, val label: String, val icon: ImageVector) {
    object Today : NavTab("today", "Hoy", Icons.Default.Today)
    object Workout : NavTab("workout", "Entreno", Icons.Default.FitnessCenter)
    object Nutrition : NavTab("nutrition", "Nutrición", Icons.Default.Restaurant)
    object Profile : NavTab("profile", "Perfil", Icons.Default.Person)

    companion object { val all = listOf(Today, Workout, Nutrition, Profile) }
}

@Composable
fun AppNavHost(
    authVm: AuthViewModel,
    dashVm: DashboardViewModel,
    workoutVm: WorkoutViewModel,
    nutritionVm: NutritionViewModel,
    profileVm: ProfileViewModel
) {
    val authState by authVm.state.collectAsState()
    var currentTab by remember { mutableStateOf<NavTab>(NavTab.Today) }

    if (!authState.isLoggedIn && authState.supabaseConfigured) {
        AuthScreen(vm = authVm, onLoggedIn = {})
    } else if (!authState.supabaseConfigured) {
        AuthScreen(vm = authVm, onLoggedIn = {})
    } else {
        Scaffold(
            containerColor = BgDark,
            bottomBar = {
                NavigationBar(containerColor = PanelDark, contentColor = LimePrimary) {
                    NavTab.all.forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LimePrimary,
                                unselectedIconColor = MutedGreen,
                                selectedTextColor = LimePrimary,
                                unselectedTextColor = MutedGreen,
                                indicatorColor = LineDark
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .background(BgDark)
                    .padding(padding)
            ) {
                when (currentTab) {
                    NavTab.Today -> DashboardScreen(vm = dashVm)
                    NavTab.Workout -> WorkoutScreen(vm = workoutVm)
                    NavTab.Nutrition -> NutritionScreen(vm = nutritionVm)
                    NavTab.Profile -> ProfileScreen(profileVm = profileVm, authVm = authVm)
                }
            }
        }
    }
}
