package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tiendavirtualapp.viewmodel.CartViewModel
import com.example.tiendavirtualapp.util.formatPrice
import com.example.tiendavirtualapp.ui.components.CartItemRow
import com.example.tiendavirtualapp.ui.components.PromoBox
import com.example.tiendavirtualapp.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Carrito(cartViewModel: CartViewModel = viewModel(), navController: NavController? = null) {
    val cartItems by cartViewModel.cartItems.collectAsState()

    // Calcular total y formatearlo a 2 decimales
    val total = cartItems.sumOf { it.precio }
    val totalFormateado = formatPrice(total)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Carrito 🛍️") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PromoBox()

            if (cartItems.isEmpty()) {
                EmptyState(title = "Tu carrito está vacío", subtitle = "Agrega tus productos favoritos ✨")
            } else {
                // Contenido principal
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f) // ocupa espacio disponible
                    ) {
                        items(cartItems.size) { index ->
                            val product = cartItems[index]
                            CartItemRow(producto = product, onRemove = {
                                cartViewModel.removeFromCart(product)
                            })
                        }
                    }

                    // Reemplazado Divider (deprecado) por HorizontalDivider
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Total + botón fijo abajo
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Total: S/ $totalFormateado",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { navController?.navigate("checkout") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            enabled = cartItems.isNotEmpty()
                        ) {
                            Text("Finalizar compra")
                        }
                    }
                }
            }
        }
    }
}
