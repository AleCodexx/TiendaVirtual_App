package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onGloballyPositioned

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaCatalogo(
    navController: NavController,
    productoViewModel: ProductoViewModel = viewModel(),
    cartViewModel: CartViewModel,
    cartIconPositionDp: Offset? = null,
    initialCategory: String? = null
) {
    val productos by productoViewModel.productos.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current

    // permitir filtrar por categoría cuando se pasa initialCategory
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    // Estado para la gota animada (usamos dos Animatable<Float> para X e Y en px)
    var dropletVisible by remember { mutableStateOf(false) }
    val dropletX = remember { Animatable(0f) }
    val dropletY = remember { Animatable(0f) }
    val dropletScale = remember { Animatable(1f) }

    // Guardar tamaño de la pantalla para posibles cálculos
    val screenSize = remember { mutableStateOf(IntSize(0, 0)) }

    val filtered = productos.filter {
        val matchesQuery = it.nombre.contains(query, ignoreCase = true) || it.descripcion.contains(query, ignoreCase = true)
        val matchesCategory = selectedCategory?.let { cat -> it.categoria.equals(cat, ignoreCase = true) } ?: true
        matchesQuery && matchesCategory
    }

    // Función para iniciar la animación de la gota desde startOffsetDp hacia cartIconPositionDp
    fun animateDroplet(startOffsetDp: Offset) {
        val targetDp = cartIconPositionDp ?: return
        // convertir dp offsets a px para animación precisa en Canvas (Canvas usa px)
        val startPxX = with(density) { startOffsetDp.x.dp.toPx() }
        val startPxY = with(density) { startOffsetDp.y.dp.toPx() }
        val targetPxX = with(density) { targetDp.x.dp.toPx() }
        val targetPxY = with(density) { targetDp.y.dp.toPx() }

        scope.launch {
            dropletVisible = true
            dropletX.snapTo(startPxX)
            dropletY.snapTo(startPxY)
            dropletScale.snapTo(1f)

            // animación de movimiento
            dropletX.animateTo(targetPxX, animationSpec = tween(durationMillis = 600))
            dropletY.animateTo(targetPxY, animationSpec = tween(durationMillis = 600))

            // pequeño pulso
            dropletScale.animateTo(0.6f, animationSpec = tween(durationMillis = 200))
            dropletVisible = false
        }
    }

    val dropletColor = MaterialTheme.colorScheme.primary

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
        Box(modifier = Modifier.fillMaxSize()) {
            // grid de productos
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
                            }
                        },
                        onClick = { navController.navigate("detalle/${producto.id}") },
                        onAddToCartWithPosition = { startOffsetDp ->
                            // iniciar animación solo si tenemos la posición del icono del carrito
                            if (cartIconPositionDp != null) {
                                // startOffsetDp está en dp (como se reportó), iniciamos animación
                                animateDroplet(startOffsetDp)
                            }
                        }
                    )
                }
            }

            // overlay para la gota animada (Canvas usa px)
            if (dropletVisible) {
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coords ->
                        screenSize.value = coords.size
                    }
                ) {
                    val posX = dropletX.value
                    val posY = dropletY.value
                    val radius = 12f * dropletScale.value
                    drawCircle(
                        color = dropletColor,
                        radius = radius,
                        center = androidx.compose.ui.geometry.Offset(posX, posY)
                    )
                }
            }
        }
    }
}
