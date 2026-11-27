package com.miempresa.pizzeriaapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miempresa.pizzeriaapp.modelo.Cliente
import com.miempresa.pizzeriaapp.modelo.Pedido
import com.miempresa.pizzeriaapp.modelo.Producto
import com.miempresa.pizzeriaapp.negocio.PizzeriaRepositorio
import com.miempresa.pizzeriaapp.util.Resultado
import com.miempresa.pizzeriaapp.util.ejecutarSeguro
import com.miempresa.pizzeriaapp.util.esCorreoValido
import com.miempresa.pizzeriaapp.util.formatearTelefono
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Pantallas de la app. Cada una tiene su título y el mensaje que se muestra mientras carga.
enum class Pantalla(val titulo: String, val mensajeCarga: String) {
    INICIO("PizzeriaApp", "Cargando resumen…"),
    CLIENTES("Clientes", "Cargando clientes…"),
    REGISTRO_CLIENTE("Registrar cliente", "Preparando formulario…"),
    NUEVO_PEDIDO("Nuevo pedido", "Cargando catálogo…"),
    PEDIDOS("Pedidos", "Generando resumen de pedidos…"),
    PRODUCTOS("Productos", "Cargando productos…")
}

// Interfaz.
data class EstadoPizzeria(
    val pantalla: Pantalla = Pantalla.INICIO,
    val cargando: Boolean = true,
    val mensajeCarga: String = "Cargando datos de la pizzería…",
    val totalClientes: Int = 0,
    val totalPedidos: Int = 0,
    val ultimoCliente: String? = null,
    val clientes: List<Cliente> = emptyList(),
    val pedidos: List<Pedido> = emptyList(),
    val catalogo: List<Producto> = emptyList(),
    val mensaje: String? = null
)

class PizzeriaViewModel : ViewModel() {

    private val repositorio = PizzeriaRepositorio()

    private val _estado = MutableLiveData(EstadoPizzeria())
    val estado: LiveData<EstadoPizzeria> = _estado

    // Estado actual (nunca nulo), para crear el siguiente con copy().
    private val estadoActual: EstadoPizzeria
        get() = _estado.value ?: EstadoPizzeria()

    init {
        // Carga inicial asíncrona con corrutinas (simula leer desde una base de datos o API).
        viewModelScope.launch {
            delay(800)
            repositorio.cargarDatosDeEjemplo()
            actualizarEstado()
        }
    }

    // ---------- Navegación ----------

    // Muestra el indicador de progreso con el mensaje del destino, carga los datos y cambia de pantalla.
    fun navegarA(destino: Pantalla, mensaje: String? = null) {
        // Si ya hay una carga en curso o ya estamos en esa pantalla, no se hace nada.
        if (estadoActual.cargando || destino == estadoActual.pantalla) return

        viewModelScope.launch {
            _estado.value = estadoActual.copy(cargando = true, mensajeCarga = destino.mensajeCarga, mensaje = null)
            delay(TIEMPO_CARGA)
            actualizarEstado(mensaje, pantalla = destino)
        }
    }

    // (Botón atrás) desde el formulario de registro vuelve a Clientes / desde las demás, a Inicio.
    fun volver() {
        val destino = if (estadoActual.pantalla == Pantalla.REGISTRO_CLIENTE) Pantalla.CLIENTES else Pantalla.INICIO
        navegarA(destino)
    }

    // ---------- Acciones reales ----------

    // Registra un cliente con los datos del formulario. Si está bien, vuelve a la lista de clientes.
    fun registrarCliente(nombre: String, direccion: String, telefono: String, correo: String) {
        val resultado = ejecutarSeguro {
            require(esCorreoValido(correo)) { "El correo '$correo' no es válido. Ejemplo: nombre@correo.cl" }
            val telefonoFormateado = requireNotNull(formatearTelefono(telefono)) {
                "El teléfono debe tener 10 dígitos (ej: 9876543210), o 12 si incluye el 56."
            }
            val clientesAntes = repositorio.totalClientes()
            val cliente = repositorio.registrarCliente(nombre, direccion, telefonoFormateado, correo)
            // El repositorio no duplica clientes (mismo nombre y correo): si el total no cambió, ya existía.
            check(repositorio.totalClientes() > clientesAntes) {
                "${cliente.nombre} ya estaba registrado con ese correo."
            }
            cliente
        }
        when (resultado) {
            is Resultado.Exito -> navegarA(Pantalla.CLIENTES, "Cliente ${resultado.valor.nombre} registrado.")
            is Resultado.Error -> actualizarEstado("Error: ${resultado.mensaje}")
        }
    }

    // Crea un pedido con las cantidades elegidas en el formulario. Si está bien, va a la lista de pedidos.
    fun crearPedido(cliente: Cliente?, cantidades: Map<Producto, Int>, notas: String) {
        val resultado = ejecutarSeguro {
            val elegido = requireNotNull(cliente) { "Selecciona un cliente para el pedido." }
            val conCantidad = cantidades.filterValues { it > 0 }
            require(conCantidad.isNotEmpty()) { "Agrega al menos un producto al pedido." }
            require(conCantidad.values.all { it in CANTIDAD_PERMITIDA }) {
                "Cada producto debe tener entre 1 y 100 unidades."
            }
            // {Pepperoni=2, Bebida=1}  →  [Pepperoni, Pepperoni, Bebida]
            val productos = conCantidad.flatMap { (producto, cantidad) -> List(cantidad) { producto } }
            repositorio.crearPedido(elegido, productos, notas.ifBlank { null })
        }
        when (resultado) {
            is Resultado.Exito -> navegarA(
                Pantalla.PEDIDOS,
                "Pedido N° ${resultado.valor.id} creado para ${resultado.valor.cliente.nombre}."
            )
            is Resultado.Error -> actualizarEstado("Error: ${resultado.mensaje}")
        }
    }

    // Avanza el pedido al siguiente estado En preparación → Listo → En camino → Entregado.
    fun avanzarEstado(pedido: Pedido) {
        val resultado = ejecutarSeguro {
            val siguiente = checkNotNull(pedido.estado.siguiente) { "El pedido N° ${pedido.id} ya fue entregado." }
            pedido.cambiarEstado(siguiente)
            pedido
        }
        procesar(resultado) { "Pedido N° ${it.id} pasó a '${it.estado.etiqueta}'." }
    }

    // ---------- Apoyo ----------

    // Convierte el Resultado en un mensaje para la pantalla.
    private fun <T> procesar(resultado: Resultado<T>, mensajeExito: (T) -> String) {
        val mensaje = when (resultado) {
            is Resultado.Exito -> mensajeExito(resultado.valor)
            is Resultado.Error -> "Error: ${resultado.mensaje}"
        }
        actualizarEstado(mensaje)
    }

    // Lee los datos del repositorio y los publica; Compose se redibuja automáticamente.
    // copy() mantiene lo que no cambia (por ejemplo, la pantalla actual).
    private fun actualizarEstado(mensaje: String? = null, pantalla: Pantalla = estadoActual.pantalla) {
        _estado.value = estadoActual.copy(
            pantalla = pantalla,
            cargando = false,
            totalClientes = repositorio.totalClientes(),
            totalPedidos = repositorio.totalPedidos(),
            ultimoCliente = repositorio.ultimoCliente()?.nombre,
            clientes = repositorio.obtenerClientes(),
            pedidos = repositorio.obtenerPedidos(),
            catalogo = repositorio.catalogo,
            mensaje = mensaje
        )
    }

    companion object {
        private const val TIEMPO_CARGA = 700L          // milisegundos que se ve el indicador al navegar
        private val CANTIDAD_PERMITIDA = 1..100         // misma regla que en la versión de consola
    }
}