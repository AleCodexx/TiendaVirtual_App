package com.example.tiendavirtualapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiendavirtualapp.ui.AdminScreen
import com.example.tiendavirtualapp.ui.CatalogScreen
import com.example.tiendavirtualapp.ui.HomeScreen
import com.example.tiendavirtualapp.ui.theme.TiendaVirtualAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TiendaVirtualAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("catalog") { CatalogScreen() }
                    composable("admin") { AdminScreen() }
                }

            }
        }
    }
}