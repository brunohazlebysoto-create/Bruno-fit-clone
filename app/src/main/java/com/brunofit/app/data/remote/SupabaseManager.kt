package com.brunofit.app.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object SupabaseManager {
    private var _url: String = ""
    private var _key: String = ""
    private var _accessToken: String? = null
    private var _userId: String? = null
    private var _userEmail: String? = null

    val isInitialized: Boolean get() = _url.isNotEmpty() && _key.isNotEmpty()

    data class UserInfo(val id: String, val email: String)

    fun init(url: String, anonKey: String) {
        _url = url.trim().trimEnd('/')
        _key = anonKey.trim()
    }

    suspend fun signIn(email: String, password: String) = withContext(Dispatchers.IO) {
        if (!isInitialized) error("Supabase no configurado")
        val conn = URL("$_url/auth/v1/token?grant_type=password").openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("apikey", _key)
            conn.doOutput = true
            val body = JSONObject().apply { put("email", email); put("password", password) }.toString()
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val text = (if (code < 400) conn.inputStream else conn.errorStream).bufferedReader().readText()
            if (code != 200) error("Error $code: ${JSONObject(text).optString("error_description", text)}")
            val json = JSONObject(text)
            _accessToken = json.getString("access_token")
            val user = json.getJSONObject("user")
            _userId = user.getString("id")
            _userEmail = email
        } finally {
            conn.disconnect()
        }
    }

    suspend fun signUp(email: String, password: String) = withContext(Dispatchers.IO) {
        if (!isInitialized) error("Supabase no configurado")
        val conn = URL("$_url/auth/v1/signup").openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("apikey", _key)
            conn.doOutput = true
            val body = JSONObject().apply { put("email", email); put("password", password) }.toString()
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            if (code !in 200..299) {
                val text = (conn.errorStream ?: conn.inputStream).bufferedReader().readText()
                error("Error $code: ${JSONObject(text).optString("error_description", text)}")
            }
        } finally {
            conn.disconnect()
        }
    }

    suspend fun signOut() {
        val token = _accessToken
        if (token != null && isInitialized) {
            try {
                withContext(Dispatchers.IO) {
                    val conn = URL("$_url/auth/v1/logout").openConnection() as HttpURLConnection
                    try {
                        conn.requestMethod = "POST"
                        conn.setRequestProperty("Authorization", "Bearer $token")
                        conn.setRequestProperty("apikey", _key)
                        conn.connect()
                    } finally { conn.disconnect() }
                }
            } catch (_: Exception) {}
        }
        _accessToken = null
        _userId = null
        _userEmail = null
    }

    fun currentUser(): UserInfo? {
        val id = _userId ?: return null
        return UserInfo(id = id, email = _userEmail ?: "")
    }

    fun postgrest(): Any? = null
}
