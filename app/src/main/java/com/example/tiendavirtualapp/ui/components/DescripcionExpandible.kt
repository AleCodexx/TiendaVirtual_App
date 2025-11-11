package com.example.tiendavirtualapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun DescripcionExpandible(
    text: String,
    modifier: Modifier = Modifier,
    maxCollapsedLines: Int = 3
) {
    val expanded = remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = if (expanded.value) Int.MAX_VALUE else maxCollapsedLines,
            overflow = if (expanded.value) TextOverflow.Visible else TextOverflow.Ellipsis
        )
        if (!expanded.value) {
            Text(
                text = "Ver más",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clickable { expanded.value = true }
            )
        }
    }
}

