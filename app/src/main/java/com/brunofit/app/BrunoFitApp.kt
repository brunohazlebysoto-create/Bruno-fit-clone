package com.brunofit.app

import android.app.Application
import com.brunofit.app.data.local.AppDatabase
import com.brunofit.app.data.prefs.AppPreferences
import com.brunofit.app.data.repository.MetricsRepository
import com.brunofit.app.data.repository.NutritionRepository
import com.brunofit.app.data.repository.WorkoutRepository

class BrunoFitApp : Application() {
    val db by lazy { AppDatabase.getInstance(this) }
    val prefs by lazy { AppPreferences.getInstance(this) }
    val nutritionRepo by lazy { NutritionRepository(db.nutritionDao()) }
    val workoutRepo by lazy { WorkoutRepository(db.workoutDao()) }
    val metricsRepo by lazy { MetricsRepository(db.metricsDao()) }
}
