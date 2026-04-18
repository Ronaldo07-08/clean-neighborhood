package com.MobApp.cleanneighborhood.data.model

// Запрос на вход
data class LoginRequest(
    val login: String,
    val password: String
)

// Запрос на регистрацию
data class RegisterRequest(
    val login: String,
    val email: String,
    val password: String
)

// Ответ сервера при успешной авторизации
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val login: String,
    val email: String
)

// Общая обёртка для ошибок API
data class ApiError(
    val message: String,
    val code: Int
)