package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tiendavirtualapp.viewmodel.ProductoViewModel
import com.example.tiendavirtualapp.viewmodel.CartViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.tiendavirtualapp.data.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.tiendavirtualapp.ui.components.ImagenProducto
import com.example.tiendavirtualapp.ui.components.DescripcionExpandible
import com.example.tiendavirtualapp.ui.components.SelectorCantidad
import com.example.tiendavirtualapp.ui.components.ProductosRelacionados
import androidx.compose.runtime.mutableStateOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleProducto(
    productId: String?,
    navController: NavController,
    productoViewModel: ProductoViewModel = viewModel(),
    cartViewModel: CartViewModel,
) {
    val productos by productoViewModel.productos.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val producto = productos.find { it.id == productId }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val cantidad = cartItems.count { it.id == producto?.id }
    val scrollState = rememberScrollState()

    val cantidadSeleccionada = remember { mutableStateOf(1) }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (producto != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "S/ ${producto.precio}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (!SessionManager.isLoggedIn(context)) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Debes iniciar sesión para agregar al carrito")
                                }
                            } else {
                                cartViewModel.addToCart(producto)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Producto agregado al carrito")
                                }
                            }
                        },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ) {
                        if (cantidad > 0) {
                            Text("Agregar al carrito ($cantidad)")
                        } else {
                            Text("Agregar al carrito")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (producto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Producto no encontrado", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 80.dp), // espacio para el bottomBar
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImagenProducto(
                    producto = producto,
                    onBack = { navController.popBackStack() },
                    onSearch = { /* TODO: abrir búsqueda global */ }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    DescripcionExpandible(text = producto.descripcion)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text("Cantidad:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    SelectorCantidad(cantidadState = cantidadSeleccionada)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Más productos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
                )

                ProductosRelacionados(
                    productos = productos.filter { it.id != producto.id }.take(10),
                    onProductClick = { prod -> navController.navigate("detalle/${prod.id}") }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

        }
    }
}