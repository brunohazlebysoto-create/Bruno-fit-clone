package com.brunofit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunofit.app.data.local.entity.WorkoutLogEntity
import com.brunofit.app.data.model.WorkoutSet
import com.brunofit.app.data.model.EXERCISE_DB
import com.brunofit.app.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

data class WorkoutState(
    val date: LocalDate = LocalDate.now(),
    val exercises: List<WorkoutLogEntity> = emptyList(),
    val showAddExercise: Boolean = false,
    val exerciseSearch: String = "",
    val selectedExercise: String? = null,
    val draftReps: String = "10",
    val draftKg: String = "60",
    val draftNote: String = ""
)

class WorkoutViewModel(private val repo: WorkoutRepository) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutState())
    val state: StateFlow<WorkoutState> = _state
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    val filteredExercises: StateFlow<List<String>> = _state
        .map { s ->
            val q = s.exerciseSearch.lowercase()
            if (q.isEmpty()) EXERCISE_DB.map { it.name }
            else EXERCISE_DB.filter { it.name.lowercase().contains(q) || it.muscles.any { m -> m.lowercase().contains(q) } }.map { it.name }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), EXERCISE_DB.map { it.name })

    init { loadDay(LocalDate.now()) }

    fun loadDay(date: LocalDate) {
        _state.update { it.copy(date = date) }
        repo.getByDate(date.format(fmt))
            .onEach { list -> _state.update { it.copy(exercises = list) } }
            .launchIn(viewModelScope)
    }

    fun previousDay() = loadDay(_state.value.date.minusDays(1))
    fun nextDay() = loadDay(_state.value.date.plusDays(1))

    fun showAddExercise() = _state.update { it.copy(showAddExercise = true, exerciseSearch = "") }
    fun hideAddExercise() = _state.update { it.copy(showAddExercise = false, selectedExercise = null) }
    fun onSearchChange(q: String) = _state.update { it.copy(exerciseSearch = q) }
    fun selectExercise(name: String) = _state.update { it.copy(selectedExercise = name, showAddExercise = false) }
    fun onRepsChange(v: String) = _state.update { it.copy(draftReps = v) }
    fun onKgChange(v: String) = _state.update { it.copy(draftKg = v) }
    fun onNoteChange(v: String) = _state.update { it.copy(draftNote = v) }

    fun addSet() {
        val ex = _state.value.selectedExercise ?: return
        val date = _state.value.date.format(fmt)
        val reps = _state.value.draftReps.toIntOrNull() ?: 0
        val kg = _state.value.draftKg.toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            val id = "${date}_${ex}"
            val existing = _state.value.exercises.find { it.exerciseName == ex && it.date == date }
            val existingSets = if (existing != null) {
                try { Json.decodeFromString<List<WorkoutSet>>(existing.setsJson) } catch (_: Exception) { emptyList() }
            } else emptyList()
            val newSet = WorkoutSet(id = UUID.randomUUID().toString(), reps = reps, kg = kg, date = date, note = _state.value.draftNote)
            val updatedSets = existingSets + newSet
            repo.upsertExercise(
                WorkoutLogEntity(
                    id = id, date = date, exerciseName = ex,
                    setsJson = Json.encodeToString(updatedSets)
                )
            )
            _state.update { it.copy(selectedExercise = null, draftNote = "") }
        }
    }

    fun deleteExercise(date: String, name: String) {
        viewModelScope.launch { repo.deleteExercise(date, name) }
    }

    fun getSets(entity: WorkoutLogEntity): List<WorkoutSet> =
        try { Json.decodeFromString(entity.setsJson) } catch (_: Exception) { emptyList() }
}
