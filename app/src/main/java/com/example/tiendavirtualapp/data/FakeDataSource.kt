package com.example.tiendavirtualapp.data

import com.example.tiendavirtualapp.model.Producto

object FakeDataSource {
    val productos = listOf(
        Producto(1, "Auriculares Bluetooth", 59.99, "Sonido envolvente", "Electrónica"),
        Producto(2, "Smartwatch Deportivo", 120.50, "Con monitor de ritmo cardíaco", "Electrónica"),
        Producto(3, "Laptop Gamer", 3500.0, "16GB RAM, RTX 4060", "Electrónica"),
        Producto(4, "Camiseta Básica", 15.99, "100% algodón", "Ropa y accesorios"),
        Producto(5, "Gorra Deportiva", 9.99, "Ajustable, varios colores", "Ropa y accesorios"),
        Producto(6, "Zapatillas Running", 89.99, "Ligeras y transpirables", "Calzado"),
        Producto(7, "Botines de Cuero", 120.0, "Para vestir", "Calzado"),
        Producto(8, "Set de Bloques", 25.50, "100 piezas, colores variados", "Juguetes"),
        Producto(9, "Muñeca Interactiva", 39.99, "Habla y canta", "Juguetes"),
        Producto(10, "Balón de Fútbol", 19.99, "Tamaño oficial", "Deporte y aire libre"),
        Producto(11, "Bicicleta de Montaña", 450.0, "21 velocidades", "Deporte y aire libre"),
        Producto(12, "Set de Maquillaje", 29.99, "Incluye brochas", "Belleza y salud"),
        Producto(13, "Secador de Pelo", 35.99, "Iónico, 2000W", "Belleza y salud"),
        Producto(14, "Juego de Sartenes", 49.99, "Antiadherente, 3 piezas", "Hogar y cocina"),
        Producto(15, "Cafetera Eléctrica", 65.0, "12 tazas", "Hogar y cocina"),
        Producto(16, "Cargador para Auto", 12.99, "USB doble", "Automotriz"),
        Producto(17, "Kit de Emergencia", 39.99, "Incluye cables y herramientas", "Automotriz")
    )
}
