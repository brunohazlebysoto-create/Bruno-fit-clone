package com.brunofit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunofit.app.data.local.entity.NutritionLogEntity
import com.brunofit.app.data.model.FoodItem
import com.brunofit.app.data.prefs.AppPreferences
import com.brunofit.app.data.repository.NutritionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

data class NutritionState(
    val date: LocalDate = LocalDate.now(),
    val entity: NutritionLogEntity? = null,
    val foodItems: List<FoodItem> = emptyList(),
    val showAddFood: Boolean = false,
    val draftName: String = "",
    val draftKcal: String = "",
    val draftProtein: String = "",
    val draftCarbs: String = "",
    val draftFat: String = "",
    val draftQty: String = "1",
    val targetKcal: Int = 2600,
    val targetProtein: Int = 220,
    val targetCarbs: Int = 265,
    val targetFat: Int = 70
)

class NutritionViewModel(
    private val repo: NutritionRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionState())
    val state: StateFlow<NutritionState> = _state
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        loadTargets()
        loadDay(LocalDate.now())
    }

    private fun loadTargets() {
        viewModelScope.launch {
            combine(prefs.targetKcal, prefs.targetProtein, prefs.targetCarbs, prefs.targetFat)
            { kcal, prot, carbs, fat -> _state.update { it.copy(targetKcal = kcal, targetProtein = prot, targetCarbs = carbs, targetFat = fat) } }
                .launchIn(viewModelScope)
        }
    }

    fun loadDay(date: LocalDate) {
        _state.update { it.copy(date = date) }
        viewModelScope.launch {
            val entity = repo.getByDate(date.format(fmt))
            val items = if (entity != null) {
                try { Json.decodeFromString<List<FoodItem>>(entity.foodItemsJson) } catch (_: Exception) { emptyList() }
            } else emptyList()
            _state.update { it.copy(entity = entity, foodItems = items) }
        }
    }

    fun previousDay() = loadDay(_state.value.date.minusDays(1))
    fun nextDay() = loadDay(_state.value.date.plusDays(1))
    fun showAddFood() = _state.update { it.copy(showAddFood = true, draftName = "", draftKcal = "", draftProtein = "", draftCarbs = "", draftFat = "", draftQty = "1") }
    fun hideAddFood() = _state.update { it.copy(showAddFood = false) }

    fun onNameChange(v: String) = _state.update { it.copy(draftName = v) }
    fun onKcalChange(v: String) = _state.update { it.copy(draftKcal = v) }
    fun onProteinChange(v: String) = _state.update { it.copy(draftProtein = v) }
    fun onCarbsChange(v: String) = _state.update { it.copy(draftCarbs = v) }
    fun onFatChange(v: String) = _state.update { it.copy(draftFat = v) }
    fun onQtyChange(v: String) = _state.update { it.copy(draftQty = v) }

    fun addFood() {
        val s = _state.value
        if (s.draftName.isBlank()) return
        val qty = s.draftQty.toDoubleOrNull() ?: 1.0
        val item = FoodItem(
            id = UUID.randomUUID().toString(),
            name = s.draftName.trim(),
            kcal = (s.draftKcal.toDoubleOrNull() ?: 0.0) * qty,
            proteina = (s.draftProtein.toDoubleOrNull() ?: 0.0) * qty,
            carbo = (s.draftCarbs.toDoubleOrNull() ?: 0.0) * qty,
            grasa = (s.draftFat.toDoubleOrNull() ?: 0.0) * qty,
            qty = qty
        )
        viewModelScope.launch {
            repo.addFoodItem(s.date.format(fmt), item)
            loadDay(s.date)
            _state.update { it.copy(showAddFood = false) }
        }
    }

    fun removeFood(itemId: String) {
        viewModelScope.launch {
            repo.removeFoodItem(_state.value.date.format(fmt), itemId)
            loadDay(_state.value.date)
        }
    }

    fun updateWater(water: Double) {
        viewModelScope.launch {
            repo.updateWater(_state.value.date.format(fmt), water)
            loadDay(_state.value.date)
        }
    }
}
