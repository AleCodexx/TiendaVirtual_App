package com.example.tiendavirtualapp.model

data class Resena(
    val id: String = "",
    val productoId: String = "",
    val orderId: String = "",
    val usuarioEmail: String = "",
    val puntuacion: Int = 5,
    val comentario: String = "",
    val fecha: Long = System.currentTimeMillis()
)

