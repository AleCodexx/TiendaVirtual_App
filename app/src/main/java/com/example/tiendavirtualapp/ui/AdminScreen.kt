package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendavirtualapp.data.FakeDataSource
import com.example.tiendavirtualapp.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen() {
    val productos = FakeDataSource.productos

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrar productos ⚙️") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: Abrir formulario para agregar nuevo producto
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    ) { padding ->
        if (productos.isEmpty()) {
            // 🟢 Mensaje cuando no hay productos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos registrados todavía 🚫")
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productos) { producto ->
                    ProductoAdminItem(
                        producto = producto,
                        onEditar = { /* TODO: implementar edición */ },
                        onEliminar = { /* TODO: implementar eliminación */ }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoAdminItem(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(producto.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Precio: S/ ${producto.precio}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(onClick = onEditar) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onEliminar,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}
