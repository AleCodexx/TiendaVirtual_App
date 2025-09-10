package com.example.tiendavirtualapp.data

import com.example.tiendavirtualapp.model.Producto

// Simulación de productos (antes de Firebase)
object FakeDataSource {
    val productos = listOf(
        Producto(1, "Auriculares Bluetooth", 59.99, "Sonido envolvente"),
        Producto(2, "Smartwatch Deportivo", 120.50, "Con monitor de ritmo cardíaco"),
        Producto(3, "Laptop Gamer", 3500.0, "16GB RAM, RTX 4060"),
        Producto(4, "Camiseta Básica", 15.99, "100% algodón"),
        Producto(5, "Gorra Deportiva", 9.99, "Ajustable, varios colores"),
        Producto(6, "Zapatillas Running", 89.99, "Ligeras y transpirables"),
        Producto(7, "Botines de Cuero", 120.0, "Para vestir"),
        Producto(8, "Set de Bloques", 25.50, "100 piezas, colores variados"),
        Producto(9, "Muñeca Interactiva", 39.99, "Habla y canta"),
        Producto(10, "Balón de Fútbol", 19.99, "Tamaño oficial"),
        Producto(11, "Bicicleta de Montaña", 450.0, "21 velocidades"),
        Producto(12, "Set de Maquillaje", 29.99, "Incluye brochas"),
        Producto(13, "Secador de Pelo", 35.99, "Iónico, 2000W"),
        Producto(14, "Juego de Sartenes", 49.99, "Antiadherente, 3 piezas"),
        Producto(15, "Cafetera Eléctrica", 65.0, "12 tazas"),
        Producto(16, "Cargador para Auto", 12.99, "USB doble"),
        Producto(17, "Kit de Emergencia", 39.99, "Incluye cables y herramientas")
    )
}
