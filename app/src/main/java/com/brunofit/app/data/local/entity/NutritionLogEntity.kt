package com.brunofit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_logs")
data class NutritionLogEntity(
    @PrimaryKey val date: String,
    val kcal: Double = 0.0,
    val proteina: Double = 0.0,
    val carbo: Double = 0.0,
    val grasa: Double = 0.0,
    val water: Double = 0.0,
    val foodItemsJson: String = "[]",
    val supplementsJson: String = "{}",
    val userId: String = "",
    val synced: Boolean = false
)
