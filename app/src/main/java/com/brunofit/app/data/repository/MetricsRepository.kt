package com.brunofit.app.data.repository

import com.brunofit.app.data.local.dao.MetricsDao
import com.brunofit.app.data.local.entity.MetricsLogEntity
import kotlinx.coroutines.flow.Flow

class MetricsRepository(private val dao: MetricsDao) {
    fun getAll(): Flow<List<MetricsLogEntity>> = dao.getAll()
    suspend fun getByDate(date: String): MetricsLogEntity? = dao.getByDate(date)
    suspend fun save(entity: MetricsLogEntity) = dao.upsert(entity)
    suspend fun getRecent(): List<MetricsLogEntity> = dao.getRecent()
}
