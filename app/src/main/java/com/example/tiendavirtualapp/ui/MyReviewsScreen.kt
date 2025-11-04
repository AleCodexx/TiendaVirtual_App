package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.tiendavirtualapp.model.Resena
import com.example.tiendavirtualapp.model.Producto
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReviewsScreen(navController: NavController) {
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
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
                            var editing by rememberSaveable { mutableStateOf(false) }
                            var rating by rememberSaveable { mutableStateOf(review.puntuacion) }
                            var comment by rememberSaveable { mutableStateOf(review.comentario) }
                            val db = FirebaseFirestore.getInstance()
                            var producto by remember { mutableStateOf<Producto?>(null) }
                            var productoLoading by remember { mutableStateOf(true) }
                            var expanded by rememberSaveable { mutableStateOf(false) }

                            // Consultar datos del producto
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

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                if (productoLoading) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                } else {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AsyncImage(
                                                model = producto?.imagenUrl,
                                                contentDescription = producto?.nombre,
                                                modifier = Modifier.size(80.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(producto?.nombre ?: "Producto", fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
                                                Text(producto?.descripcion ?: "Sin descripción", fontSize = 13.sp, color = Color.Gray, maxLines = 2)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    repeat(5) { i ->
                                                        val filled = i < review.puntuacion
                                                        Icon(
                                                            imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                                                            contentDescription = null,
                                                            tint = if (filled) MaterialTheme.colorScheme.primary else Color.Gray
                                                        )
                                                    }
                                                }
                                            }
                                            IconButton(onClick = { expanded = !expanded }) {
                                                Icon(
                                                    imageVector = if (expanded) Icons.Filled.Check else Icons.Filled.Edit,
                                                    contentDescription = if (expanded) "Cerrar" else "Ver reseña"
                                                )
                                            }
                                        }
                                        if (expanded) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            if (!editing) {
                                                Text(review.comentario)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                                Text(
                                                    text = "Fecha: ${sdf.format(Date(review.fecha))}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                                    IconButton(onClick = {
                                                        rating = review.puntuacion
                                                        comment = review.comentario
                                                        editing = true
                                                    }) {
                                                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    IconButton(onClick = {
                                                        val id = review.id
                                                        if (id.isNotBlank()) {
                                                            db.collection("resenas").document(id).delete()
                                                                .addOnSuccessListener {
                                                                    reviews = reviews.filter { it.id != id }
                                                                    coroutineScope.launch {
                                                                        snackbarHostState.showSnackbar("Reseña eliminada")
                                                                    }
                                                                }
                                                                .addOnFailureListener { e ->
                                                                    coroutineScope.launch {
                                                                        snackbarHostState.showSnackbar("Error al eliminar reseña: ${e.message}")
                                                                    }
                                                                }
                                                        }
                                                    }) {
                                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                                                    }
                                                }
                                            } else {
                                                Text("Editar reseña", fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("Puntuación:", modifier = Modifier.padding(end = 8.dp))
                                                    Row {
                                                        repeat(5) { i ->
                                                            val idx = i + 1
                                                            IconButton(onClick = { rating = idx }) {
                                                                Icon(
                                                                    imageVector = if (i < rating) Icons.Filled.Star else Icons.Outlined.Star,
                                                                    contentDescription = null,
                                                                    tint = if (i < rating) MaterialTheme.colorScheme.primary else Color.Gray
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                OutlinedTextField(
                                                    value = comment,
                                                    onValueChange = { comment = it },
                                                    label = { Text("Comentario") },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .heightIn(min = 56.dp, max = 200.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                                    TextButton(onClick = {
                                                        rating = review.puntuacion
                                                        comment = review.comentario
                                                        editing = false
                                                    }) { Text("Cancelar") }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Button(onClick = {
                                                        if (comment.isBlank()) {
                                                            coroutineScope.launch {
                                                                snackbarHostState.showSnackbar("El comentario no puede estar vacío")
                                                            }
                                                            return@Button
                                                        }
                                                        if (rating !in 1..5) {
                                                            coroutineScope.launch {
                                                                snackbarHostState.showSnackbar("Puntuación inválida")
                                                            }
                                                            return@Button
                                                        }
                                                        val updated = review.copy(
                                                            puntuacion = rating,
                                                            comentario = comment,
                                                            fecha = System.currentTimeMillis()
                                                        )
                                                        db.collection("resenas").document(review.id).set(updated)
                                                            .addOnSuccessListener {
                                                                reviews = reviews.map { if (it.id == review.id) updated else it }
                                                                editing = false
                                                                coroutineScope.launch {
                                                                    snackbarHostState.showSnackbar("Reseña actualizada")
                                                                }
                                                            }
                                                            .addOnFailureListener { e ->
                                                                coroutineScope.launch {
                                                                    snackbarHostState.showSnackbar("Error al actualizar reseña: ${e.message}")
                                                                }
                                                            }
                                                    }) { Text("Guardar") }
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
}