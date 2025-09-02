package com.example.tiendavirtualapp.data


import com.example.tiendavirtualapp.model.Producto

// Simulación de productos (antes de Firebase)
object FakeDataSource {
    val productos = listOf(
        Producto(1, "Auriculares Bluetooth", 59.99, "Sonido envolvente"),
        Producto(2, "Smartwatch Deportivo", 120.50, "Con monitor de ritmo cardíaco"),
        Producto(3, "Laptop Gamer", 3500.0, "16GB RAM, RTX 4060"),
        Producto(4, "Cámara Digital", 450.75, "20MP, Zoom óptico 10x")

    )
}
