package com.example.tiendavirtualapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendavirtualapp.model.Producto
import com.google.firebase.database.FirebaseDatabase
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
            val dbRef = FirebaseDatabase.getInstance().getReference("productos")
            dbRef.get().addOnSuccessListener { snapshot ->
                val lista = mutableListOf<Producto>()
                for (item in snapshot.children) {
                    val producto = item.getValue(Producto::class.java)
                    producto?.let { lista.add(it) }
                }
                _productos.value = lista
            }
        }
    }
}
