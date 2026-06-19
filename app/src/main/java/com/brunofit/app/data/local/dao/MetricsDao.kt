package com.brunofit.app.data.local.dao

import androidx.room.*
import com.brunofit.app.data.local.entity.MetricsLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricsDao {
    @Query("SELECT * FROM metrics_logs WHERE date = :date")
    suspend fun getByDate(date: String): MetricsLogEntity?

    @Query("SELECT * FROM metrics_logs ORDER BY date DESC")
    fun getAll(): Flow<List<MetricsLogEntity>>

    @Query("SELECT * FROM metrics_logs ORDER BY date DESC LIMIT 60")
    suspend fun getRecent(): List<MetricsLogEntity>

    @Upsert
    suspend fun upsert(entity: MetricsLogEntity)

    @Query("DELETE FROM metrics_logs")
    suspend fun deleteAll()
}
