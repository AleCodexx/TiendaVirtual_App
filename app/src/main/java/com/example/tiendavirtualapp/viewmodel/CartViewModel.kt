package com.example.tiendavirtualapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendavirtualapp.model.Producto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<Producto>>(emptyList())
    val cartItems: StateFlow<List<Producto>> = _cartItems

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getCartCollection() =
        auth.currentUser?.let { db.collection("carts").document(it.uid).collection("items") }

    init {
        loadCartFromFirestore()
    }

    private fun loadCartFromFirestore() {
        val collection = getCartCollection() ?: return
        collection.get().addOnSuccessListener { snapshot ->
            val productos = snapshot.documents.mapNotNull { it.toObject(Producto::class.java) }
            _cartItems.value = productos
        }
    }

    fun addToCart(producto: Producto) {
        _cartItems.value = _cartItems.value + producto
        val collection = getCartCollection() ?: return
        collection.document(producto.id ?: producto.hashCode().toString()).set(producto)
    }

    fun removeFromCart(producto: Producto) {
        _cartItems.value = _cartItems.value - producto
        val collection = getCartCollection() ?: return
        collection.document(producto.id ?: producto.hashCode().toString()).delete()
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        val collection = getCartCollection() ?: return
        // Borra todos los productos del carrito en Firestore
        collection.get().addOnSuccessListener { snapshot ->
            for (doc in snapshot.documents) {
                doc.reference.delete()
            }
        }
    }

    fun onUserChanged() {
        // Llama esto al iniciar/cerrar sesión para recargar el carrito
        _cartItems.value = emptyList()
        loadCartFromFirestore()
    }
}
