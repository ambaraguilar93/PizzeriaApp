package com.miempresa.pizzeriaapp.negocio


import com.miempresa.pizzeriaapp.modelo.CategoriaProducto
import com.miempresa.pizzeriaapp.modelo.Producto
import com.miempresa.pizzeriaapp.modelo.EstadoPedido
import com.miempresa.pizzeriaapp.modelo.Pedido
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

// Resumen de ventas que se muestra en la pantalla de pedidos.
data class EstadisticasVentas(
    val totalRecaudado: Int,
    val cantidadPedidos: Int,
    val ticketPromedio: Int,
    val pendientes: Int,
    val entregados: Int,
    val unidadesPorProducto: List<Pair<String, Int>>,   // (producto, unidades), de mayor a menor
    val unidadesTotales: Int
) {
    // El primero de la lista es el más vendido (si hay empate, el que se vendió primero).
    val masVendido: String? get() = unidadesPorProducto.firstOrNull()?.first
}

// Calcula las estadísticas a partir de los pedidos registrados.
fun calcularEstadisticas(pedidos: List<Pedido>): EstadisticasVentas {
    val vendidos = pedidos.flatMap { it.productos }
    val totalRecaudado = pedidos.sumOf { it.calcularTotal() }   // con descuentos e IVA
    val entregados = pedidos.count { it.estado == EstadoPedido.ENTREGADO }

    return EstadisticasVentas(
        totalRecaudado = totalRecaudado,
        cantidadPedidos = pedidos.size,
        ticketPromedio = if (pedidos.isEmpty()) 0 else totalRecaudado / pedidos.size,
        pendientes = pedidos.size - entregados,
        entregados = entregados,
        // [Pepperoni, Pepperoni, Bebida] → [(Pepperoni, 2), (Bebida, 1)]
        unidadesPorProducto = vendidos
            .groupingBy { it.nombre }
            .eachCount()
            .toList()
            .sortedByDescending { it.second },
        unidadesTotales = vendidos.size
    )
}