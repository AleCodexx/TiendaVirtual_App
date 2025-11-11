package com.example.tiendavirtualapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tiendavirtualapp.model.Producto
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    producto: Producto,
    cantidadEnCarrito: Int = 0,
    onAddToCart: () -> Unit = {},
    onClick: () -> Unit = {},
    onAddToCartWithPosition: ((Offset) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp, max = 220.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp, max = 220.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "S/ ${producto.precio}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Box(modifier = Modifier.wrapContentSize()) {
                        var buttonOffsetDp = Offset.Zero
                        val density = LocalDensity.current
                        IconButton(
                            onClick = {
                                // primero ejecutar la acción de añadir (el padre decide si se añade)
                                onAddToCart()
                                // luego reportar la posición para que el padre pueda animar la gota
                                onAddToCartWithPosition?.let { callback ->
                                    callback(buttonOffsetDp)
                                }
                            },
                            modifier = Modifier
                                .onGloballyPositioned { coords ->
                                    val position = coords.localToWindow(Offset.Zero)
                                    // Convert position from pixels to dp-based Offset using density
                                    buttonOffsetDp = with(density) { Offset(position.x.toDp().value, position.y.toDp().value) }
                                }
                        ) {
                            BadgedBox(badge = {
                                if (cantidadEnCarrito > 0) {
                                    Badge { Text(cantidadEnCarrito.toString()) }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AddShoppingCart,
                                    contentDescription = "Agregar al carrito",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
