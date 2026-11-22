package com.miempresa.pizzeriaapp.util

// Operaciones de formato reutilizables en cualquier módulo.
fun formatearPrecio(valor: Int): String = "$" + "%,d".format(valor).replace(',', '.')