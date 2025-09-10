package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class Categoria(val nombre: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoriesScreen() {
    var query by remember { mutableStateOf("") }

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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filtered) { categoria ->
                CategoriaCircle(categoria.nombre)
            }
        }
    }
}
@Composable
fun CategoriaCircle(nombre: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
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
