package com.example.tiendavirtualapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tiendavirtualapp.model.Direccion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioDireccion(navController: NavController) {
    val context = LocalContext.current
    val backStackEntry = navController.currentBackStackEntry
    val id = backStackEntry?.arguments?.getString("id")
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var distrito by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var isEdit by remember { mutableStateOf(false) }

    // Si hay id, cargar datos de Firestore
    LaunchedEffect(id) {
        if (!id.isNullOrBlank()) {
            isEdit = true
            FirebaseFirestore.getInstance().collection("direcciones").document(id).get()
                .addOnSuccessListener { doc ->
                    val d = doc.toObject(Direccion::class.java)
                    if (d != null) {
                        nombre = d.nombre
                        apellido = d.apellido
                        direccion = d.direccionCompleta
                        departamento = d.departamento
                        provincia = d.provincia
                        distrito = d.distrito
                        telefono = d.telefono
                    }
                }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Agregar dirección") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección completa") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = departamento, onValueChange = { departamento = it }, label = { Text("Departamento") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = provincia, onValueChange = { provincia = it }, label = { Text("Provincia") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = distrito, onValueChange = { distrito = it }, label = { Text("Distrito") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono de contacto") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (
                        nombre.isNotBlank() && apellido.isNotBlank() && direccion.isNotBlank() &&
                        departamento.isNotBlank() && provincia.isNotBlank() && distrito.isNotBlank() && telefono.isNotBlank() && userId != null
                    ) {
                        val direccionObj = Direccion(
                            nombre = nombre,
                            apellido = apellido,
                            direccionCompleta = direccion,
                            departamento = departamento,
                            provincia = provincia,
                            distrito = distrito,
                            telefono = telefono,
                            usuarioId = userId
                        )
                        val db = FirebaseFirestore.getInstance().collection("direcciones")
                        if (isEdit && !id.isNullOrBlank()) {
                            db.document(id).set(direccionObj, SetOptions.merge())
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Dirección actualizada exitosamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Error al actualizar la dirección",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            db.add(direccionObj)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Dirección guardada exitosamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Error al guardar la dirección",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Completa todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEdit) "Actualizar" else "Guardar")
            }
        }
    }
}
