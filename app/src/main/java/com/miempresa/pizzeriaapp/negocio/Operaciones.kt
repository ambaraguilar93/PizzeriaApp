package com.miempresa.pizzeriaapp.negocio

import com.miempresa.pizzeriaapp.modelo.CategoriaProducto
import com.miempresa.pizzeriaapp.modelo.Producto
import kotlin.math.roundToInt

// Reglas de negocio
const val TASA_IVA = 19
const val PIZZAS_PARA_DESCUENTO = 3
const val PORCENTAJE_DESCUENTO = 10

// Resultado del cálculo de un pedido
data class Totales(
    val subtotal: Int,
    val descuento: Int,
    val impuestos: Int,
    val total: Int
)

fun contarPizzas(productos: List<Producto>): Int =
    productos.count { it.categoria == CategoriaProducto.PIZZA }

fun calcularDescuento(subtotal: Int, cantidadPizzas: Int): Int =
    if (cantidadPizzas > PIZZAS_PARA_DESCUENTO) subtotal * PORCENTAJE_DESCUENTO / 100 else 0

// Total (subtotal, descuento e IVA).
fun calcularTotales(productos: List<Producto>): Totales {
    val subtotal = productos.sumOf { it.precio }
    val descuento = calcularDescuento(subtotal, contarPizzas(productos))
    val base = subtotal - descuento
    val impuestos = (base * TASA_IVA / 100.0).roundToInt()
    return Totales(subtotal, descuento, impuestos, base + impuestos)
}