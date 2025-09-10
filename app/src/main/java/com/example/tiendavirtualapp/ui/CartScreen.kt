package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiendavirtualapp.R
import com.example.tiendavirtualapp.data.FakeDataSource
import com.example.tiendavirtualapp.model.Producto
import kotlin.text.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen() {
    val cartItems = emptyList<Producto>() // 🚫 Carrito aún vacío
    val recommendedProducts = FakeDataSource.productos // ✅ Tus productos

    Scaffold(
        topBar = { TopAppBar(title = { Text("Carrito 🛍️") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🟢 Caja de promos
            PromoBox()

            if (cartItems.isEmpty()) {
                EmptyCartMessage()
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(recommendedProducts.size) { index ->
                    val product = recommendedProducts[index]
                    ProductoItem(producto = product)
                }
            }

        }
    }
}

@Composable
fun PromoBox() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Envío gratis en artículos seleccionados 🚚", fontWeight = FontWeight.Bold)

        }
    }
}
@Composable
fun EmptyCartMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tu carrito está vacío", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Agrega tus productos favoritos ✨", color = Color.Gray)
    }
}
