package com.example.tiendavirtualapp.utils

import com.example.tiendavirtualapp.model.Producto
import com.google.firebase.database.FirebaseDatabase

object DataUploader {

    fun insertarProductosIniciales() {
        val dbRef = FirebaseDatabase.getInstance().getReference("productos")

        val productos = listOf(
            Producto(
                id = dbRef.push().key ?: "",
                nombre = "Laptop Gamer",
                precio = 3500.0,
                descripcion = "Laptop con Ryzen 7 y RTX 4060",
                categoria = "Electrónica",
                imagenUrl = "https://via.placeholder.com/300x400.png?text=Laptop"
            )
        )

        productos.forEach { producto ->
            dbRef.child(producto.id).setValue(producto)
        }
    }
}
