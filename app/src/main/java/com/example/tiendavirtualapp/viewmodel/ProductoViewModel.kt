package com.example.tiendavirtualapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendavirtualapp.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("productos").get()
                .addOnSuccessListener { result ->
                    val lista = mutableListOf<Producto>()
                    for (document in result) {
                        val producto = document.toObject(Producto::class.java)
                        lista.add(producto)
                    }
                    _productos.value = lista
                }
        }
    }
}
