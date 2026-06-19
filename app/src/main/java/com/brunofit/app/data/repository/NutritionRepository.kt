package com.brunofit.app.data.repository

import com.brunofit.app.data.local.dao.NutritionDao
import com.brunofit.app.data.local.entity.NutritionLogEntity
import com.brunofit.app.data.model.FoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NutritionRepository(private val dao: NutritionDao) {

    fun getAll(): Flow<List<NutritionLogEntity>> = dao.getAll()

    suspend fun getByDate(date: String): NutritionLogEntity? = dao.getByDate(date)

    suspend fun save(entity: NutritionLogEntity) = dao.upsert(entity)

    suspend fun addFoodItem(date: String, item: FoodItem, userId: String = "") {
        val existing = dao.getByDate(date)
        val items = if (existing != null) {
            Json.decodeFromString<List<FoodItem>>(existing.foodItemsJson) + item
        } else {
            listOf(item)
        }
        val kcal = items.sumOf { it.kcal }
        val prot = items.sumOf { it.proteina }
        val carbo = items.sumOf { it.carbo }
        val grasa = items.sumOf { it.grasa }
        dao.upsert(
            (existing ?: NutritionLogEntity(date = date, userId = userId)).copy(
                kcal = kcal, proteina = prot, carbo = carbo, grasa = grasa,
                foodItemsJson = Json.encodeToString(items),
                userId = userId.ifEmpty { existing?.userId ?: "" }
            )
        )
    }

    suspend fun removeFoodItem(date: String, itemId: String) {
        val existing = dao.getByDate(date) ?: return
        val items = Json.decodeFromString<List<FoodItem>>(existing.foodItemsJson)
            .filter { it.id != itemId }
        dao.upsert(
            existing.copy(
                kcal = items.sumOf { it.kcal },
                proteina = items.sumOf { it.proteina },
                carbo = items.sumOf { it.carbo },
                grasa = items.sumOf { it.grasa },
                foodItemsJson = Json.encodeToString(items)
            )
        )
    }

    suspend fun updateWater(date: String, water: Double, userId: String = "") {
        val existing = dao.getByDate(date)
        dao.upsert(
            (existing ?: NutritionLogEntity(date = date, userId = userId)).copy(water = water)
        )
    }

    suspend fun getFoodItems(date: String): List<FoodItem> {
        val entity = dao.getByDate(date) ?: return emptyList()
        return try { Json.decodeFromString(entity.foodItemsJson) } catch (_: Exception) { emptyList() }
    }
}
