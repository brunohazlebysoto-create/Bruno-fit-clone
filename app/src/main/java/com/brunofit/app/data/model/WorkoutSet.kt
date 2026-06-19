package com.brunofit.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkoutSet(
    val id: String = "",
    val reps: Int = 0,
    val kg: Double = 0.0,
    val date: String = "",
    val note: String = ""
)
