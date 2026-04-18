package com.MobApp.cleanneighborhood.ui.auth

import AppLogo
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.MobApp.cleanneighborhood.R

private val GreenColor = Color(0xFF609432)

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Поля ввода
    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordConfirmVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var agreedToPrivacy by remember { mutableStateOf(false) }

    // Когда успешно зарегистрировались — переходим на Home
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetState()
            onNavigateToHome()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Фон с природой
        Image(
            painter = painterResource(id = R.drawable.bg_auth),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Центрируем карточку
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Белая карточка
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Логотип
                    AppLogo()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Заголовок
                    Text(
                        text = "РЕГИСТРАЦИЯ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Поле логина
                    AuthTextField(
                        value = login,
                        onValueChange = {
                            login = it
                            viewModel.clearError()
                        },
                        label = "Логин",
                        placeholder = "Введите ваш логин"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Поле почты
                    AuthTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            viewModel.clearError()
                        },
                        label = "Почта",
                        placeholder = "Введите ваш email",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Два поля пароля рядом
                    Text(
                        text = "Пароль",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Придумайте пароль
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    viewModel.clearError()
                                },
                                placeholder = {
                                    Text(
                                        text = "Придумайте пароль",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                visualTransformation = if (!passwordVisible)
                                    androidx.compose.ui.text.input.PasswordVisualTransformation()
                                else
                                    androidx.compose.ui.text.input.VisualTransformation.None,
                                trailingIcon = {
                                    IconButton(
                                        onClick = { passwordVisible = !passwordVisible }
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (passwordVisible)
                                                    R.drawable.ic_eye_on
                                                else
                                                    R.drawable.ic_eye_off
                                            ),
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenColor,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                        }

                        // Подтвердите пароль
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = passwordConfirm,
                                onValueChange = {
                                    passwordConfirm = it
                                    viewModel.clearError()
                                },
                                placeholder = {
                                    Text(
                                        text = "Подтвердите пароль",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                visualTransformation = if (!passwordConfirmVisible)
                                    androidx.compose.ui.text.input.PasswordVisualTransformation()
                                else
                                    androidx.compose.ui.text.input.VisualTransformation.None,
                                trailingIcon = {
                                    IconButton(
                                        onClick = { passwordConfirmVisible = !passwordConfirmVisible }
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (passwordConfirmVisible)
                                                    R.drawable.ic_eye_on
                                                else
                                                    R.drawable.ic_eye_off
                                            ),
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenColor,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Чекбокс — Пользовательское соглашение
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = GreenColor,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.Black)) {
                                    append("Я ознакомлен(а) с ")
                                }
                                withStyle(SpanStyle(color = GreenColor)) {
                                    append("Пользовательским соглашением.")
                                }
                            },
                            fontSize = 13.sp
                        )
                    }

                    // Чекбокс — Персональные данные
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreedToPrivacy,
                            onCheckedChange = { agreedToPrivacy = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = GreenColor,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.Black)) {
                                    append("Я даю согласие на ")
                                }
                                withStyle(SpanStyle(color = GreenColor)) {
                                    append("обработку персональных данных.")
                                }
                            },
                            fontSize = 13.sp
                        )
                    }

                    // Ошибка
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            color = Color.Red,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Кнопка Зарегистрироваться
                    Button(
                        onClick = {
                            viewModel.register(
                                login = login,
                                email = email,
                                password = password,
                                passwordConfirm = passwordConfirm,
                                agreedToTerms = agreedToTerms,
                                agreedToPrivacy = agreedToPrivacy
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenColor
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Зарегистрироваться",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Разделитель "или"
                    AuthDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ссылка на вход
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color.Black)) {
                                    append("Уже есть аккаунт? ")
                                }
                                withStyle(SpanStyle(color = GreenColor)) {
                                    append("Войти")
                                }
                            },
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}