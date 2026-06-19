package com.brunofit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunofit.app.data.prefs.AppPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileState(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val userEmail: String = "",
    val isLoggedIn: Boolean = false,
    val targetKcal: Int = 2600,
    val targetProtein: Int = 220,
    val targetCarbs: Int = 265,
    val targetFat: Int = 70,
    val presetKey: String = "definicion",
    val activeSplit: String = "A",
    val message: String = ""
)

class ProfileViewModel(private val prefs: AppPreferences) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init {
        viewModelScope.launch {
            combine(
                prefs.supabaseUrl, prefs.supabaseKey, prefs.userEmail,
                prefs.isLoggedIn, prefs.presetKey
            ) { arr ->
                arr
            }.launchIn(viewModelScope)

            prefs.supabaseUrl.combine(prefs.supabaseKey) { url, key -> url to key }
                .combine(prefs.userEmail) { (url, key), email -> Triple(url, key, email) }
                .combine(prefs.isLoggedIn) { (url, key, email), loggedIn -> Quad(url, key, email, loggedIn) }
                .collect { (url, key, email, loggedIn) ->
                    _state.update { it.copy(supabaseUrl = url, supabaseKey = key, userEmail = email, isLoggedIn = loggedIn) }
                }
        }
        viewModelScope.launch {
            combine(prefs.targetKcal, prefs.targetProtein, prefs.targetCarbs, prefs.targetFat)
            { kcal, prot, carbs, fat ->
                _state.update { it.copy(targetKcal = kcal, targetProtein = prot, targetCarbs = carbs, targetFat = fat) }
            }.launchIn(viewModelScope)
        }
        viewModelScope.launch {
            prefs.presetKey.collect { key ->
                _state.update { it.copy(presetKey = key) }
            }
        }
        viewModelScope.launch {
            prefs.activeSplit.collect { split ->
                _state.update { it.copy(activeSplit = split) }
            }
        }
    }

    fun saveTargets(kcal: Int, protein: Int, carbs: Int, fat: Int) {
        viewModelScope.launch {
            prefs.saveInt(AppPreferences.TARGET_KCAL, kcal)
            prefs.saveInt(AppPreferences.TARGET_PROTEIN, protein)
            prefs.saveInt(AppPreferences.TARGET_CARBS, carbs)
            prefs.saveInt(AppPreferences.TARGET_FAT, fat)
            _state.update { it.copy(message = "Objetivos guardados") }
        }
    }

    fun setPreset(key: String) {
        val (kcal, prot, carbs, fat) = when (key) {
            "definicion" -> listOf(2600, 220, 265, 70)
            "mantenimiento" -> listOf(3000, 200, 360, 85)
            "volumen" -> listOf(3400, 200, 450, 90)
            else -> listOf(2600, 220, 265, 70)
        }
        viewModelScope.launch {
            prefs.saveString(AppPreferences.PRESET_KEY, key)
            saveTargets(kcal, prot, carbs, fat)
            _state.update { it.copy(presetKey = key) }
        }
    }

    fun setSplit(split: String) {
        viewModelScope.launch {
            prefs.saveString(AppPreferences.ACTIVE_SPLIT, split)
            _state.update { it.copy(activeSplit = split) }
        }
    }

    fun clearMessage() = _state.update { it.copy(message = "") }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
private operator fun <A, B, C, D> Quad<A, B, C, D>.component1() = first
private operator fun <A, B, C, D> Quad<A, B, C, D>.component2() = second
private operator fun <A, B, C, D> Quad<A, B, C, D>.component3() = third
private operator fun <A, B, C, D> Quad<A, B, C, D>.component4() = fourth
