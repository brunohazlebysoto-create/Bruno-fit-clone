package com.brunofit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey val id: String,
    val date: String = "",
    @ColumnInfo(name = "exercise_name") val exerciseName: String = "",
    val setsJson: String = "[]",
    val duration: Int = 0,
    val userId: String = "",
    val synced: Boolean = false
)
