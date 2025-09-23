package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.tiendavirtualapp.viewmodel.ProductoViewModel
import com.example.tiendavirtualapp.viewmodel.CartViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.tiendavirtualapp.data.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.tiendavirtualapp.model.Producto
import androidx.compose.foundation.lazy.items


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
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
    val expanded = remember { androidx.compose.runtime.mutableStateOf(false) }

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
                        shape = RoundedCornerShape(8.dp)
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
                Box(modifier = Modifier.height(340.dp).fillMaxWidth()) {
                    AsyncImage(
                        model = producto.imagenUrl,
                        contentDescription = producto.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.background(
                                color = Color.White.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(50)
                            )
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                        IconButton(
                            onClick = { /* Acción de búsqueda */ },
                            modifier = Modifier.background(
                                color = Color.White.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(50)
                            )
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                // Eliminamos el nombre del producto, solo mostramos la descripción
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = producto.descripcion,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        maxLines = if (expanded.value) 20 else 3,
                        overflow = if (expanded.value) TextOverflow.Visible else TextOverflow.Ellipsis
                    )
                    if (!expanded.value) {
                        Text(
                            text = "Ver más",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clickable { expanded.value = true }
                        )
                    }
                }
                // Selector de cantidad estilo TEMU debajo de la descripción y alineado a la izquierda
                Spacer(modifier = Modifier.height(16.dp))
                val cantidadSeleccionada = remember { androidx.compose.runtime.mutableStateOf(1) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text("Cantidad:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (cantidadSeleccionada.value > 1) cantidadSeleccionada.value--
                        },
                        enabled = cantidadSeleccionada.value > 1,
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("-")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cantidadSeleccionada.value.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.width(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (cantidadSeleccionada.value < 5) {
                                cantidadSeleccionada.value++
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Solo puedes agregar hasta 5 unidades de este producto")
                                }
                            }
                        },
                        enabled = cantidadSeleccionada.value < 5,
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("+")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Más productos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productos.filter { it.id != producto.id }.take(10)) { prod: Producto ->
                        Column(
                            modifier = Modifier
                                .width(140.dp)
                                .clickable { navController.navigate("productDetail/${prod.id}") }
                        ) {
                            AsyncImage(
                                model = prod.imagenUrl,
                                contentDescription = prod.nombre,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(110.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Text(
                                text = prod.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "S/ ${prod.precio}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}