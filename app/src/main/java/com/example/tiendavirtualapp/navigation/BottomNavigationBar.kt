package com.example.tiendavirtualapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity

@Composable
fun BottomNavigationBar(navController: NavController, onCartIconPositionChanged: ((Offset) -> Unit)? = null) {
    val items = listOf(
        BottomNavItem("Inicio", "catalog", Icons.Default.Home),
        BottomNavItem("Categorías", "categories", Icons.Default.List),
        BottomNavItem("Tú", "profile", Icons.Default.Person),
        BottomNavItem("Carrito", "cart", Icons.Default.ShoppingCart)

    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = navBackStackEntry?.destination?.route

        val shouldHideBar = currentRoute?.startsWith("detalle") == true
        if (shouldHideBar) return@NavigationBar

        val density = LocalDensity.current

        items.forEach { item ->
            val iconModifier = if (item.route == "cart") {
                Modifier.onGloballyPositioned { coords ->
                    val position = coords.localToWindow(Offset.Zero)
                    // convert to dp-based Offset
                    val offsetDp = with(density) { Offset(position.x.toDp().value, position.y.toDp().value) }
                    onCartIconPositionChanged?.invoke(offsetDp)
                }
            } else Modifier

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label, modifier = iconModifier) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("catalog") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val label: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
