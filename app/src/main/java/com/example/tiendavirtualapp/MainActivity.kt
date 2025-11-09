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
import com.example.tiendavirtualapp.ui.Carrito
import com.example.tiendavirtualapp.ui.PantallaCatalgo
import com.example.tiendavirtualapp.ui.PantallaCategorias
import com.example.tiendavirtualapp.ui.PantallaLogin
import com.example.tiendavirtualapp.ui.PantallaPerfil
import com.example.tiendavirtualapp.ui.PantallaRegistro
import com.example.tiendavirtualapp.ui.theme.TiendaVirtualAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tiendavirtualapp.ui.PantallaPago
import com.example.tiendavirtualapp.viewmodel.CartViewModel
import com.example.tiendavirtualapp.ui.ListaDirecciones
import com.example.tiendavirtualapp.ui.FormularioDireccion
import com.example.tiendavirtualapp.ui.PantallaPedidos
import com.example.tiendavirtualapp.ui.PantallaDetallePedido


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
                        composable("catalog") { PantallaCatalgo(navController, cartViewModel = cartViewModel) }
                        composable("categories") { PantallaCategorias() }
                        composable("cart") { Carrito(cartViewModel = cartViewModel, navController = navController) }
                        composable("profile") { PantallaPerfil(navController) }
                        composable("login") { PantallaLogin(navController) }
                        composable("register") { PantallaRegistro(navController) }
                        composable("detalle/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            com.example.tiendavirtualapp.ui.PantallaDetalleProducto(
                                productId = id,
                                navController = navController,
                                cartViewModel = cartViewModel
                            )
                        }
                        composable("checkout") { PantallaPago(navController, cartViewModel = cartViewModel) }
                        composable("address_list") { ListaDirecciones(navController) }
                        composable("address_form") { FormularioDireccion(navController) }
                        composable("orders") { PantallaPedidos(navController) }
                        composable("order_detail/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")
                            PantallaDetallePedido(navController, orderId = id)
                        }
                        composable("my_reviews") { com.example.tiendavirtualapp.ui.PantallaMisResenas(navController) }
                        composable("payment_methods") { com.example.tiendavirtualapp.ui.PantallaMetodosPago(navController) }
                    }
                }
            }
        }
    }
}