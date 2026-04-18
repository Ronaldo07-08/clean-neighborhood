package com.MobApp.cleanneighborhood.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_prefs"
)

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_LOGIN = stringPreferencesKey("user_login")
        private val REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    // Сохранить токены после входа/регистрации
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        userId: String,
        login: String,
        rememberMe: Boolean = false
    ) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
            prefs[USER_ID] = userId
            prefs[USER_LOGIN] = login
            prefs[REMEMBER_ME] = rememberMe
        }
    }

    // Получить access токен
    val accessToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN]
    }

    // Получить refresh токен
    val refreshToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[REFRESH_TOKEN]
    }

    // Получить логин пользователя
    val userLogin: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_LOGIN]
    }

    // Проверить залогинен ли пользователь
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        !prefs[ACCESS_TOKEN].isNullOrEmpty()
    }

    // Получить "Запомнить меня"
    val rememberMe: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REMEMBER_ME] ?: false
    }

    // Выйти — очистить все данные
    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}