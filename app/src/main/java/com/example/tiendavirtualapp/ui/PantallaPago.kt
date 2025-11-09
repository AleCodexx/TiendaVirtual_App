package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendavirtualapp.viewmodel.CartViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.collectAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.tiendavirtualapp.model.Pedido
import com.example.tiendavirtualapp.model.Direccion
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPago(navController: NavController, cartViewModel: CartViewModel) {
    var direccion by remember { mutableStateOf(TextFieldValue("Av. Principal 123")) }
    var metodoPago by remember { mutableStateOf("Tarjeta de crédito (**** 1234)" ) }
    var numeroTarjeta by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    val cartItems by cartViewModel.cartItems.collectAsState()
    val total = cartItems.sumOf { it.precio }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var isLoading by remember { mutableStateOf(false) }

    // Consultar direcciones del usuario
    val userId = auth.currentUser?.uid
    var direcciones by remember { mutableStateOf(listOf<Direccion>()) }
    var direccionSeleccionada by remember { mutableStateOf<Direccion?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // Cargar direcciones desde Firestore
    LaunchedEffect (userId) {
        if (userId != null) {
            db.collection("direcciones")
                .whereEqualTo("usuarioId", userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val dirs = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Direccion::class.java)?.copy(id = doc.id)
                    }
                    direcciones = dirs
                    if (dirs.isNotEmpty() && direccionSeleccionada == null) {
                        direccionSeleccionada = dirs.first()
                    }
                }
        }
    }

    // Consultar métodos de pago del usuario
    var metodosPago by remember { mutableStateOf(listOf<MetodoPago>()) }
    var metodoPagoSeleccionado by remember { mutableStateOf<MetodoPago?>(null) }
    var expandedMetodoPago by remember { mutableStateOf(false) }
    val userEmail = auth.currentUser?.email

    // Cargar métodos de pago desde Firestore
    LaunchedEffect(userEmail) {
        if (!userEmail.isNullOrBlank()) {
            db.collection("metodos_pago")
                .whereEqualTo("usuarioEmail", userEmail)
                .get()
                .addOnSuccessListener { snapshot ->
                    val metodos = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MetodoPago::class.java)?.copy(id = doc.id)
                    }
                    metodosPago = metodos
                    if (metodos.isNotEmpty()) metodoPagoSeleccionado = metodos.first()
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pagar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Resumen de tu pedido", style = MaterialTheme.typography.titleLarge)
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp)
            ) {
                items(cartItems) { producto ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(producto.nombre)
                        Text("S/ ${producto.precio}")
                    }
                }
            }
            Divider()
            Text("Total: S/ $total", style = MaterialTheme.typography.titleMedium)
            // Dirección de envío seleccionada
            Text("Dirección de envío:")
            if (direcciones.isEmpty()) {
                Text("No tienes direcciones guardadas. Agrega una en tu perfil.", color = MaterialTheme.colorScheme.error)
            } else {
                // Selector de dirección
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = direccionSeleccionada?.direccionCompleta ?: "Selecciona una dirección",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona dirección") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        direcciones.forEach { dir ->
                            DropdownMenuItem(
                                text = { Text(dir.direccionCompleta) },
                                onClick = {
                                    direccionSeleccionada = dir
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                // Mostrar detalles de la dirección seleccionada
                direccionSeleccionada?.let { dir ->
                    Column(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
                        Text("${dir.nombre} ${dir.apellido}")
                        Text(dir.direccionCompleta)
                        Text("${dir.departamento}, ${dir.provincia}, ${dir.distrito}")
                        Text("Tel: ${dir.telefono}")
                    }
                }
            }
            // Método de pago profesional
            Text("Método de pago", style = MaterialTheme.typography.titleMedium)
            ExposedDropdownMenuBox(
                expanded = expandedMetodoPago,
                onExpandedChange = { expandedMetodoPago = !expandedMetodoPago }
            ) {
                TextField(
                    value = metodoPagoSeleccionado?.let { "${it.tipo} •••• ${it.numero.takeLast(4)}" } ?: "Selecciona un método de pago",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Método de pago") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMetodoPago) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedMetodoPago,
                    onDismissRequest = { expandedMetodoPago = false }
                ) {
                    metodosPago.forEach { metodo ->
                        DropdownMenuItem(
                            text = { Text("${metodo.tipo} •••• ${metodo.numero.takeLast(4)}") },
                            onClick = {
                                metodoPagoSeleccionado = metodo
                                expandedMetodoPago = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Botón de confirmar
            Button(
                onClick = {
                    if (cartItems.isEmpty()) {
                        Toast.makeText(context, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (direccionSeleccionada == null) {
                        Toast.makeText(context, "Selecciona una dirección", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (metodoPagoSeleccionado == null) {
                        Toast.makeText(context, "Selecciona un método de pago", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    val user = auth.currentUser
                    val pedido = Pedido(
                        productos = cartItems,
                        total = total,
                        direccion = direccionSeleccionada?.direccionCompleta ?: "",
                        metodoPago = "Método: ${metodoPagoSeleccionado?.tipo}, Número: ${metodoPagoSeleccionado?.numero}",
                        usuarioId = user?.uid ?: "invitado"
                    )
                    db.collection("pedidos")
                        .add(pedido)
                        .addOnSuccessListener {
                            cartViewModel.clearCart()
                            isLoading = false
                            Toast.makeText(context, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show()
                            navController.popBackStack("catalog", false)
                        }
                        .addOnFailureListener {
                            isLoading = false
                            Toast.makeText(context, "Error al guardar el pedido", Toast.LENGTH_LONG).show()
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Confirmar pedido")
            }
        }
    }
}
