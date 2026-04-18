package com.MobApp.cleanneighborhood.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.MobApp.cleanneighborhood.data.repository.AuthRepository
import com.MobApp.cleanneighborhood.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Состояние экрана авторизации
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Состояние UI — один поток для Login и Register
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Проверка залогинен ли пользователь
    val isLoggedIn = authRepository.isLoggedIn

    fun login(
        login: String,
        password: String,
        rememberMe: Boolean
    ) {
        // Базовая валидация перед запросом
        if (login.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите логин или почту") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите пароль") }
            return
        }

        viewModelScope.launch {
            // Показываем загрузку
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.login(
                login = login,
                password = password,
                rememberMe = rememberMe
            )

            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
                is AuthResult.Loading -> Unit
            }
        }
    }

    fun register(
        login: String,
        email: String,
        password: String,
        passwordConfirm: String,
        agreedToTerms: Boolean,
        agreedToPrivacy: Boolean
    ) {
        // Валидация
        if (login.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите логин") }
            return
        }
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите почту") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Некорректный формат почты") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите пароль") }
            return
        }
        if (password.length < 8) {
            _uiState.update { it.copy(errorMessage = "Пароль минимум 8 символов") }
            return
        }
        if (password != passwordConfirm) {
            _uiState.update { it.copy(errorMessage = "Пароли не совпадают") }
            return
        }
        if (!agreedToTerms) {
            _uiState.update { it.copy(errorMessage = "Примите пользовательское соглашение") }
            return
        }
        if (!agreedToPrivacy) {
            _uiState.update { it.copy(errorMessage = "Дайте согласие на обработку данных") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.register(
                login = login,
                email = email,
                password = password
            )

            when (result) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }
                is AuthResult.Loading -> Unit
            }
        }
    }

    // Сбросить ошибку (когда пользователь начал печатать)
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Сбросить состояние (при переходе между экранами)
    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}