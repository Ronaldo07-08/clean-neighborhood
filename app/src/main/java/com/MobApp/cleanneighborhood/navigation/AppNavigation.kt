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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
        // 15 / 1.5 = 10.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val navItems = listOf(
        NavItem(route = Routes.HOME, label = "Главная"),
        NavItem(route = Routes.MAP, label = "Карта"),
        NavItem(route = Routes.CATALOG, label = "Каталог")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute != Routes.PROFILE) {

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
                        // Тень снизу
                        .shadow(elevation = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Центральные кнопки — занимают всё место кроме иконки
                    Row(
                        modifier = Modifier
                            .weight(1f),
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

                    // Иконка профиля справа
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile_default),
                        contentDescription = "Профиль",
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(60.dp)
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
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME
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
        }
    }
}