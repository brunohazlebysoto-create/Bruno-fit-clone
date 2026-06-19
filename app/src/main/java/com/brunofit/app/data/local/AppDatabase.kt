package com.brunofit.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.brunofit.app.data.local.dao.MetricsDao
import com.brunofit.app.data.local.dao.NutritionDao
import com.brunofit.app.data.local.dao.WorkoutDao
import com.brunofit.app.data.local.entity.MetricsLogEntity
import com.brunofit.app.data.local.entity.NutritionLogEntity
import com.brunofit.app.data.local.entity.WorkoutLogEntity

@Database(
    entities = [NutritionLogEntity::class, WorkoutLogEntity::class, MetricsLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nutritionDao(): NutritionDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun metricsDao(): MetricsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "brunofit.db"
                ).build().also { INSTANCE = it }
            }
    }
}
