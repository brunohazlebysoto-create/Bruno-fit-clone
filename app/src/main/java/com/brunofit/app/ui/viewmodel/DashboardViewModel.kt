package com.brunofit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunofit.app.data.local.entity.MetricsLogEntity
import com.brunofit.app.data.local.entity.NutritionLogEntity
import com.brunofit.app.data.local.entity.WorkoutLogEntity
import com.brunofit.app.data.model.FoodItem
import com.brunofit.app.data.prefs.AppPreferences
import com.brunofit.app.data.repository.MetricsRepository
import com.brunofit.app.data.repository.NutritionRepository
import com.brunofit.app.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DashboardState(
    val date: LocalDate = LocalDate.now(),
    val nutrition: NutritionLogEntity? = null,
    val foodItems: List<FoodItem> = emptyList(),
    val workouts: List<WorkoutLogEntity> = emptyList(),
    val metrics: MetricsLogEntity? = null,
    val targetKcal: Int = 2600,
    val targetProtein: Int = 220,
    val targetCarbs: Int = 265,
    val targetFat: Int = 70,
    val isLoading: Boolean = false
)

class DashboardViewModel(
    private val nutritionRepo: NutritionRepository,
    private val workoutRepo: WorkoutRepository,
    private val metricsRepo: MetricsRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        loadTargets()
        loadDay(LocalDate.now())
    }

    private fun loadTargets() {
        viewModelScope.launch {
            combine(
                prefs.targetKcal,
                prefs.targetProtein,
                prefs.targetCarbs,
                prefs.targetFat
            ) { kcal, prot, carbs, fat ->
                _state.update { it.copy(targetKcal = kcal, targetProtein = prot, targetCarbs = carbs, targetFat = fat) }
            }.launchIn(viewModelScope)
        }
    }

    fun loadDay(date: LocalDate) {
        val dateStr = date.format(fmt)
        viewModelScope.launch {
            _state.update { it.copy(date = date, isLoading = true) }
            val nutrition = nutritionRepo.getByDate(dateStr)
            val foodItems = if (nutrition != null) {
                try { Json.decodeFromString<List<FoodItem>>(nutrition.foodItemsJson) } catch (_: Exception) { emptyList() }
            } else emptyList()
            val metrics = metricsRepo.getByDate(dateStr)
            _state.update { it.copy(nutrition = nutrition, foodItems = foodItems, metrics = metrics, isLoading = false) }
        }
        workoutRepo.getByDate(dateStr)
            .onEach { workouts -> _state.update { it.copy(workouts = workouts) } }
            .launchIn(viewModelScope)
    }

    fun previousDay() = loadDay(_state.value.date.minusDays(1))
    fun nextDay() = loadDay(_state.value.date.plusDays(1))

    fun updateWater(water: Double) {
        viewModelScope.launch {
            nutritionRepo.updateWater(_state.value.date.format(fmt), water)
            loadDay(_state.value.date)
        }
    }

    fun saveMetrics(weight: Double, grasaPct: Double, musculo: Double, visceral: Int) {
        viewModelScope.launch {
            metricsRepo.save(
                MetricsLogEntity(
                    date = _state.value.date.format(fmt),
                    weight = weight,
                    musculo = musculo,
                    grasaPct = grasaPct,
                    visceral = visceral
                )
            )
            loadDay(_state.value.date)
        }
    }
}
