package com.brunofit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metrics_logs")
data class MetricsLogEntity(
    @PrimaryKey val date: String,
    val weight: Double = 0.0,
    val musculo: Double = 0.0,
    val grasaPct: Double = 0.0,
    val visceral: Int = 0,
    val cintura: Double = 0.0,
    val pecho: Double = 0.0,
    val userId: String = "",
    val synced: Boolean = false
)
