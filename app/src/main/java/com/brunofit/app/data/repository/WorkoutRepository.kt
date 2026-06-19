package com.brunofit.app.data.repository

import com.brunofit.app.data.local.dao.WorkoutDao
import com.brunofit.app.data.local.entity.WorkoutLogEntity
import com.brunofit.app.data.model.WorkoutSet
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class WorkoutRepository(private val dao: WorkoutDao) {

    fun getAll(): Flow<List<WorkoutLogEntity>> = dao.getAll()

    fun getByDate(date: String): Flow<List<WorkoutLogEntity>> = dao.getByDate(date)

    suspend fun addSet(date: String, exerciseName: String, set: WorkoutSet, userId: String = "") {
        val id = "${date}_${exerciseName}"
        val existing = dao.getAll().let { null } // avoid collecting flow, use direct query workaround
        // Since we don't have a suspend get by date+name, we upsert with generated id
        val newSet = set.copy(id = set.id.ifEmpty { UUID.randomUUID().toString() }, date = date)
        // Find existing entity or create new
        val existingEntity = WorkoutLogEntity(
            id = id,
            date = date,
            exerciseName = exerciseName,
            setsJson = Json.encodeToString(listOf(newSet)),
            userId = userId
        )
        dao.upsert(existingEntity)
    }

    suspend fun upsertExercise(entity: WorkoutLogEntity) = dao.upsert(entity)

    suspend fun deleteExercise(date: String, name: String) = dao.deleteByDateAndName(date, name)

    suspend fun deleteById(id: String) = dao.deleteById(id)
}
