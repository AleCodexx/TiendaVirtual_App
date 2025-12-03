package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCambioContrasena(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val userEmail = user?.email

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    fun validateForm(): String? {
        if (newPassword.length < 6) return "La nueva contraseña debe tener al menos 6 caracteres"
        if (newPassword != confirmPassword) return "Las contraseñas no coinciden"
        return null
    }

    fun updatePasswordWithReauth() {
        val validationError = validateForm()
        if (validationError != null) {
            scope.launch { snackbarHostState.showSnackbar(validationError) }
            return
        }
        if (user == null || userEmail.isNullOrBlank()) {
            scope.launch { snackbarHostState.showSnackbar("No hay usuario autenticado") }
            return
        }
        // a partir de aquí userEmail es no-null
        val email = userEmail!!

        isLoading = true
        // Si el usuario ingresó la contraseña actual, reautenticar
        if (currentPassword.isNotBlank()) {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        isLoading = false
                        if (updateTask.isSuccessful) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Contraseña actualizada correctamente")
                            }
                            navController.popBackStack()
                        } else {
                            scope.launch { snackbarHostState.showSnackbar(updateTask.exception?.localizedMessage ?: "Error al actualizar contraseña") }
                        }
                    }
                } else {
                    isLoading = false
                    scope.launch { snackbarHostState.showSnackbar(reauthTask.exception?.localizedMessage ?: "Reautenticación fallida. Verifica tu contraseña actual.") }
                }
            }
        } else {
            // Si no ingreso contraseña actual, pedir restablecimiento por email
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    scope.launch { snackbarHostState.showSnackbar("Se ha enviado un email para restablecer la contraseña a $email") }
                    navController.popBackStack()
                } else {
                    scope.launch { snackbarHostState.showSnackbar(task.exception?.localizedMessage ?: "Error al enviar email de restablecimiento") }
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cambiar contraseña") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Cuenta: ${userEmail ?: "(no autenticado)"}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Contraseña actual (opcional)") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva contraseña") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar nueva contraseña") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = passwordVisible, onCheckedChange = { passwordVisible = it })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mostrar contraseñas")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { updatePasswordWithReauth() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Procesando...")
                } else {
                    Text("Actualizar contraseña / Enviar email de restablecimiento")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Si no recuerdas tu contraseña actual, deja el campo vacío y se te enviará un email para restablecerla.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}