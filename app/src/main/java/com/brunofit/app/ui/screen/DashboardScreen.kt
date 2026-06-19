package com.brunofit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunofit.app.ui.theme.*
import com.brunofit.app.ui.viewmodel.DashboardViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

@Composable
fun DashboardScreen(vm: DashboardViewModel) {
    val state by vm.state.collectAsState()
    var showMetricsDialog by remember { mutableStateOf(false) }
    var draftWeight by remember { mutableStateOf("") }
    var draftGrasa by remember { mutableStateOf("") }
    var draftMusculo by remember { mutableStateOf("") }
    val dateFmt = DateTimeFormatter.ofPattern("EEE d MMM", Locale("es"))

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BgDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = vm::previousDay) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MutedGreen)
                }
                Text(
                    state.date.format(dateFmt).uppercase(),
                    fontWeight = FontWeight.Bold, color = InkLight, fontSize = 15.sp, letterSpacing = 1.sp
                )
                IconButton(onClick = vm::nextDay) {
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedGreen)
                }
            }
        }

        // Calories card
        item {
            Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("CALORÍAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedGreen, letterSpacing = 1.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "${(state.nutrition?.kcal ?: 0.0).toInt()}",
                            fontSize = 40.sp, fontWeight = FontWeight.Bold, color = LimePrimary
                        )
                        Text(" / ${state.targetKcal} kcal", color = MutedGreen, modifier = Modifier.padding(bottom = 6.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (min((state.nutrition?.kcal ?: 0.0) / state.targetKcal.toDouble(), 1.0)).toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = LimePrimary, trackColor = LineDark
                    )
                }
            }
        }

        // Macros
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MacroCard("PROT", state.nutrition?.proteina ?: 0.0, state.targetProtein.toDouble(), "g", CyanAccent, Modifier.weight(1f))
                MacroCard("CARB", state.nutrition?.carbo ?: 0.0, state.targetCarbs.toDouble(), "g", AmberAccent, Modifier.weight(1f))
                MacroCard("GRASA", state.nutrition?.grasa ?: 0.0, state.targetFat.toDouble(), "g", RoseAccent, Modifier.weight(1f))
            }
        }

        // Water
        item {
            Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("AGUA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedGreen, letterSpacing = 1.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        val water = state.nutrition?.water ?: 0.0
                        Text("${water.toInt()} / 3000 ml", color = InkLight, fontWeight = FontWeight.SemiBold)
                        Row {
                            IconButton(onClick = { vm.updateWater(maxOf(0.0, (state.nutrition?.water ?: 0.0) - 250)) }) {
                                Icon(Icons.Default.Remove, contentDescription = null, tint = MutedGreen)
                            }
                            IconButton(onClick = { vm.updateWater((state.nutrition?.water ?: 0.0) + 250) }) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = BlueAccent)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (min((state.nutrition?.water ?: 0.0) / 3000.0, 1.0)).toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = BlueAccent, trackColor = LineDark
                    )
                }
            }
        }

        // Workout summary
        if (state.workouts.isNotEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("ENTRENAMIENTO DE HOY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedGreen, letterSpacing = 1.sp)
                        Spacer(Modifier.height(8.dp))
                        state.workouts.forEach { ex ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(ex.exerciseName, color = InkLight, fontSize = 14.sp)
                                val sets = try { kotlinx.serialization.json.Json.decodeFromString<List<com.brunofit.app.data.model.WorkoutSet>>(ex.setsJson) } catch (_: Exception) { emptyList() }
                                Text("${sets.size} series", color = LimePrimary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Metrics card
        item {
            Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("MÉTRICAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedGreen, letterSpacing = 1.sp)
                        TextButton(onClick = {
                            draftWeight = state.metrics?.weight?.toString() ?: ""
                            draftGrasa = state.metrics?.grasaPct?.toString() ?: ""
                            draftMusculo = state.metrics?.musculo?.toString() ?: ""
                            showMetricsDialog = true
                        }) { Text("Registrar", color = LimePrimary, fontSize = 12.sp) }
                    }
                    if (state.metrics != null) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            MetricPill("Peso", "${state.metrics!!.weight} kg", InkLight)
                            MetricPill("Grasa", "${state.metrics!!.grasaPct}%", AmberAccent)
                            MetricPill("Músculo", "${state.metrics!!.musculo} kg", LimePrimary)
                        }
                    } else {
                        Text("Sin datos para hoy", color = MutedGreen, fontSize = 13.sp)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }

    if (showMetricsDialog) {
        AlertDialog(
            onDismissRequest = { showMetricsDialog = false },
            containerColor = PanelDark,
            title = { Text("Registrar métricas", color = InkLight) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricField("Peso (kg)", draftWeight) { draftWeight = it }
                    MetricField("Grasa corporal (%)", draftGrasa) { draftGrasa = it }
                    MetricField("Músculo (kg)", draftMusculo) { draftMusculo = it }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.saveMetrics(
                        draftWeight.toDoubleOrNull() ?: 0.0,
                        draftGrasa.toDoubleOrNull() ?: 0.0,
                        draftMusculo.toDoubleOrNull() ?: 0.0,
                        0
                    )
                    showMetricsDialog = false
                }) { Text("Guardar", color = LimePrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showMetricsDialog = false }) { Text("Cancelar", color = MutedGreen) }
            }
        )
    }
}

@Composable
private fun MacroCard(label: String, current: Double, target: Double, unit: String, color: Color, modifier: Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 10.sp, color = MutedGreen, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
            Text("${current.toInt()}${unit}", color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("/${target.toInt()}", color = MutedGreen, fontSize = 11.sp)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (min(current / target, 1.0)).toFloat() },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = color, trackColor = LineDark
            )
        }
    }
}

@Composable
private fun MetricPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = MutedGreen, fontSize = 11.sp)
    }
}

@Composable
private fun MetricField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label, color = MutedGreen) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LimePrimary, unfocusedBorderColor = LineDark,
            focusedTextColor = InkLight, unfocusedTextColor = InkLight, cursorColor = LimePrimary,
            focusedLabelColor = LimePrimary, unfocusedLabelColor = MutedGreen
        ),
        singleLine = true
    )
}
