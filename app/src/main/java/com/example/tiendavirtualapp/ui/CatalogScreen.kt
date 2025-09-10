package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendavirtualapp.data.FakeDataSource
import com.example.tiendavirtualapp.model.Producto

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CatalogScreen() {
    val productos = FakeDataSource.productos
    var query by remember { mutableStateOf("") }
    val filtered = productos.filter {
        it.nombre.contains(query, ignoreCase = true) ||
                it.descripcion.contains(query, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Buscar productos...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { producto ->
                ProductoItem(producto)
            }
        }
    }
}

@Composable
fun ProductoItem(producto: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Aquí podrías poner una imagen de producto con Coil o painterResource
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            ) {
                // Imagen simulada (placeholder)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                ) {}
            }

            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = producto.descripcion,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            Text(
                text = "S/ ${producto.precio}",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Agregar al carrito")
            }
        }
    }
}
