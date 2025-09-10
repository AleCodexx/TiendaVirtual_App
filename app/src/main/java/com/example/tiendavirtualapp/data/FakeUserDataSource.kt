package com.example.tiendavirtualapp.data

object FakeUserDataSource {

    val adminUser = User(
        email = "admin@tienda.com",
        password = "123456",
        role = "admin"
    )


    val clientUser = User(
        email = "cliente@tienda.com",
        password = "654321",
        role = "cliente"
    )


    val users = mutableListOf(adminUser, clientUser)
}

data class User(
    val email: String,
    val password: String,
    val role: String // "admin" o "cliente"
)
