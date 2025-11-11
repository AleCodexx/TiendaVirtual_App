package com.example.tiendavirtualapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendavirtualapp.model.Producto
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme

@Composable
fun ProductosRelacionados(
    productos: List<Producto>,
    onProductClick: (Producto) -> Unit
) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(productos) { prod ->
            Column(
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onProductClick(prod) }
            ) {
                AsyncImage(
                    model = prod.imagenUrl,
                    contentDescription = prod.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(110.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(prod.descripcion, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("S/ ${prod.precio}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            }
        }
    }
}
