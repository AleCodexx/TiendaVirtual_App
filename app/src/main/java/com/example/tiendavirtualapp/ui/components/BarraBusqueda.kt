package com.example.tiendavirtualapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BarraBusqueda(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar productos..."
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

