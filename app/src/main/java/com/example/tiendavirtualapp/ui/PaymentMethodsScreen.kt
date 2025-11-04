package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*

// Modelo de método de pago
 data class MetodoPago(
    val id: String = "",
    val tipo: String = "Tarjeta",
    val numero: String = "",
    val vencimiento: String = "",
    val cvv: String = "",
    val titular: String = "",
    val usuarioEmail: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email
    var metodos by remember { mutableStateOf(listOf<MetodoPago>()) }
    var loading by remember { mutableStateOf(true) }
    var showForm by remember { mutableStateOf(false) }
    var editMetodo by remember { mutableStateOf<MetodoPago?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Consulta los métodos de pago del usuario
    LaunchedEffect(userEmail) {
        if (!userEmail.isNullOrBlank()) {
            FirebaseFirestore.getInstance()
                .collection("metodos_pago")
                .whereEqualTo("usuarioEmail", userEmail)
                .get()
                .addOnSuccessListener { result ->
                    val list = result.documents.mapNotNull { doc ->
                        val m = doc.toObject(MetodoPago::class.java)
                        if (m != null) m.copy(id = doc.id) else null
                    }
                    metodos = list
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
        topBar = { TopAppBar(title = { Text("Métodos de pago") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editMetodo = null
                showForm = true
            }) {
                Text("+")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (metodos.isEmpty()) {
                Text("No tienes métodos de pago guardados.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(metodos) { metodo ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${metodo.tipo} •••• ${metodo.numero.takeLast(4)}", style = MaterialTheme.typography.titleMedium)
                                Text("Vencimiento: ${metodo.vencimiento}", style = MaterialTheme.typography.bodySmall)
                                Text("Titular: ${metodo.titular}", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                    TextButton(onClick = {
                                        editMetodo = metodo
                                        showForm = true
                                    }) { Text("Editar") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(onClick = {
                                        FirebaseFirestore.getInstance().collection("metodos_pago").document(metodo.id).delete()
                                            .addOnSuccessListener {
                                                metodos = metodos.filter { it.id != metodo.id }
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Método eliminado")
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Error al eliminar: ${e.message}")
                                                }
                                            }
                                    }) { Text("Eliminar") }
                                }
                            }
                        }
                    }
                }
            }
            if (showForm) {
                MetodoPagoForm(
                    metodo = editMetodo,
                    userEmail = userEmail ?: "",
                    onClose = { showForm = false },
                    onSaved = { nuevo ->
                        showForm = false
                        if (editMetodo == null) {
                            metodos = metodos + nuevo
                        } else {
                            metodos = metodos.map { if (it.id == nuevo.id) nuevo else it }
                        }
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Método guardado")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MetodoPagoForm(
    metodo: MetodoPago?,
    userEmail: String,
    onClose: () -> Unit,
    onSaved: (MetodoPago) -> Unit
) {
    var tipo by remember { mutableStateOf(metodo?.tipo ?: "Tarjeta") }
    var numero by remember { mutableStateOf(metodo?.numero ?: "") }
    var vencimiento by remember { mutableStateOf(metodo?.vencimiento ?: "") }
    var cvv by remember { mutableStateOf(metodo?.cvv ?: "") }
    var titular by remember { mutableStateOf(metodo?.titular ?: "") }
    val db = FirebaseFirestore.getInstance()
    val isEdit = metodo != null

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Button(onClick = {
                if (numero.length < 12 || vencimiento.isBlank() || cvv.length < 3 || titular.isBlank()) return@Button
                val nuevo = MetodoPago(
                    id = metodo?.id ?: "",
                    tipo = tipo,
                    numero = numero,
                    vencimiento = vencimiento,
                    cvv = cvv,
                    titular = titular,
                    usuarioEmail = userEmail
                )
                if (isEdit) {
                    db.collection("metodos_pago").document(nuevo.id).set(nuevo)
                        .addOnSuccessListener { onSaved(nuevo) }
                } else {
                    db.collection("metodos_pago").add(nuevo)
                        .addOnSuccessListener { ref -> onSaved(nuevo.copy(id = ref.id)) }
                }
            }) { Text(if (isEdit) "Guardar cambios" else "Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Cancelar") }
        },
        title = { Text(if (isEdit) "Editar método de pago" else "Nuevo método de pago") },
        text = {
            Column {
                OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo") })
                OutlinedTextField(value = numero, onValueChange = { numero = it }, label = { Text("Número de tarjeta") })
                OutlinedTextField(value = vencimiento, onValueChange = { vencimiento = it }, label = { Text("Vencimiento (MM/AA)") })
                OutlinedTextField(value = cvv, onValueChange = { cvv = it }, label = { Text("CVV") })
                OutlinedTextField(value = titular, onValueChange = { titular = it }, label = { Text("Titular") })
            }
        }
    )
}

