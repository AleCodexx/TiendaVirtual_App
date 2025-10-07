package com.example.tiendavirtualapp.utils

import com.example.tiendavirtualapp.model.Producto
import com.google.firebase.firestore.FirebaseFirestore

object DataUploader {

    fun insertarProductosIniciales() {
        val db = FirebaseFirestore.getInstance()
        val productosCollection = db.collection("productos")

        val productos = listOf(
            Producto(
                id = productosCollection.document().id,
                nombre = "Zapatillas Running Hombre",
                precio = 109.99,
                descripcion = "Zapatillas deportivas ligeras y transpirables para correr, ideales para entrenamiento diario.",
                categoria = "Calzado",
                imagenUrl = "https://img.kwcdn.com/product/fancy/9f27059d-3e67-41f8-b33d-a65b3bbff8c2.jpg?imageView2/2/w/800/q/70/format/webp"
            ),
            Producto(
                id = productosCollection.document().id,
                nombre = "Set de Brochas de Maquillaje",
                precio = 9.99,
                descripcion = "Set profesional de brochas de maquillaje con estuche, cerdas suaves y resistentes.",
                categoria = "Belleza y salud",
                imagenUrl = "https://img.kwcdn.com/product/fancy/52cf02c3-77c6-4a43-975e-6ef8b881eef9.jpg?imageView2/2/w/800/q/70/format/webp"
            ),
            Producto(
                id = productosCollection.document().id,
                nombre = "Robot Aspirador Inteligente",
                precio = 69.80,
                descripcion = "Robot aspirador con sensores inteligentes, programación automática y control por app.",
                categoria = "Hogar y cocina",
                imagenUrl = "https://img.kwcdn.com/product/open/47b967d97a0946b7a8c55b1df15e6fc2-goods.jpeg?imageView2/2/w/800/q/70/format/webp"
            ),
            Producto(
                id = productosCollection.document().id,
                nombre = "Camiseta Oversize Unisex",
                precio = 19.99,
                descripcion = "Camiseta de algodón oversize, disponible en varios colores y tallas.",
                categoria = "Ropa y accesorios",
                imagenUrl = "https://img.kwcdn.com/product/fancy/5ddebe2c-ed50-4572-bd5f-37a3bcb0ad35.jpg?imageView2/2/w/800/q/70/format/webp"
            ),
            Producto(
                id = productosCollection.document().id,
                nombre = "Set de Herramientas Automotriz 151 piezas",
                precio = 25.10,
                descripcion = "Set completo de herramientas para auto, incluye maletín resistente.",
                categoria = "Automotriz",
                imagenUrl = "https://img.kwcdn.com/product/fancy/14754d98-bbfc-4952-b9d0-bb6b987614bf.jpg?imageView2/2/w/800/q/70/format/webp"
            ),
            Producto(
                id = productosCollection.document().id,
                nombre = "Balón de Fútbol Profesional",
                precio = 51.90,
                descripcion = "Balón de fútbol tamaño oficial, material resistente y costuras reforzadas.",
                categoria = "Deporte y aire libre",
                imagenUrl = "https://img.kwcdn.com/product/fancy/7331b9a0-5592-42cf-ac50-d15e59b96ed1.jpg?imageView2/2/w/800/q/70/format/webp"
            ),
            Producto(
                id = productosCollection.document().id,
                nombre = "Set de Bloques de Construcción 500 piezas",
                precio = 67.25,
                descripcion = "Juguete educativo de bloques de construcción para niños, colores variados.",
                categoria = "Juguetes",
                imagenUrl = "https://img.kwcdn.com/product/fancy/c15e3871-2f25-433e-b6d4-21b682cc521c.jpg?imageView2/2/w/800/q/70/format/webp"
            )
        )

        productos.forEach { producto ->
            productosCollection.document(producto.id).set(producto)
        }
    }
}
