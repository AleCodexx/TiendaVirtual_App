package com.example.tiendavirtualapp.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import com.example.tiendavirtualapp.model.Direccion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDirecciones(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    var direcciones by remember { mutableStateOf(listOf<Direccion>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar direcciones desde Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("direcciones")
                .whereEqualTo("usuarioId", userId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        direcciones = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Direccion::class.java)?.copy(id = doc.id)
                        }
                        isLoading = false
                    }
                }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tus direcciones") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("address_form") }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (direcciones.isEmpty()) {
                Text("No tienes direcciones guardadas.", color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(direcciones) { direccion ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${direccion.nombre} ${direccion.apellido}", style = MaterialTheme.typography.titleMedium)
                                Text(direccion.direccionCompleta)
                                Text("${direccion.departamento}, ${direccion.provincia}, ${direccion.distrito}")
                                Text("Tel: ${direccion.telefono}")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        // Editar dirección
                                        navController.navigate("address_form?id=${direccion.id}")
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = {
                                        // Eliminar dirección
                                        db.collection("direcciones").document(direccion.id).delete()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
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
