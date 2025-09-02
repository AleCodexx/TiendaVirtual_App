package com.example.tiendavirtualapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendavirtualapp.data.FakeDataSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Administrar productos") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* luego abrir formulario */ }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(FakeDataSource.productos) { producto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = producto.descripcion, style = MaterialTheme.typography.bodyMedium)
                        Text("Precio: S/ ${producto.precio}", style = MaterialTheme.typography.bodyMedium)
                        Row {
                            Button(onClick = { /* Acción editar */ }) {
                                Text("Editar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { /* Acción eliminar */ },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}