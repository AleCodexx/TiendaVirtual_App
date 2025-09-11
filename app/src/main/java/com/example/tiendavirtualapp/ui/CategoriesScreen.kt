package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiendavirtualapp.data.FakeDataSource
import com.example.tiendavirtualapp.model.Producto

data class Categoria(val nombre: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoriesScreen() {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val categorias = listOf(
        Categoria("Electrónica"),
        Categoria("Celulares"),
        Categoria("Ropa y accesorios"),
        Categoria("Calzado"),
        Categoria("Juguetes"),
        Categoria("Deporte y aire libre"),
        Categoria("Belleza y salud"),
        Categoria("Hogar y cocina"),
        Categoria("Automotriz"),
    )

    val filtered = categorias.filter {
        it.nombre.contains(query, ignoreCase = true)
    }

    val productosFiltrados: List<Producto> = selectedCategory?.let { cat ->
        FakeDataSource.productos.filter { it.categoria.equals(cat, ignoreCase = true) }
    } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Buscar categorías...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🔹 Grid de categorías
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f), // ocupa parte superior
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filtered) { categoria ->
                    CategoriaCircle(
                        nombre = categoria.nombre,
                        onClick = { selectedCategory = categoria.nombre }
                    )
                }
            }

            // 🔹 Productos relacionados
            if (selectedCategory != null) {
                Text(
                    text = "Productos en ${selectedCategory}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    productosFiltrados.forEach { producto ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(producto.descripcion, style = MaterialTheme.typography.bodySmall)
                                Text("S/ ${producto.precio}", style = MaterialTheme.typography.bodyMedium)

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriaCircle(nombre: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(90.dp)
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        // Círculo placeholder
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            modifier = Modifier.size(70.dp)
        ) {}
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = nombre,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 13.sp,
            maxLines = 2
        )
    }
}
