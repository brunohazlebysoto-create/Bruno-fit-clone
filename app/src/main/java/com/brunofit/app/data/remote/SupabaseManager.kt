package com.brunofit.app.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

object SupabaseManager {
    private var _client: SupabaseClient? = null
    val client: SupabaseClient? get() = _client
    val isInitialized: Boolean get() = _client != null

    fun init(url: String, anonKey: String) {
        if (url.isBlank() || anonKey.isBlank()) return
        _client = createSupabaseClient(url.trim(), anonKey.trim()) {
            install(Auth)
            install(Postgrest)
        }
    }

    suspend fun signIn(email: String, password: String) {
        val c = _client ?: error("Supabase no está configurado")
        c.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signUp(email: String, password: String) {
        val c = _client ?: error("Supabase no está configurado")
        c.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        _client?.auth?.signOut()
    }

    fun currentUser() = _client?.auth?.currentUserOrNull()

    fun postgrest() = _client?.postgrest
}
