package com.example.tiendavirtualapp.viewmodel


import androidx.lifecycle.ViewModel
import com.example.tiendavirtualapp.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<Producto>>(emptyList())
    val cartItems: StateFlow<List<Producto>> = _cartItems

    fun addToCart(producto: Producto) {
        _cartItems.value = _cartItems.value + producto
    }

    fun removeFromCart(producto: Producto) {
        _cartItems.value = _cartItems.value - producto
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}
