package com.brunofit.app.data.local.dao

import androidx.room.*
import com.brunofit.app.data.local.entity.NutritionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {
    @Query("SELECT * FROM nutrition_logs WHERE date = :date")
    suspend fun getByDate(date: String): NutritionLogEntity?

    @Query("SELECT * FROM nutrition_logs ORDER BY date DESC")
    fun getAll(): Flow<List<NutritionLogEntity>>

    @Query("SELECT * FROM nutrition_logs ORDER BY date DESC LIMIT 30")
    suspend fun getRecent(): List<NutritionLogEntity>

    @Upsert
    suspend fun upsert(entity: NutritionLogEntity)

    @Query("DELETE FROM nutrition_logs")
    suspend fun deleteAll()
}
