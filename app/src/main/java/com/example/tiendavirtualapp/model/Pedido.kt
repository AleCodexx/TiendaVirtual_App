package com.example.tiendavirtualapp.model

import com.example.tiendavirtualapp.model.Producto

// Modelo de datos para un pedido
// Puedes agregar más campos según lo que necesites mostrar en historial, etc.
data class Pedido(
    val id: String = "", // generado por Firestore
    val productos: List<Producto> = emptyList(),
    val total: Double = 0.0,
    val direccion: String = "",
    val metodoPago: String = "",
    val usuarioId: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val estado: String = "pendiente"
)

