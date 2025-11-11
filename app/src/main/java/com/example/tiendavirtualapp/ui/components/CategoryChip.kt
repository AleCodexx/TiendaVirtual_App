package com.example.tiendavirtualapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryChip(
    nombre: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(90.dp)
            .padding(4.dp)
            .clickable { onClick() }
    ) {
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

