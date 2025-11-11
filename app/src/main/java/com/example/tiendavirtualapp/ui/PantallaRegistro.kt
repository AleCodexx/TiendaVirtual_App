package com.example.tiendavirtualapp.ui

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

private const val PASSWORD_MIN_LENGTH = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun validateNombre(): Boolean {
        return if (nombre.isBlank()) {
            nombreError = "El nombre no puede estar vacío"
            false
        } else {
            nombreError = null
            true
        }
    }

    fun validateEmail(): Boolean {
        return if (email.isBlank()) {
            emailError = "El correo no puede estar vacío"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = "Correo no válido"
            false
        } else {
            emailError = null
            true
        }
    }

    fun validatePassword(): Boolean {
        return if (password.isBlank()) {
            passwordError = "La contraseña no puede estar vacía"
            false
        } else if (password.length < PASSWORD_MIN_LENGTH) {
            passwordError = "La contraseña debe tener al menos $PASSWORD_MIN_LENGTH caracteres"
            false
        } else {
            passwordError = null
            true
        }
    }

    // estado combinando validaciones
    val isFormValid by derivedStateOf {
        validateNombre() && validateEmail() && validatePassword()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registro") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    if (nombreError != null) validateNombre()
                },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError != null
            )
            nombreError?.let { Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)) }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) validateEmail()
                },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
                isError = emailError != null
            )
            emailError?.let { Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)) }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) validatePassword()
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, contentDescription = "Toggle contraseña")
                    }
                },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Contraseña") },
                isError = passwordError != null
            )
            passwordError?.let { Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)) }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // Re-validate before submitting
                    val v1 = validateNombre()
                    val v2 = validateEmail()
                    val v3 = validatePassword()
                    if (!v1 || !v2 || !v3) return@Button

                    loading = true
                    auth.createUserWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener { task ->
                            loading = false
                            if (task.isSuccessful) {
                                val uid = auth.currentUser?.uid ?: ""
                                val userMap = mapOf(
                                    "nombre" to nombre,
                                    "email" to email
                                )
                                db.collection("clientes").document(uid).set(userMap)

                                Toast.makeText(context, "Registro exitoso 🎉", Toast.LENGTH_SHORT).show()
                                navController.popBackStack() // vuelve al login
                            } else {
                                val msg = task.exception?.localizedMessage ?: "Error al registrar"
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading && isFormValid
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrando...")
                } else {
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Volver al login")
            }
        }
    }
}
