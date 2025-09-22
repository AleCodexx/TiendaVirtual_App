package com.example.tiendavirtualapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiendavirtualapp.navigation.BottomNavigationBar
import com.example.tiendavirtualapp.ui.CartScreen
import com.example.tiendavirtualapp.ui.CatalogScreen
import com.example.tiendavirtualapp.ui.CategoriesScreen
import com.example.tiendavirtualapp.ui.LoginScreen
import com.example.tiendavirtualapp.ui.ProfileScreen
import com.example.tiendavirtualapp.ui.RegisterScreen
import com.example.tiendavirtualapp.ui.theme.TiendaVirtualAppTheme
import com.example.tiendavirtualapp.utils.DataUploader


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //DataUploader.insertarProductosIniciales()
            TiendaVirtualAppTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController)
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "catalog",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("catalog") { CatalogScreen() }
                        composable("categories") { CategoriesScreen() }
                        composable("cart") { CartScreen() }
                        composable("profile") { ProfileScreen(navController) }
                        composable("login") { LoginScreen(navController) }
                        composable("register") { RegisterScreen(navController) }
                    }
                }
            }
        }
    }
}