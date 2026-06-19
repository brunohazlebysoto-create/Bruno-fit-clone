package com.brunofit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunofit.app.ui.theme.*
import com.brunofit.app.ui.viewmodel.AuthViewModel
import com.brunofit.app.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(profileVm: ProfileViewModel, authVm: AuthViewModel) {
    val state by profileVm.state.collectAsState()
    val authState by authVm.state.collectAsState()

    var draftKcal by remember(state.targetKcal) { mutableStateOf(state.targetKcal.toString()) }
    var draftProtein by remember(state.targetProtein) { mutableStateOf(state.targetProtein.toString()) }
    var draftCarbs by remember(state.targetCarbs) { mutableStateOf(state.targetCarbs.toString()) }
    var draftFat by remember(state.targetFat) { mutableStateOf(state.targetFat.toString()) }

    LaunchedEffect(state.message) {
        if (state.message.isNotEmpty()) kotlinx.coroutines.delay(2000); profileVm.clearMessage()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(BgDark).verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("PERFIL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedGreen, letterSpacing = 2.sp, modifier = Modifier.padding(top = 8.dp))

        // Account info
        Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Cuenta", fontWeight = FontWeight.Bold, color = InkLight, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                if (authState.isLoggedIn) {
                    Text("Conectado como:", color = MutedGreen, fontSize = 12.sp)
                    Text(authState.userEmail, color = LimePrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = authVm::logout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoseAccent)
                    ) { Text("Cerrar sesión", color = BgDark, fontWeight = FontWeight.Bold) }
                } else {
                    Text("No has iniciado sesión", color = MutedGreen, fontSize = 14.sp)
                }
            }
        }

        // Preset selector
        Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Objetivo nutricional", fontWeight = FontWeight.Bold, color = InkLight, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                listOf(
                    "definicion" to "Definición (2600 kcal)",
                    "mantenimiento" to "Mantenimiento (3000 kcal)",
                    "volumen" to "Volumen (3400 kcal)"
                ).forEach { (key, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.presetKey == key,
                            onClick = { profileVm.setPreset(key) },
                            colors = RadioButtonDefaults.colors(selectedColor = LimePrimary, unselectedColor = MutedGreen)
                        )
                        Text(label, color = if (state.presetKey == key) LimePrimary else InkLight, fontSize = 14.sp)
                    }
                }
            }
        }

        // Custom targets
        Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Objetivos personalizados", fontWeight = FontWeight.Bold, color = InkLight, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileField("Kcal", draftKcal, { draftKcal = it }, Modifier.weight(1f))
                    ProfileField("Proteína (g)", draftProtein, { draftProtein = it }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileField("Carbos (g)", draftCarbs, { draftCarbs = it }, Modifier.weight(1f))
                    ProfileField("Grasa (g)", draftFat, { draftFat = it }, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        profileVm.saveTargets(
                            draftKcal.toIntOrNull() ?: 2600,
                            draftProtein.toIntOrNull() ?: 220,
                            draftCarbs.toIntOrNull() ?: 265,
                            draftFat.toIntOrNull() ?: 70
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LimePrimary)
                ) { Text("Guardar objetivos", color = BgDark, fontWeight = FontWeight.Bold) }
            }
        }

        // Split selector
        Card(colors = CardDefaults.cardColors(containerColor = PanelDark), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Split activo", fontWeight = FontWeight.Bold, color = InkLight, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                val splits = listOf(
                    "A" to "A - Pecho + Bíceps",
                    "B" to "B - Pierna Cuádr. + Hombros",
                    "C" to "C - Espalda + Tríceps",
                    "D" to "D - Pierna Posterior",
                    "E" to "E - Suelo / Sin equipo"
                )
                splits.forEach { (key, label) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = state.activeSplit == key,
                            onClick = { profileVm.setSplit(key) },
                            colors = RadioButtonDefaults.colors(selectedColor = LimePrimary, unselectedColor = MutedGreen)
                        )
                        Text(label, color = if (state.activeSplit == key) LimePrimary else InkLight, fontSize = 14.sp)
                    }
                }
            }
        }

        if (state.message.isNotEmpty()) {
            Text(state.message, color = LimePrimary, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 4.dp))
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ProfileField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier.fillMaxWidth()) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label, color = MutedGreen, fontSize = 11.sp) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LimePrimary, unfocusedBorderColor = LineDark,
            focusedTextColor = InkLight, unfocusedTextColor = InkLight,
            cursorColor = LimePrimary, focusedLabelColor = LimePrimary, unfocusedLabelColor = MutedGreen
        ),
        singleLine = true
    )
}
