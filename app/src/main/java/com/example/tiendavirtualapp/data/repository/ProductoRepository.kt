package com.example.tiendavirtualapp.data.repository

import com.example.tiendavirtualapp.data.remote.ApiService
import com.example.tiendavirtualapp.model.Producto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductoRepository {

    private val api: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://68bf937d9c70953d96efe276.mockapi.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
    }

    suspend fun getProductos(): List<Producto> {
        return api.getProductos()
    }
}