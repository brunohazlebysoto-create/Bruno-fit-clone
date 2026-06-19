package com.brunofit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunofit.app.ui.theme.*
import com.brunofit.app.ui.viewmodel.WorkoutViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WorkoutScreen(vm: WorkoutViewModel) {
    val state by vm.state.collectAsState()
    val filtered by vm.filteredExercises.collectAsState()
    val dateFmt = DateTimeFormatter.ofPattern("EEE d MMM", Locale("es"))

    Box(Modifier.fillMaxSize().background(BgDark)) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date nav
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = vm::previousDay) { Icon(Icons.Default.ChevronLeft, null, tint = MutedGreen) }
                    Text(state.date.format(dateFmt).uppercase(), fontWeight = FontWeight.Bold, color = InkLight, letterSpacing = 1.sp)
                    IconButton(onClick = vm::nextDay) { Icon(Icons.Default.ChevronRight, null, tint = MutedGreen) }
                }
            }

            // Exercise list
            items(state.exercises) { ex ->
                val sets = vm.getSets(ex)
                Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(ex.exerciseName, color = InkLight, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { vm.deleteExercise(ex.date, ex.exerciseName) }) {
                                Icon(Icons.Default.Delete, null, tint = RoseAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                        if (sets.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                Text("Serie", color = MutedGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Reps", color = MutedGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Peso (kg)", color = MutedGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(color = LineDark, modifier = Modifier.padding(vertical = 4.dp))
                            sets.forEachIndexed { idx, set ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                    Text("${idx + 1}", color = MutedGreen, fontSize = 14.sp)
                                    Text("${set.reps}", color = InkLight, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text("${set.kg}", color = LimePrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                        TextButton(onClick = { vm.selectExercise(ex.exerciseName) }) {
                            Icon(Icons.Default.Add, null, tint = LimePrimary, modifier = Modifier.size(16.dp))
                            Text("Agregar serie", color = LimePrimary, fontSize = 13.sp)
                        }
                    }
                }
            }

            if (state.exercises.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FitnessCenter, null, tint = LineDark, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Sin ejercicios registrados", color = MutedGreen, fontSize = 14.sp)
                            Text("Tocá el + para agregar", color = MutedGreen, fontSize = 12.sp)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        // FAB
        FloatingActionButton(
            onClick = vm::showAddExercise,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = LimePrimary, contentColor = BgDark
        ) { Icon(Icons.Default.Add, null) }
    }

    // Add exercise dialog
    if (state.showAddExercise) {
        AlertDialog(
            onDismissRequest = vm::hideAddExercise,
            containerColor = PanelDark,
            title = { Text("Elegir ejercicio", color = InkLight) },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.exerciseSearch,
                        onValueChange = vm::onSearchChange,
                        placeholder = { Text("Buscar ejercicio...", color = MutedGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LimePrimary, unfocusedBorderColor = LineDark,
                            focusedTextColor = InkLight, unfocusedTextColor = InkLight, cursorColor = LimePrimary
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = MutedGreen) },
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(filtered) { name ->
                            Text(
                                name, color = InkLight, fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth().clickable { vm.selectExercise(name) }.padding(vertical = 10.dp, horizontal = 4.dp)
                            )
                            HorizontalDivider(color = LineDark)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = vm::hideAddExercise) { Text("Cancelar", color = MutedGreen) } }
        )
    }

    // Add set dialog
    if (state.selectedExercise != null) {
        AlertDialog(
            onDismissRequest = { vm.selectExercise("").let { /* no-op keep dialog */ } },
            containerColor = PanelDark,
            title = { Text(state.selectedExercise!!, color = InkLight) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.draftReps, onValueChange = vm::onRepsChange,
                        label = { Text("Repeticiones", color = MutedGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = workoutFieldColors(), singleLine = true
                    )
                    OutlinedTextField(
                        value = state.draftKg, onValueChange = vm::onKgChange,
                        label = { Text("Peso (kg)", color = MutedGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = workoutFieldColors(), singleLine = true
                    )
                    OutlinedTextField(
                        value = state.draftNote, onValueChange = vm::onNoteChange,
                        label = { Text("Nota (opcional)", color = MutedGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = workoutFieldColors(), singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = vm::addSet, colors = ButtonDefaults.buttonColors(containerColor = LimePrimary)) {
                    Text("Guardar serie", color = BgDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { vm.hideAddExercise() }) { Text("Cancelar", color = MutedGreen) } }
        )
    }
}

@Composable
private fun workoutFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LimePrimary, unfocusedBorderColor = LineDark,
    focusedTextColor = InkLight, unfocusedTextColor = InkLight,
    cursorColor = LimePrimary, focusedLabelColor = LimePrimary, unfocusedLabelColor = MutedGreen
)
