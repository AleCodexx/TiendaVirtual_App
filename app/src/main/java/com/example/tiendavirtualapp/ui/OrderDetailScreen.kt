package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.example.tiendavirtualapp.model.Pedido
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavController, orderId: String?) {
    var pedido by remember { mutableStateOf<Pedido?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar pedido por id
    LaunchedEffect(orderId) {
        if (!orderId.isNullOrBlank()) {
            FirebaseFirestore.getInstance().collection("pedidos").document(orderId).get()
                .addOnSuccessListener { doc ->
                    pedido = doc.toObject(Pedido::class.java)
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle del pedido") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (pedido == null) {
                Text("No se encontró el pedido.", color = MaterialTheme.colorScheme.error)
            } else {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                Text("Fecha: ${sdf.format(Date(pedido!!.fecha))}")
                Text("Total: S/ ${pedido!!.total}")
                Text("Estado: ${pedido!!.estado}")
                Text("Dirección: ${pedido!!.direccion}")
                //Text("Método de pago: ${pedido!!.metodoPago}")
                Spacer(modifier = Modifier.height(12.dp))
                Text("Productos:", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    pedido!!.productos.forEach { producto ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(), // Permite que la tarjeta se adapte a la información
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = producto.imagenUrl,
                                    contentDescription = producto.nombre,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .padding(end = 12.dp),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
                                    Text(producto.descripcion, fontSize = 13.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("S/ ${producto.precio}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
