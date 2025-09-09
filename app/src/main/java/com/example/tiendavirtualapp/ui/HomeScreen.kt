package com.example.tiendavirtualapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tienda Virtual") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Bienvenido 👋")

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.navigate("catalog") }) {
                Text("Ver productos 🛒")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { navController.navigate("admin") }) {
                Text("Administrar productos ⚙️")
            }

            
        }
    }
}

