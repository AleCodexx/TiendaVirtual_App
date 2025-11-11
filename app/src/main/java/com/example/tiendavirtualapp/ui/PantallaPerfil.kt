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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendavirtualapp.data.SessionManager
import com.example.tiendavirtualapp.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(navController: NavController, cartViewModel: CartViewModel = viewModel()) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = SessionManager.getUserEmail(context)
    var nombre by remember { mutableStateOf("Cargando...") }

    // 🔹 Traemos nombre del cliente desde Firestore
    LaunchedEffect(userEmail) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val ref = FirebaseFirestore.getInstance().collection("clientes").document(uid)
            ref.get().addOnSuccessListener { snapshot ->
                nombre = snapshot.getString("nombre") ?: "Usuario"
            }.addOnFailureListener {
                nombre = "Usuario"
            }
        } else {
            nombre = "Invitado"
        }
    }

    // obtener inicial para el avatar
    val initial = remember(nombre, userEmail) {
        when {
            nombre.isNotBlank() && nombre != "Cargando..." -> nombre.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
            !userEmail.isNullOrBlank() -> userEmail.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
            else -> "U"
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Perfil 👤") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🔹 Header del perfil mejorado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar con inicial
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .shadow(4.dp, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userEmail ?: "Invitado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (userEmail != null) {
                    ProfileOption(title = "Mis pedidos", icon = Icons.Default.ReceiptLong) {
                        navController.navigate("orders")
                    }
                    ProfileOption(title = "Direcciones", icon = Icons.Default.LocationOn) {
                        navController.navigate("address_list")
                    }
                    ProfileOption(title = "Métodos de pago", icon = Icons.Default.Payment) {
                        navController.navigate("payment_methods")
                    }
                    ProfileOption(title = "Mis reseñas", icon = Icons.Default.Star) {
                        navController.navigate("my_reviews")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            auth.signOut()
                            cartViewModel.onUserChanged() // Limpia el carrito al cerrar sesión
                            SessionManager.logout(context)
                            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("catalog") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cerrar sesión", color = MaterialTheme.colorScheme.onError)
                    }
                } else {
                    Button(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar sesión")
                    }
                    OutlinedButton(
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
fun ProfileOption(title: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.ArrowForward, // mejor affordance para navegación
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
