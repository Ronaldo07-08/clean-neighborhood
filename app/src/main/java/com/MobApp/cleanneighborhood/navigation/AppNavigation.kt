package com.MobApp.cleanneighborhood.navigation

import androidx.compose.ui.res.painterResource
import com.MobApp.cleanneighborhood.R
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.MobApp.cleanneighborhood.ui.auth.AuthViewModel
import com.MobApp.cleanneighborhood.ui.auth.LoginScreen
import com.MobApp.cleanneighborhood.ui.auth.RegisterScreen
import com.MobApp.cleanneighborhood.ui.catalog.CatalogScreen
import com.MobApp.cleanneighborhood.ui.home.HomeScreen
import com.MobApp.cleanneighborhood.ui.map.MapScreen
import com.MobApp.cleanneighborhood.ui.profile.ProfileScreen

private val ActiveColor = Color(0xFF609432)
private val InactiveColor = Color(0xFF000000)

object Routes {
    const val HOME = "home"
    const val MAP = "map"
    const val CATALOG = "catalog"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

data class NavItem(
    val route: String,
    val label: String
)

@Composable
fun NavTextButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (isActive) ActiveColor else InactiveColor,
        animationSpec = tween(durationMillis = 300),
        label = "textColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.05f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "scale"
    )

    Text(
        text = label,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun AppNavigation(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle(
        initialValue = false
    )

    val startDestination = if (isLoggedIn) Routes.HOME else Routes.LOGIN

    val navController = rememberNavController()

    val navItems = listOf(
        NavItem(route = Routes.HOME, label = "Главная"),
        NavItem(route = Routes.MAP, label = "Карта"),
        NavItem(route = Routes.CATALOG, label = "Каталог")
    )

    // Теперь currentRoute объявлен ПОСЛЕ navController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Список экранов где TopBar скрыт
    val screensWithoutTopBar = listOf(
        Routes.PROFILE,
        Routes.LOGIN,
        Routes.REGISTER
    )

    Scaffold(
        topBar = {
            // Проверка здесь — currentRoute уже объявлен выше
            if (currentRoute !in screensWithoutTopBar) {

                val isProfileActive = currentRoute == Routes.PROFILE

                val profileScale by animateFloatAsState(
                    targetValue = if (isProfileActive) 1.05f else 1f,
                    animationSpec = tween(durationMillis = 300),
                    label = "profileScale"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .shadow(elevation = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        navItems.forEach { item ->
                            NavTextButton(
                                label = item.label,
                                isActive = currentRoute == item.route,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(Routes.HOME) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile_default),
                        contentDescription = "Профиль",
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(22.dp)
                            .scale(profileScale)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    navController.navigate(Routes.PROFILE) {
                                        launchSingleTop = true
                                    }
                                }
                            ),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Routes.HOME) {
                HomeScreen(paddingValues = innerPadding)
            }
            composable(Routes.MAP) {
                MapScreen(paddingValues = innerPadding)
            }
            composable(Routes.CATALOG) {
                CatalogScreen(paddingValues = innerPadding)
            }
            composable(Routes.PROFILE) {
                ProfileScreen(paddingValues = innerPadding)
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}