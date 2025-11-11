package com.example.tiendavirtualapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tiendavirtualapp.model.Producto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
            val productosExpanded = mutableListOf<Producto>()
            for (doc in snapshot.documents) {
                val prod = doc.toObject(Producto::class.java)
                val cantidad = doc.getLong("cantidad")?.toInt() ?: 1
                if (prod != null) {
                    repeat(cantidad) { productosExpanded.add(prod) }
                }
            }
            _cartItems.value = productosExpanded
        }
    }

    fun addToCart(producto: Producto) {
        // Añade una unidad en memoria
        _cartItems.value = _cartItems.value + producto
        // Sincroniza en Firestore: aumenta o crea el documento con campo 'cantidad'
        val collection = getCartCollection() ?: return
        val docRef = collection.document(producto.id)
        val newCount = _cartItems.value.count { it.id == producto.id }
        val data = mapOf(
            "id" to producto.id,
            "nombre" to producto.nombre,
            "precio" to producto.precio,
            "descripcion" to producto.descripcion,
            "categoria" to producto.categoria,
            "imagenUrl" to producto.imagenUrl,
            "cantidad" to newCount
        )
        docRef.set(data)
    }

    fun removeFromCart(producto: Producto) {
        // Elimina una unidad en memoria
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.id == producto.id }
        if (index >= 0) {
            current.removeAt(index)
            _cartItems.value = current
        }
        // Actualizar en Firestore: decrementar cantidad o borrar documento
        val collection = getCartCollection() ?: return
        val docRef = collection.document(producto.id)
        val newCount = _cartItems.value.count { it.id == producto.id }
        if (newCount > 0) {
            // obtener producto actual para enviar datos
            val prod = _cartItems.value.firstOrNull { it.id == producto.id } ?: producto
            val data = mapOf(
                "id" to prod.id,
                "nombre" to prod.nombre,
                "precio" to prod.precio,
                "descripcion" to prod.descripcion,
                "categoria" to prod.categoria,
                "imagenUrl" to prod.imagenUrl,
                "cantidad" to newCount
            )
            docRef.set(data)
        } else {
            docRef.delete()
        }
    }

    fun decreaseFromCart(producto: Producto) {
        // Alias de removeFromCart para claridad
        removeFromCart(producto)
    }

    fun removeAllOfProduct(producto: Producto) {
        // Elimina todas las unidades del producto en memoria
        _cartItems.value = _cartItems.value.filter { it.id != producto.id }
        // Borra del servidor
        val collection = getCartCollection() ?: return
        collection.document(producto.id).delete()
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
