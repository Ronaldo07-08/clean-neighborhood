package com.MobApp.cleanneighborhood.data.repository

import com.MobApp.cleanneighborhood.data.model.AuthResponse
import com.MobApp.cleanneighborhood.data.model.LoginRequest
import com.MobApp.cleanneighborhood.data.model.RegisterRequest
import com.MobApp.cleanneighborhood.data.network.ApiService
import com.MobApp.cleanneighborhood.data.storage.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

// Обёртка результата — три состояния
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(
        login: String,
        password: String,
        rememberMe: Boolean
    ): AuthResult<AuthResponse> {
        return try {
            val response = apiService.login(
                LoginRequest(
                    login = login,
                    password = password
                )
            )

            if (response.isSuccessful) {
                val body = response.body()!!
                // Сохраняем токены в DataStore
                tokenManager.saveTokens(
                    accessToken = body.accessToken,
                    refreshToken = body.refreshToken,
                    userId = body.userId,
                    login = body.login,
                    rememberMe = rememberMe
                )
                AuthResult.Success(body)
            } else {
                // Сервер ответил но с ошибкой (401, 400 и т.д.)
                when (response.code()) {
                    401 -> AuthResult.Error("Неверный логин или пароль")
                    404 -> AuthResult.Error("Пользователь не найден")
                    else -> AuthResult.Error("Ошибка сервера: ${response.code()}")
                }
            }

        } catch (e: Exception) {
            // Нет интернета или сервер недоступен
            AuthResult.Error("Ошибка подключения: ${e.localizedMessage}")
        }
    }

    suspend fun register(
        login: String,
        email: String,
        password: String
    ): AuthResult<AuthResponse> {
        return try {
            val response = apiService.register(
                RegisterRequest(
                    login = login,
                    email = email,
                    password = password
                )
            )

            if (response.isSuccessful) {
                val body = response.body()!!
                // После регистрации сразу сохраняем токены
                tokenManager.saveTokens(
                    accessToken = body.accessToken,
                    refreshToken = body.refreshToken,
                    userId = body.userId,
                    login = body.login,
                    rememberMe = false
                )
                AuthResult.Success(body)
            } else {
                when (response.code()) {
                    409 -> AuthResult.Error("Логин или почта уже заняты")
                    400 -> AuthResult.Error("Проверьте правильность данных")
                    else -> AuthResult.Error("Ошибка сервера: ${response.code()}")
                }
            }

        } catch (e: Exception) {
            AuthResult.Error("Ошибка подключения: ${e.localizedMessage}")
        }
    }

    // Выход из аккаунта
    suspend fun logout() {
        tokenManager.clearTokens()
    }

    // Проверка — залогинен ли пользователь
    // Используется при старте приложения
    val isLoggedIn = tokenManager.isLoggedIn
}