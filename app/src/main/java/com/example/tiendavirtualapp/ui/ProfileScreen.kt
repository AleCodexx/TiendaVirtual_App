package com.example.tiendavirtualapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendavirtualapp.data.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = SessionManager.getUserEmail(context)

    var nombre by remember { mutableStateOf("Cargando...") }

    // 🔹 Traemos nombre del cliente desde Firebase Realtime Database
    LaunchedEffect(userEmail) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val ref = FirebaseDatabase.getInstance().getReference("clientes").child(uid)
            ref.get().addOnSuccessListener { snapshot ->
                nombre = snapshot.child("nombre").getValue(String::class.java) ?: "Usuario"
            }.addOnFailureListener {
                nombre = "Usuario"
            }
        } else {
            nombre = "Invitado"
        }
    }

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
                        text = nombre,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userEmail ?: "Invitado",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (userEmail != null) {
                    ProfileOption("Mis pedidos")
                    ProfileOption("Direcciones")
                    ProfileOption("Métodos de pago")

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            auth.signOut()
                            SessionManager.logout(context)
                            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
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
