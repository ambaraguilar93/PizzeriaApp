package com.miempresa.pizzeriaapp.modelo

import com.miempresa.pizzeriaapp.negocio.Totales
import com.miempresa.pizzeriaapp.negocio.calcularTotales
import com.miempresa.pizzeriaapp.negocio.contarPizzas

enum class EstadoPedido(val etiqueta: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado");

    val siguiente: EstadoPedido? get() = entries.getOrNull(ordinal + 1)
}

class Pedido(
    val id: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    val notas: String? = null,
    estadoInicial: EstadoPedido = EstadoPedido.EN_PREPARACION
) {
    companion object {
        const val ID_COMBINADO = 0
    }

    init {
        require(productos.isNotEmpty()) { "El pedido N° $id no tiene productos." }
    }

    var estado: EstadoPedido = estadoInicial
        private set

    val totales: Totales = calcularTotales(productos)

    val cantidadPizzas: Int get() = contarPizzas(productos)

    fun calcularTotal(): Int = totales.total

    // El pedido avanza paso a paso de preparación → listo → en camino → entregado.
    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        check(nuevoEstado == estado.siguiente) {
            val permitido = estado.siguiente?.let { "El único cambio permitido es a '${it.etiqueta}'." }
                ?: "El pedido ya está en su estado final."
            "No se puede pasar de '${estado.etiqueta}' a '${nuevoEstado.etiqueta}'. $permitido"
        }
        estado = nuevoEstado
    }

    // Sobrecarga del operador.
    // Solo se pueden combinar pedidos del mismo cliente.
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "Solo se pueden combinar pedidos del mismo cliente (${cliente.nombre} y ${otro.cliente.nombre})."
        }
        val notasUnidas = listOfNotNull(notas, otro.notas).distinct().joinToString(" | ").ifEmpty { null }
        return Pedido(ID_COMBINADO, cliente, productos + otro.productos, notasUnidas)
    }

    // Desestructuración.
    // El total se calcula aparte, por eso las funciones component se declaran a mano.
    operator fun component1(): Cliente = cliente
    operator fun component2(): List<Producto> = productos
    operator fun component3(): Int = totales.total

    // Pizzas repetidas
    fun resumenProductos(): String =
        productos.groupingBy { it }.eachCount().entries
            .joinToString(", ") { (producto, cantidad) -> "${cantidad}x ${producto.nombre}" }
}