package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendavirtualapp.data.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val user = SessionManager.currentUser

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Perfil 👤") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🔹 Header del perfil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = user?.email ?: "Invitado",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // 🔹 Rol del usuario (badge)
                    user?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (it.role == "admin") "Administrador" else "Cliente",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (it.role == "admin")
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Gray
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Opciones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (user != null) {
                    if (user.role == "admin") {
                        // Solo para admin
                        ProfileOption("Administrar productos ⚙️") {
                            navController.navigate("admin")
                        }
                    } else {
                        // Solo para usuarios normales
                        ProfileOption("Mis pedidos")
                        ProfileOption("Direcciones")
                        ProfileOption("Métodos de pago")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            SessionManager.currentUser = null
                            navController.navigate("login") {
                                popUpTo("catalog") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar sesión")
                    }
                } else {
                    Button(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar sesión")
                    }
                    Button(
                        onClick = { navController.navigate("register") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrarse")
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileOption(title: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
