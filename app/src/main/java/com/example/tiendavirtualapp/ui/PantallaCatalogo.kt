package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiendavirtualapp.viewmodel.ProductoViewModel
import com.example.tiendavirtualapp.viewmodel.CartViewModel
import androidx.navigation.NavController
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.tiendavirtualapp.data.SessionManager
import kotlinx.coroutines.launch
import com.example.tiendavirtualapp.ui.components.BarraBusqueda
import com.example.tiendavirtualapp.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaCatalogo(
    navController: NavController,
    productoViewModel: ProductoViewModel = viewModel(),
    cartViewModel: CartViewModel,
) {
    val productos by productoViewModel.productos.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filtered = productos.filter {
        it.nombre.contains(query, ignoreCase = true) ||
                it.descripcion.contains(query, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BarraBusqueda(
                        query = query,
                        onQueryChange = { query = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { producto ->
                val cantidad = cartItems.count { it.id == producto.id }
                ProductCard(
                    producto = producto,
                    cantidadEnCarrito = cantidad,
                    onAddToCart = {
                        if (!SessionManager.isLoggedIn(context)) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Debes iniciar sesión para agregar productos al carrito.")
                            }
                        } else {
                            cartViewModel.addToCart(producto)
                            scope.launch {
                                snackbarHostState.showSnackbar("Producto agregado al carrito")
                            }
                        }
                    },
                    onClick = { navController.navigate("detalle/${producto.id}") }
                )
            }
        }
    }
}
