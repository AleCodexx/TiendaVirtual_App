package com.example.tiendavirtualapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tiendavirtualapp.model.Producto

@Composable
fun ImagenProducto(
    modifier: Modifier = Modifier,
    producto: Producto,
    onBack: () -> Unit = {},
    onSearch: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
    ) {
        AsyncImage(
            model = producto.imagenUrl,
            contentDescription = producto.nombre,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(color = Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(50))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            IconButton(
                onClick = onSearch,
                modifier = Modifier
                    .background(color = Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(50))
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Buscar")
            }
        }
    }
}
