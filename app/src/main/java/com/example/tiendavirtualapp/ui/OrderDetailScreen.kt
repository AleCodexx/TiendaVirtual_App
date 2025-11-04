package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import com.example.tiendavirtualapp.data.SessionManager
import com.example.tiendavirtualapp.model.Resena
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavController, orderId: String?) {
    var pedido by remember { mutableStateOf<Pedido?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
        topBar = {
            TopAppBar(
                title = { Text("Detalle del pedido") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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

                // Línea de tiempo para el estado del pedido
                Spacer(modifier = Modifier.height(12.dp))
                OrderStatusTimeline(currentStatus = pedido!!.estado)
                Spacer(modifier = Modifier.height(12.dp))

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

                            // Si el pedido está entregado, permitimos reseñas por producto
                            if (pedido!!.estado.trim().lowercase(Locale.getDefault()) == "entregado") {
                                Spacer(modifier = Modifier.height(8.dp))
                                ProductReviewSection(
                                    productoId = producto.id,
                                    productoNombre = producto.nombre,
                                    orderId = pedido!!.id,
                                    userEmail = userEmail,
                                    snackbarHostState = snackbarHostState,
                                    onSaved = { message ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/// Composable auxiliar para dibujar la línea de tiempo del estado
@Composable
private fun OrderStatusTimeline(currentStatus: String) {
    // Definimos las etapas en orden
    val stages = listOf("Pendiente", "En preparación", "En envío", "Entregado")
    // Normalizamos el estado actual para comparar
    val normalized = currentStatus.trim().lowercase(Locale.getDefault())
    val currentIndex = stages.indexOfFirst { it.lowercase(Locale.getDefault()) == normalized }.let { if (it == -1) 0 else it }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            stages.forEachIndexed { index, stage ->
                // Cada etapa: círculo + etiqueta debajo
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    val completed = index <= currentIndex
                    val circleColor = if (completed) MaterialTheme.colorScheme.primary else Color.LightGray

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(circleColor)
                            .shadow(if (completed) 4.dp else 0.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (completed && index < currentIndex) {
                            Icon(Icons.Default.Check, contentDescription = "completo", tint = Color.White, modifier = Modifier.size(16.dp))
                        } else if (index == currentIndex) {
                            // Indicador del estado actual: se muestra un punto o el número
                            Text((index + 1).toString(), color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        } else {
                            Text((index + 1).toString(), color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Línea conectora (por debajo de los círculos)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            stages.forEachIndexed { index, _ ->
                // Línea entre etapas: se pinta completa si la etapa anterior está completada
                val weight = 1f
                if (index < stages.lastIndex) {
                    val lineCompleted = index < currentIndex
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(weight)
                            .height(2.dp)
                            .padding(horizontal = 6.dp),
                        color = if (lineCompleted) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                } else {
                    Spacer(modifier = Modifier.weight(weight))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        // Etiquetas de texto debajo de cada círculo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            stages.forEachIndexed { index, stage ->
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stage,
                        fontSize = 12.sp,
                        color = if (index <= currentIndex) MaterialTheme.colorScheme.primary else Color.Gray,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// Composable para gestionar la reseña de un producto dentro del detalle del pedido
@Composable
private fun ProductReviewSection(
    productoId: String,
    productoNombre: String,
    orderId: String,
    userEmail: String?,
    snackbarHostState: SnackbarHostState,
    onSaved: (String) -> Unit
) {
    var existing by remember { mutableStateOf<Resena?>(null) }
    var loading by remember { mutableStateOf(true) }
    var editing by remember { mutableStateOf(false) }
    var rating by rememberSaveable { mutableStateOf(5) }
    var comment by rememberSaveable { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    // Consultar si ya existe una reseña del usuario para este producto en este pedido
    LaunchedEffect(productoId, orderId, userEmail) {
        loading = true
        existing = null
        if (!userEmail.isNullOrBlank()) {
            db.collection("resenas")
                .whereEqualTo("productoId", productoId)
                .whereEqualTo("orderId", orderId)
                .whereEqualTo("usuarioEmail", userEmail)
                .limit(1)
                .get()
                .addOnSuccessListener { snap ->
                    val doc = snap.documents.firstOrNull()
                    if (doc != null) {
                        val r = doc.toObject(Resena::class.java)
                        if (r != null) {
                            existing = r.copy(id = doc.id)
                        }
                    }
                    loading = false
                }
                .addOnFailureListener {
                    loading = false
                }
        } else {
            loading = false
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)) {
        if (loading) {
            Text("Comprobando reseña...")
        } else if (existing != null && !editing) {
            // Mostrar reseña existente (solo lectura) con opciones para editar/eliminar si es del mismo usuario
            Text("Tu reseña:", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    val filled = i < existing!!.puntuacion
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (filled) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(existing!!.comentario)

            // Botones Editar / Eliminar si el usuario coincide
            if (!userEmail.isNullOrBlank() && userEmail == existing!!.usuarioEmail) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = {
                        // Preparar editor con datos existentes
                        rating = existing!!.puntuacion
                        comment = existing!!.comentario
                        editing = true
                    }) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Editar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        // Eliminar reseña
                        val id = existing!!.id
                        if (id.isNotBlank()) {
                            db.collection("resenas").document(id).delete()
                                .addOnSuccessListener {
                                    existing = null
                                    onSaved("Reseña eliminada")
                                }
                                .addOnFailureListener { e ->
                                    onSaved("Error al eliminar reseña: ${e.message}")
                                }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Eliminar")
                    }
                }
            }

        } else {
            // Mostrar editor (botón para abrir o edición activa)
            if (!userEmail.isNullOrBlank()) {
                if (!editing) {
                    Button(onClick = { editing = true }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Escribir reseña")
                    }
                } else {
                    Text("Escribe tu reseña para: $productoNombre", fontWeight = FontWeight.Bold)
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
                            // cancelar edición: si veníamos de una reseña existente, no la borramos
                            if (existing != null) {
                                // restaurar valores a los existentes
                                rating = existing!!.puntuacion
                                comment = existing!!.comentario
                            } else {
                                rating = 5
                                comment = ""
                            }
                            editing = false
                        }) { Text("Cancelar") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            // Validaciones
                            if (comment.isBlank()) {
                                onSaved("El comentario no puede estar vacío")
                                return@Button
                            }
                            if (rating !in 1..5) {
                                onSaved("Puntuación inválida")
                                return@Button
                            }

                            if (existing != null && existing!!.id.isNotBlank()) {
                                // Actualizar reseña existente
                                val updated = existing!!.copy(
                                    puntuacion = rating,
                                    comentario = comment,
                                    fecha = System.currentTimeMillis()
                                )
                                db.collection("resenas").document(existing!!.id).set(updated)
                                    .addOnSuccessListener {
                                        existing = updated
                                        editing = false
                                        onSaved("Reseña actualizada")
                                    }
                                    .addOnFailureListener { e ->
                                        onSaved("Error al actualizar reseña: ${e.message}")
                                    }
                            } else {
                                // Crear nueva reseña
                                val nueva = Resena(
                                    id = "",
                                    productoId = productoId,
                                    orderId = orderId,
                                    usuarioEmail = userEmail ?: "",
                                    puntuacion = rating,
                                    comentario = comment,
                                    fecha = System.currentTimeMillis()
                                )

                                db.collection("resenas").add(nueva)
                                    .addOnSuccessListener { docRef ->
                                        existing = nueva.copy(id = docRef.id)
                                        editing = false
                                        comment = ""
                                        rating = 5
                                        onSaved("Reseña guardada")
                                    }
                                    .addOnFailureListener { e ->
                                        onSaved("Error al guardar reseña: ${e.message}")
                                    }
                            }

                        }) { Text("Guardar") }
                    }
                }
            } else {
                Text("Inicia sesión para escribir una reseña.", color = Color.Gray)
            }
        }
    }
}
