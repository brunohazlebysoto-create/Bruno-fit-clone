package com.brunofit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.brunofit.app.ui.AppNavHost
import com.brunofit.app.ui.theme.BrunoFitTheme
import com.brunofit.app.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    private val app get() = application as BrunoFitApp

    private val authVm: AuthViewModel by viewModels {
        factory { AuthViewModel(app.prefs) }
    }
    private val dashVm: DashboardViewModel by viewModels {
        factory { DashboardViewModel(app.nutritionRepo, app.workoutRepo, app.metricsRepo, app.prefs) }
    }
    private val workoutVm: WorkoutViewModel by viewModels {
        factory { WorkoutViewModel(app.workoutRepo) }
    }
    private val nutritionVm: NutritionViewModel by viewModels {
        factory { NutritionViewModel(app.nutritionRepo, app.prefs) }
    }
    private val profileVm: ProfileViewModel by viewModels {
        factory { ProfileViewModel(app.prefs) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrunoFitTheme {
                AppNavHost(
                    authVm = authVm,
                    dashVm = dashVm,
                    workoutVm = workoutVm,
                    nutritionVm = nutritionVm,
                    profileVm = profileVm
                )
            }
        }
    }

    private inline fun <VM : ViewModel> factory(crossinline create: () -> VM) =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = create() as T
        }
}
