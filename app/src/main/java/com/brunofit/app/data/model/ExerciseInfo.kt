package com.brunofit.app.data.model

data class ExerciseInfo(
    val name: String,
    val equipment: String,
    val muscles: List<String>
)

val EXERCISE_DB: List<ExerciseInfo> = listOf(
    // PECTORAL
    ExerciseInfo("Press banca", "peso libre", listOf("Pectoral", "Tríceps", "Deltoide ant.")),
    ExerciseInfo("Press inclinado mancuerna", "peso libre", listOf("Pectoral", "Deltoide ant.", "Tríceps")),
    ExerciseInfo("Press banca mancuerna", "peso libre", listOf("Pectoral", "Tríceps", "Deltoide ant.")),
    ExerciseInfo("Aperturas", "peso libre", listOf("Pectoral")),
    ExerciseInfo("Aperturas inclinadas", "peso libre", listOf("Pectoral", "Deltoide ant.")),
    ExerciseInfo("Cruces en polea", "polea", listOf("Pectoral")),
    ExerciseInfo("Pec deck", "máquina", listOf("Pectoral")),
    ExerciseInfo("Flexiones de brazos", "cuerpo libre", listOf("Pectoral", "Tríceps", "Deltoide ant.")),
    // ESPALDA
    ExerciseInfo("Dominadas / Jalón", "polea", listOf("Espalda", "Bíceps")),
    ExerciseInfo("Jalón polea", "polea", listOf("Espalda", "Bíceps")),
    ExerciseInfo("Remo barra", "peso libre", listOf("Espalda", "Bíceps")),
    ExerciseInfo("Remo mancuerna", "peso libre", listOf("Espalda", "Bíceps")),
    ExerciseInfo("Remo máquina", "máquina", listOf("Espalda")),
    ExerciseInfo("Remo en polea baja", "polea", listOf("Espalda", "Bíceps")),
    ExerciseInfo("Pullover polea", "polea", listOf("Espalda")),
    ExerciseInfo("Face pull", "polea", listOf("Deltoides", "Espalda")),
    ExerciseInfo("Peso muerto", "peso libre", listOf("Isquios", "Glúteos", "Espalda")),
    ExerciseInfo("Peso muerto rumano", "peso libre", listOf("Isquios", "Glúteos", "Espalda")),
    // HOMBROS
    ExerciseInfo("Press Arnold", "peso libre", listOf("Deltoides", "Tríceps")),
    ExerciseInfo("Press militar", "peso libre", listOf("Deltoides", "Tríceps")),
    ExerciseInfo("Press hombro mancuerna", "peso libre", listOf("Deltoides", "Tríceps")),
    ExerciseInfo("Vuelos laterales", "peso libre", listOf("Deltoides")),
    ExerciseInfo("Vuelos laterales polea", "polea", listOf("Deltoides")),
    ExerciseInfo("Vuelos posteriores polea", "polea", listOf("Deltoides")),
    // BÍCEPS
    ExerciseInfo("Curl inclinado", "peso libre", listOf("Bíceps")),
    ExerciseInfo("Curl martillo", "peso libre", listOf("Bíceps", "Antebrazo")),
    ExerciseInfo("Curl prono barra", "peso libre", listOf("Bíceps", "Antebrazo")),
    ExerciseInfo("Curl barra", "peso libre", listOf("Bíceps")),
    ExerciseInfo("Curl mancuerna", "peso libre", listOf("Bíceps")),
    ExerciseInfo("Curl predicador", "peso libre", listOf("Bíceps")),
    ExerciseInfo("Curl polea baja", "polea", listOf("Bíceps")),
    ExerciseInfo("Curl con Auto-Resistencia", "cuerpo libre", listOf("Bíceps")),
    // TRÍCEPS
    ExerciseInfo("Press cerrado", "peso libre", listOf("Tríceps", "Pectoral")),
    ExerciseInfo("Extensión polea", "polea", listOf("Tríceps")),
    ExerciseInfo("Extensión sobre cabeza", "peso libre", listOf("Tríceps")),
    ExerciseInfo("Jalón tríceps cuerda", "polea", listOf("Tríceps")),
    ExerciseInfo("Fondos tríceps", "cuerpo libre", listOf("Tríceps", "Pectoral", "Deltoides")),
    // PIERNAS
    ExerciseInfo("Sentadilla", "peso libre", listOf("Cuádriceps", "Glúteos", "Isquios")),
    ExerciseInfo("Sentadilla búlgara", "peso libre", listOf("Cuádriceps", "Glúteos", "Isquios")),
    ExerciseInfo("Sentadilla ciclista Smith", "máquina", listOf("Cuádriceps")),
    ExerciseInfo("Prensa 45°", "máquina", listOf("Cuádriceps", "Glúteos")),
    ExerciseInfo("Extensión cuádriceps", "máquina", listOf("Cuádriceps")),
    ExerciseInfo("Hack squat", "máquina", listOf("Cuádriceps", "Glúteos")),
    ExerciseInfo("Estocada", "peso libre", listOf("Cuádriceps", "Glúteos", "Isquios")),
    ExerciseInfo("Estocada atrás Smith", "máquina", listOf("Glúteos", "Cuádriceps", "Isquios")),
    ExerciseInfo("Leg curl sentado", "máquina", listOf("Isquios")),
    ExerciseInfo("Leg curl tumbado", "máquina", listOf("Isquios")),
    ExerciseInfo("Puente glúteos", "peso libre", listOf("Glúteos")),
    ExerciseInfo("Hip thrust", "peso libre", listOf("Glúteos", "Isquios")),
    ExerciseInfo("Hip thrust máquina", "máquina", listOf("Glúteos", "Isquios")),
    ExerciseInfo("Sentadillas con pulso", "cuerpo libre", listOf("Cuádriceps", "Glúteos", "Isquios")),
    // CORE
    ExerciseInfo("Plancha", "cuerpo libre", listOf("Core")),
    ExerciseInfo("Crunch", "cuerpo libre", listOf("Core")),
    ExerciseInfo("Elevación de piernas", "cuerpo libre", listOf("Core")),
    ExerciseInfo("Remo Delfín", "cuerpo libre", listOf("Espalda", "Deltoides")),
)
