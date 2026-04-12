package com.MobApp.cleanneighborhood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.MobApp.cleanneighborhood.navigation.AppNavigation
import com.MobApp.cleanneighborhood.ui.theme.CleanNeighborhoodTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CleanNeighborhoodTheme {
                AppNavigation()
            }
        }
    }
}