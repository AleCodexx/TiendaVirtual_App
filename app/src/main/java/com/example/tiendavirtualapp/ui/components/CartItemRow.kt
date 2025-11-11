package com.example.tiendavirtualapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.example.tiendavirtualapp.model.Producto
import com.example.tiendavirtualapp.util.formatPrice
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun CartItemRow(
    modifier: Modifier = Modifier,
    producto: Producto,
    cantidad: Int = 1,
    onIncrease: () -> Unit = {},
    onDecrease: () -> Unit = {},
    onRemove: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, maxLines = 2)
                // Formatear precio del producto a 2 decimales
                val precioFormateado = formatPrice(producto.precio)
                Text("S/ $precioFormateado", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Controles de cantidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, enabled = cantidad > 1) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                }
                Text(text = cantidad.toString(), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.CenterVertically))
                IconButton(onClick = onIncrease) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onRemove,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartItemRowPreview() {
    val producto = Producto(
        id = "p1",
        nombre = "Camiseta de prueba",
        precio = 49.99,
        descripcion = "Una camiseta cómoda",
        categoria = "Ropa",
        imagenUrl = ""
    )
    val cantidadState = remember { mutableStateOf(2) }
    CartItemRow(
        producto = producto,
        cantidad = cantidadState.value,
        onIncrease = { cantidadState.value++ },
        onDecrease = { if (cantidadState.value > 1) cantidadState.value-- },
        onRemove = {}
    )
}
