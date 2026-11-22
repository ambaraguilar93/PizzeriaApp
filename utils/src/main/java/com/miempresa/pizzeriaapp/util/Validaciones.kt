package com.miempresa.pizzeriaapp.util

const val TELEFONO_POR_DEFECTO = "+56 (000) 000-0000"
const val CORREO_POR_DEFECTO = "correo@invalido.com"

// Regla de precios.
fun formatearPrecio(valor: Int): String = "$" + "%,d".format(valor).replace(',', '.')