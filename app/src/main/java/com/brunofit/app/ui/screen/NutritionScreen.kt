package com.brunofit.app.ui.screen

import androidx.compose.foundation.background
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
import com.brunofit.app.ui.viewmodel.NutritionViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

@Composable
fun NutritionScreen(vm: NutritionViewModel) {
    val state by vm.state.collectAsState()
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

            // Totals card
            item {
                Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("RESUMEN DEL DÍA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedGreen, letterSpacing = 1.sp)
                        Spacer(Modifier.height(12.dp))
                        val kcal = state.entity?.kcal ?: 0.0
                        val prot = state.entity?.proteina ?: 0.0
                        val carbs = state.entity?.carbo ?: 0.0
                        val fat = state.entity?.grasa ?: 0.0
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            MacroSummary("Kcal", kcal.toInt(), state.targetKcal, LimePrimary)
                            MacroSummary("Prot", prot.toInt(), state.targetProtein, CyanAccent)
                            MacroSummary("Carb", carbs.toInt(), state.targetCarbs, AmberAccent)
                            MacroSummary("Grasa", fat.toInt(), state.targetFat, RoseAccent)
                        }
                    }
                }
            }

            // Food items
            item {
                Text("ALIMENTOS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedGreen, letterSpacing = 1.sp)
            }

            if (state.foodItems.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
                        Text("Sin alimentos registrados hoy", color = MutedGreen, fontSize = 14.sp)
                    }
                }
            } else {
                items(state.foodItems) { food ->
                    Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(food.name, color = InkLight, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(
                                    "${food.kcal.toInt()} kcal · P:${food.proteina.toInt()}g · C:${food.carbo.toInt()}g · G:${food.grasa.toInt()}g",
                                    color = MutedGreen, fontSize = 12.sp
                                )
                            }
                            IconButton(onClick = { vm.removeFood(food.id) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Close, null, tint = RoseAccent, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Water
            item {
                Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("AGUA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedGreen, letterSpacing = 1.sp)
                        Spacer(Modifier.height(8.dp))
                        val water = state.entity?.water ?: 0.0
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${water.toInt()} / 3000 ml", color = InkLight, fontWeight = FontWeight.SemiBold)
                            Row {
                                IconButton(onClick = { vm.updateWater(maxOf(0.0, water - 250)) }) {
                                    Icon(Icons.Default.Remove, null, tint = MutedGreen)
                                }
                                IconButton(onClick = { vm.updateWater(water + 250) }) {
                                    Icon(Icons.Default.Add, null, tint = BlueAccent)
                                }
                            }
                        }
                        LinearProgressIndicator(
                            progress = { (min(water / 3000.0, 1.0)).toFloat() },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = BlueAccent, trackColor = LineDark
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        FloatingActionButton(
            onClick = vm::showAddFood,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = LimePrimary, contentColor = BgDark
        ) { Icon(Icons.Default.Add, null) }
    }

    if (state.showAddFood) {
        AlertDialog(
            onDismissRequest = vm::hideAddFood,
            containerColor = PanelDark,
            title = { Text("Agregar alimento", color = InkLight) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    NutriField("Nombre del alimento *", state.draftName, vm::onNameChange)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NutriField("Kcal", state.draftKcal, vm::onKcalChange, KeyboardType.Decimal, Modifier.weight(1f))
                        NutriField("Cantidad", state.draftQty, vm::onQtyChange, KeyboardType.Decimal, Modifier.weight(1f))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NutriField("Prot (g)", state.draftProtein, vm::onProteinChange, KeyboardType.Decimal, Modifier.weight(1f))
                        NutriField("Carb (g)", state.draftCarbs, vm::onCarbsChange, KeyboardType.Decimal, Modifier.weight(1f))
                        NutriField("Grasa (g)", state.draftFat, vm::onFatChange, KeyboardType.Decimal, Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(onClick = vm::addFood, colors = ButtonDefaults.buttonColors(containerColor = LimePrimary)) {
                    Text("Agregar", color = BgDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = vm::hideAddFood) { Text("Cancelar", color = MutedGreen) } }
        )
    }
}

@Composable
private fun MacroSummary(label: String, current: Int, target: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$current", color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("/$target", color = MutedGreen, fontSize = 11.sp)
        Text(label, color = MutedGreen, fontSize = 10.sp)
    }
}

@Composable
private fun NutriField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label, color = MutedGreen, fontSize = 11.sp) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LimePrimary, unfocusedBorderColor = LineDark,
            focusedTextColor = InkLight, unfocusedTextColor = InkLight,
            cursorColor = LimePrimary, focusedLabelColor = LimePrimary, unfocusedLabelColor = MutedGreen
        ),
        singleLine = true
    )
}
