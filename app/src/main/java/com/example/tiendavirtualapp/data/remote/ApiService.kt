package com.example.tiendavirtualapp.data.remote

import com.example.tiendavirtualapp.model.Producto
import retrofit2.http.GET

interface ApiService {
    @GET("productos")
    suspend fun getProductos(): List<Producto>
}