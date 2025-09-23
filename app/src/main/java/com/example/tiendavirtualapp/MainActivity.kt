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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tiendavirtualapp.viewmodel.CartViewModel


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //DataUploader.insertarProductosIniciales()
            TiendaVirtualAppTheme {
                val navController = rememberNavController()
                val cartViewModel: CartViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        // Solo mostrar la barra si la ruta actual NO es detalle/{id}
                        val navBackStackEntry = navController.currentBackStackEntryAsState().value
                        val currentRoute = navBackStackEntry?.destination?.route
                        val shouldHideBar = currentRoute?.startsWith("detalle") == true
                        if (!shouldHideBar) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = "catalog",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("catalog") { CatalogScreen(navController, cartViewModel = cartViewModel) }
                        composable("categories") { CategoriesScreen() }
                        composable("cart") { CartScreen(cartViewModel = cartViewModel) }
                        composable("profile") { ProfileScreen(navController) }
                        composable("login") { LoginScreen(navController) }
                        composable("register") { RegisterScreen(navController) }
                        composable("detalle/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            com.example.tiendavirtualapp.ui.ProductDetailScreen(
                                productId = id,
                                navController = navController,
                                cartViewModel = cartViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}