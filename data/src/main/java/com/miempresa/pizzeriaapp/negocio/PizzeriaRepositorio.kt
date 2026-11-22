package com.miempresa.pizzeriaapp.negocio

import com.miempresa.pizzeriaapp.modelo.Cliente
import com.miempresa.pizzeriaapp.modelo.Pedido
import com.miempresa.pizzeriaapp.modelo.Producto
import com.miempresa.pizzeriaapp.modelo.crearCatalogo
import com.miempresa.pizzeriaapp.util.esNombreValido
import com.miempresa.pizzeriaapp.util.normalizarCorreo
import com.miempresa.pizzeriaapp.util.normalizarTelefono

// Guarda en memoria los clientes y pedidos de la pizzería.
class PizzeriaRepositorio {

    val catalogo: List<Producto> = crearCatalogo()

    private val clientes = mutableListOf<Cliente>()
    private val pedidos = mutableListOf<Pedido>()

    // Consultas (se entregan copias para que nadie modifique las listas desde fuera).
    fun obtenerClientes(): List<Cliente> = clientes.toList()
    fun obtenerPedidos(): List<Pedido> = pedidos.toList()

    // Datos para el resumen de la pantalla inicial.
    fun totalClientes(): Int = clientes.size
    fun totalPedidos(): Int = pedidos.size
    fun ultimoCliente(): Cliente? = clientes.lastOrNull()

    // Registra un cliente usando las validaciones de :utils.
    // Si ya existe (mismo nombre y correo), devuelve el que estaba registrado.
    fun registrarCliente(nombre: String, direccion: String, telefono: String, correo: String): Cliente {
        require(esNombreValido(nombre)) { "El nombre '$nombre' no es válido." }
        require(direccion.isNotBlank()) { "La dirección no puede estar vacía." }

        val nuevo = Cliente(
            nombre = nombre.trim(),
            direccion = direccion.trim(),
            telefono = normalizarTelefono(telefono),
            correo = normalizarCorreo(correo)
        )
        return clientes.find { it == nuevo } ?: nuevo.also { clientes.add(it) }
    }

    // Crea un pedido para un cliente registrado. El id es correlativo.
    fun crearPedido(cliente: Cliente, productos: List<Producto>, notas: String? = null): Pedido {
        require(cliente in clientes) { "El cliente ${cliente.nombre} no está registrado." }

        val pedido = Pedido(id = pedidos.size + 1, cliente = cliente, productos = productos, notas = notas)
        pedidos.add(pedido)
        return pedido
    }

    // Datos iniciales para que la app no parta vacía.
    fun cargarDatosDeEjemplo() {
        if (clientes.isNotEmpty()) return

        val (pepperoni, margarita, hawaiana, bebida) = catalogo
        val camila = registrarCliente("Camila Rojas", "Av. Providencia 1234", "+56 (987) 654-3210", "camila@correo.cl")
        val diego = registrarCliente("Diego Muñoz", "Los Leones 456", "+56 (912) 345-6789", "diego@correo.cl")

        crearPedido(camila, listOf(pepperoni, pepperoni, margarita, hawaiana, bebida), notas = "Sin cebolla")
        crearPedido(diego, listOf(hawaiana, bebida))
    }
}