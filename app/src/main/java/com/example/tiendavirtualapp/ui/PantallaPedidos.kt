package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendavirtualapp.model.Pedido
import com.example.tiendavirtualapp.util.formatPrice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPedidos(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    var pedidos by remember { mutableStateOf(listOf<Pedido>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar pedidos del usuario
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("pedidos")
                .whereEqualTo("usuarioId", userId)
                // Mostrar pedidos más recientes primero
                .orderBy("fecha", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        pedidos = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Pedido::class.java)?.copy(id = doc.id)
                        }
                        isLoading = false
                    }
                }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis pedidos") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (pedidos.isEmpty()) {
                Text("No tienes pedidos realizados.", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(pedidos) { pedido ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                Text("Fecha: ${sdf.format(Date(pedido.fecha))}")
                                // Formatear total a 2 decimales
                                Text("Total: S/ ${formatPrice(pedido.total)}")
                                Text("Estado: ${pedido.estado}")
                                Button(
                                    onClick = { navController.navigate("order_detail/${pedido.id}") },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Ver detalles")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
