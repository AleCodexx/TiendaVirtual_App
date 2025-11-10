package com.example.tiendavirtualapp.model

// Modelo de método de pago utilizado por la UI y por Firestore
data class MetodoPago(
    val id: String = "",
    val tipo: String = "Tarjeta",
    val numero: String = "",
    val vencimiento: String = "",
    val cvv: String = "",
    val titular: String = "",
    val usuarioEmail: String = ""
)

