package com.brunofit.app.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "brunofit_prefs")

class AppPreferences(private val context: Context) {
    companion object {
        val SUPABASE_URL = stringPreferencesKey("supabase_url")
        val SUPABASE_KEY = stringPreferencesKey("supabase_key")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val PRESET_KEY = stringPreferencesKey("preset_key")
        val ACTIVE_SPLIT = stringPreferencesKey("active_split")
        val TARGET_KCAL = intPreferencesKey("target_kcal")
        val TARGET_PROTEIN = intPreferencesKey("target_protein")
        val TARGET_CARBS = intPreferencesKey("target_carbs")
        val TARGET_FAT = intPreferencesKey("target_fat")

        @Volatile private var INSTANCE: AppPreferences? = null
        fun getInstance(context: Context): AppPreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(context.applicationContext).also { INSTANCE = it }
            }
    }

    val supabaseUrl: Flow<String> = context.dataStore.data.map { it[SUPABASE_URL] ?: "" }
    val supabaseKey: Flow<String> = context.dataStore.data.map { it[SUPABASE_KEY] ?: "" }
    val userId: Flow<String> = context.dataStore.data.map { it[USER_ID] ?: "" }
    val userEmail: Flow<String> = context.dataStore.data.map { it[USER_EMAIL] ?: "" }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val presetKey: Flow<String> = context.dataStore.data.map { it[PRESET_KEY] ?: "definicion" }
    val activeSplit: Flow<String> = context.dataStore.data.map { it[ACTIVE_SPLIT] ?: "A" }
    val targetKcal: Flow<Int> = context.dataStore.data.map { it[TARGET_KCAL] ?: 2600 }
    val targetProtein: Flow<Int> = context.dataStore.data.map { it[TARGET_PROTEIN] ?: 220 }
    val targetCarbs: Flow<Int> = context.dataStore.data.map { it[TARGET_CARBS] ?: 265 }
    val targetFat: Flow<Int> = context.dataStore.data.map { it[TARGET_FAT] ?: 70 }

    suspend fun saveString(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { it[key] = value }
    }

    suspend fun saveBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { it[key] = value }
    }

    suspend fun saveInt(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { it[key] = value }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
