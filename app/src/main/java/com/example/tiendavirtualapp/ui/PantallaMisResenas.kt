package com.example.tiendavirtualapp.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.tiendavirtualapp.model.Resena
import com.example.tiendavirtualapp.model.Producto
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMisResenas(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email
    var reviews by remember { mutableStateOf(listOf<Resena>()) }
    var loading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Consulta solo las reseñas del usuario actual
    LaunchedEffect(userEmail) {
        if (!userEmail.isNullOrBlank()) {
            FirebaseFirestore.getInstance()
                .collection("resenas")
                .whereEqualTo("usuarioEmail", userEmail)
                .get()
                .addOnSuccessListener { result ->
                    val list = result.documents.mapNotNull { doc ->
                        val r = doc.toObject(Resena::class.java)
                        if (r != null) r.copy(id = doc.id) else null
                    }
                    reviews = list
                    loading = false
                }
                .addOnFailureListener {
                    loading = false
                }
        } else {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis reseñas") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                reviews.isEmpty() -> {
                    Text(
                        "Aún no has escrito ninguna reseña.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reviews) { review ->
                            // Obtener info del producto asociado (solo lectura)
                            val db = FirebaseFirestore.getInstance()
                            var producto by remember { mutableStateOf<Producto?>(null) }
                            var productoLoading by remember { mutableStateOf(true) }

                            LaunchedEffect(review.productoId) {
                                productoLoading = true
                                db.collection("productos").document(review.productoId).get()
                                    .addOnSuccessListener { doc ->
                                        producto = doc.toObject(Producto::class.java)
                                        productoLoading = false
                                    }
                                    .addOnFailureListener {
                                        productoLoading = false
                                    }
                            }

                            // Card estilo Temu: imagen grande, info, estrellas y botón "Ver pedido"
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val orderId = review.orderId.trim()
                                        if (orderId.isNotBlank()) {
                                            val encoded = Uri.encode(orderId)
                                            // debug: mostrar snackbar con el id que vamos a abrir
                                            coroutineScope.launch { snackbarHostState.showSnackbar("Abrir pedido: $orderId") }
                                            navController.navigate("order_detail/$encoded")
                                        } else {
                                            // Fallback: navegar al detalle del producto si no hay orderId
                                            val prodId = review.productoId.trim()
                                            if (prodId.isNotBlank()) {
                                                coroutineScope.launch { snackbarHostState.showSnackbar("Abrir producto: $prodId") }
                                                navController.navigate("detalle/${Uri.encode(prodId)}")
                                            } else {
                                                coroutineScope.launch { snackbarHostState.showSnackbar("ID de pedido/producto inválido") }
                                            }
                                        }
                                    },
                                elevation = CardDefaults.cardElevation(8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                if (productoLoading) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                } else {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AsyncImage(
                                                model = producto?.imagenUrl,
                                                contentDescription = producto?.nombre,
                                                modifier = Modifier
                                                    .size(96.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .padding(end = 12.dp)
                                            )

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    producto?.nombre ?: "Producto",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 2
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))

                                                // Puntuación (estática)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    repeat(5) { i ->
                                                        val filled = i < review.puntuacion
                                                        Icon(
                                                            imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                                                            contentDescription = null,
                                                            tint = if (filled) MaterialTheme.colorScheme.primary else Color.Gray,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                    text = review.comentario.ifBlank { "Sin comentario" },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 3
                                                )

                                                // Mostrar orderId para depuración/visibilidad (si existe)
                                                if (review.orderId.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = "Pedido: ${review.orderId}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }

                                            // Chevron para indicar navegación
                                            Icon(
                                                imageVector = Icons.Filled.ChevronRight,
                                                contentDescription = "Ver producto",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                            Text(
                                                text = "Fecha: ${sdf.format(Date(review.fecha))}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray,
                                                modifier = Modifier.weight(1f)
                                            )

                                            OutlinedButton(
                                                onClick = {
                                                    val orderId = review.orderId.trim()
                                                    if (orderId.isNotBlank()) {
                                                       coroutineScope.launch { snackbarHostState.showSnackbar("Abrir pedido: $orderId") }
                                                        navController.navigate("order_detail/${Uri.encode(orderId)}")
                                                    } else {
                                                        val prodId = review.productoId.trim()
                                                        if (prodId.isNotBlank()) navController.navigate("detalle/${Uri.encode(prodId)}")
                                                        else coroutineScope.launch { snackbarHostState.showSnackbar("ID de pedido/producto inválido") }
                                                    }
                                                },
                                                shape = RoundedCornerShape(20.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text("Ver pedido", style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}