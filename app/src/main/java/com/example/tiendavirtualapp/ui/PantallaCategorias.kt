package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tiendavirtualapp.model.Producto
import com.example.tiendavirtualapp.viewmodel.ProductoViewModel
import com.example.tiendavirtualapp.ui.components.CategoryChip
import com.example.tiendavirtualapp.ui.components.ProductCard
import com.example.tiendavirtualapp.ui.components.BarraBusqueda
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyItems


data class Categoria(val nombre: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaCategorias(viewModel: ProductoViewModel = viewModel(), onProductClick: (String) -> Unit = {}) {
    val productos by viewModel.productos.collectAsState()
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Categorías dinámicas
    val categorias = productos.map { it.categoria }.distinct().map { Categoria(it) }

    // Filtro por búsqueda sobre categorías
    val filteredCategorias = categorias.filter { it.nombre.contains(query, ignoreCase = true) }

    // Productos de la categoría seleccionada
    val productosFiltrados: List<Producto> = selectedCategory?.let { cat ->
        productos.filter { it.categoria.equals(cat, ignoreCase = true) }
    } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BarraBusqueda(
                        query = query,
                        onQueryChange = { query = it },
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
            // Grid de categorías
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredCategorias) { categoria ->
                    CategoryChip(
                        nombre = categoria.nombre,
                        onClick = { selectedCategory = categoria.nombre }
                    )
                }
            }

            // Productos de la categoría seleccionada
            if (selectedCategory != null) {
                Text(
                    text = "Productos en ${selectedCategory}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    lazyItems(productosFiltrados) { producto ->
                        ProductCard(
                            producto = producto,
                            onClick = { onProductClick(producto.id) }
                        )
                    }
                }
            }
        }
    }
}
