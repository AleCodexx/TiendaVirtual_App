package com.example.tiendavirtualapp.model

data class Producto(
    val id: String = "",          // Firebase usa String como key (evitamos Int fijo)
    val nombre: String = "",
    val precio: Double = 0.0,
    val descripcion: String = "",
    val categoria: String = "",
    val imagenUrl: String = ""    // 👈 conviene agregarlo para mostrar fotos en el catálogo
)
