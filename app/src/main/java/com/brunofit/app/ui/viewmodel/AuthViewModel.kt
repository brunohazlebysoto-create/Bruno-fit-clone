package com.brunofit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunofit.app.data.prefs.AppPreferences
import com.brunofit.app.data.remote.SupabaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val isSignUp: Boolean = false,
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val supabaseConfigured: Boolean = false,
    val userEmail: String = ""
)

class AuthViewModel(private val prefs: AppPreferences) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    init {
        viewModelScope.launch {
            val url = prefs.supabaseUrl.first()
            val key = prefs.supabaseKey.first()
            val loggedIn = prefs.isLoggedIn.first()
            val email = prefs.userEmail.first()
            if (url.isNotEmpty() && key.isNotEmpty()) SupabaseManager.init(url, key)
            _state.update {
                it.copy(
                    supabaseUrl = url,
                    supabaseKey = key,
                    supabaseConfigured = url.isNotEmpty() && key.isNotEmpty(),
                    isLoggedIn = loggedIn,
                    userEmail = email
                )
            }
        }
    }

    fun onEmailChange(v: String) = _state.update { it.copy(email = v) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v) }
    fun onUrlChange(v: String) = _state.update { it.copy(supabaseUrl = v) }
    fun onKeyChange(v: String) = _state.update { it.copy(supabaseKey = v) }
    fun toggleMode() = _state.update { it.copy(isSignUp = !it.isSignUp, error = null) }

    fun configureSupabase() {
        val url = _state.value.supabaseUrl.trim()
        val key = _state.value.supabaseKey.trim()
        viewModelScope.launch {
            prefs.saveString(AppPreferences.SUPABASE_URL, url)
            prefs.saveString(AppPreferences.SUPABASE_KEY, key)
            try {
                SupabaseManager.init(url, key)
                _state.update { it.copy(supabaseConfigured = url.isNotEmpty() && key.isNotEmpty(), error = "Supabase configurado") }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                SupabaseManager.signIn(_state.value.email.trim(), _state.value.password)
                val user = SupabaseManager.currentUser()
                if (user != null) {
                    prefs.saveString(AppPreferences.USER_ID, user.id)
                    prefs.saveString(AppPreferences.USER_EMAIL, _state.value.email.trim())
                    prefs.saveBoolean(AppPreferences.IS_LOGGED_IN, true)
                    _state.update { it.copy(isLoggedIn = true, isLoading = false, userEmail = _state.value.email.trim()) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "No se pudo obtener la sesión. Intentá de nuevo.") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error al iniciar sesión") }
            }
        }
    }

    fun signUp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                SupabaseManager.signUp(_state.value.email.trim(), _state.value.password)
                _state.update { it.copy(isLoading = false, error = "Revisa tu email para confirmar el registro") }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error al registrarse") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            SupabaseManager.signOut()
            prefs.saveBoolean(AppPreferences.IS_LOGGED_IN, false)
            prefs.saveString(AppPreferences.USER_ID, "")
            prefs.saveString(AppPreferences.USER_EMAIL, "")
            _state.update { it.copy(isLoggedIn = false, userEmail = "") }
        }
    }
}
