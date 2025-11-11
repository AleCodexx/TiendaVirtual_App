package com.example.tiendavirtualapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp

@Composable
fun SelectorCantidad(
    cantidadState: MutableState<Int>,
    modifier: Modifier = Modifier,
    min: Int = 1,
    max: Int = 5,
    size: Dp = 32.dp
) {
    Row(horizontalArrangement = Arrangement.Start, modifier = modifier) {
        Button(
            onClick = { if (cantidadState.value > min) cantidadState.value-- },
            enabled = cantidadState.value > min,
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.width(size)
        ) {
            Text("-")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = cantidadState.value.toString(), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { if (cantidadState.value < max) cantidadState.value++ },
            enabled = cantidadState.value < max,
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.width(size)
        ) {
            Text("+")
        }
    }
}
