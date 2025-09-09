package com.example.tiendavirtualapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendavirtualapp.data.repository.ProductoRepository
import com.example.tiendavirtualapp.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {

    private val repository = ProductoRepository()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    init {
        fetchProductos()
    }

    private fun fetchProductos() {
        viewModelScope.launch {
            try {
                val response = repository.getProductos()
                _productos.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
