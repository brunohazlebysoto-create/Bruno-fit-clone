package com.brunofit.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val id: String = "",
    val name: String = "",
    val kcal: Double = 0.0,
    val proteina: Double = 0.0,
    val carbo: Double = 0.0,
    val grasa: Double = 0.0,
    val qty: Double = 1.0,
    val unit: String = "g"
)
