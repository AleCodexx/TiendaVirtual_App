package com.example.tiendavirtualapp.util

import java.util.Locale

/**
 * Formatea un precio a 2 decimales usando el locale del dispositivo.
 */
fun formatPrice(value: Double): String = String.format(Locale.getDefault(), "%.2f", value)

