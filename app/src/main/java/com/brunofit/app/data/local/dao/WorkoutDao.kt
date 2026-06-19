package com.brunofit.app.data.local.dao

import androidx.room.*
import com.brunofit.app.data.local.entity.WorkoutLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_logs WHERE date = :date ORDER BY exercise_name ASC")
    fun getByDate(date: String): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs ORDER BY date DESC")
    fun getAll(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT DISTINCT exercise_name FROM workout_logs ORDER BY exercise_name ASC")
    suspend fun getExerciseNames(): List<String>

    @Upsert
    suspend fun upsert(entity: WorkoutLogEntity)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM workout_logs WHERE date = :date AND exercise_name = :name")
    suspend fun deleteByDateAndName(date: String, name: String)

    @Query("DELETE FROM workout_logs")
    suspend fun deleteAll()
}
