package com.brunofit.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunofit.app.ui.theme.*
import com.brunofit.app.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(vm: AuthViewModel, onLoggedIn: () -> Unit) {
    val state by vm.state.collectAsState()
    var showPass by remember { mutableStateOf(false) }
    var configExpanded by remember { mutableStateOf(!state.supabaseConfigured) }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoggedIn()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("BRUNOFIT", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = LimePrimary, letterSpacing = 4.sp)
        Text("Centro de Mando Fitness", color = MutedGreen, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, bottom = 32.dp))

        // Supabase config card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PanelDark)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Configuración Supabase", fontWeight = FontWeight.SemiBold, color = InkLight)
                    TextButton(onClick = { configExpanded = !configExpanded }) {
                        Text(if (configExpanded) "Cerrar" else "Editar", color = LimePrimary)
                    }
                }
                if (state.supabaseConfigured && !configExpanded) {
                    Text("✓ Supabase configurado", color = LimePrimary, fontSize = 13.sp)
                }
                if (configExpanded) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.supabaseUrl,
                        onValueChange = vm::onUrlChange,
                        label = { Text("URL del proyecto", color = MutedGreen) },
                        placeholder = { Text("https://xxxx.supabase.co", color = MutedGreen, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = authFieldColors(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.supabaseKey,
                        onValueChange = vm::onKeyChange,
                        label = { Text("Anon Key", color = MutedGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = authFieldColors(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.configureSupabase(); configExpanded = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LineDark)
                    ) { Text("Guardar configuración", color = InkLight) }
                }
            }
        }

        if (state.supabaseConfigured) {
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PanelDark)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        if (state.isSignUp) "Crear cuenta" else "Iniciar sesión",
                        fontWeight = FontWeight.Bold, color = InkLight, fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = vm::onEmailChange,
                        label = { Text("Email", color = MutedGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = authFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = vm::onPasswordChange,
                        label = { Text("Contraseña", color = MutedGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = authFieldColors(),
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPass = !showPass }) {
                                Icon(
                                    if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null, tint = MutedGreen
                                )
                            }
                        },
                        singleLine = true
                    )
                    if (state.error != null) {
                        Text(state.error!!, color = RoseAccent, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { if (state.isSignUp) vm.signUp() else vm.login() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = LimePrimary)
                    ) {
                        if (state.isLoading) CircularProgressIndicator(color = BgDark, modifier = Modifier.size(20.dp))
                        else Text(if (state.isSignUp) "Registrarse" else "Entrar", color = BgDark, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = vm::toggleMode, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            if (state.isSignUp) "¿Ya tenés cuenta? Iniciar sesión" else "¿No tenés cuenta? Crear una",
                            color = LimePrimary, fontSize = 13.sp, textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            Spacer(Modifier.height(24.dp))
            Text(
                "Configurá tu proyecto Supabase arriba para continuar.\nSi no tenés uno, crealo gratis en supabase.com",
                color = MutedGreen, fontSize = 13.sp, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LimePrimary,
    unfocusedBorderColor = LineDark,
    focusedTextColor = InkLight,
    unfocusedTextColor = InkLight,
    cursorColor = LimePrimary,
    focusedLabelColor = LimePrimary,
    unfocusedLabelColor = MutedGreen
)
