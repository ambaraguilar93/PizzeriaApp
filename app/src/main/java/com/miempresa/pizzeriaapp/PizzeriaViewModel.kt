package com.miempresa.pizzeriaapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miempresa.pizzeriaapp.modelo.Pedido
import com.miempresa.pizzeriaapp.negocio.PizzeriaRepositorio
import com.miempresa.pizzeriaapp.util.Resultado
import com.miempresa.pizzeriaapp.util.ejecutarSeguro
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Todo lo que la pantalla necesita mostrar.
data class EstadoPizzeria(
    val cargando: Boolean = true,
    val totalClientes: Int = 0,
    val totalPedidos: Int = 0,
    val ultimoCliente: String? = null,
    val pedidos: List<Pedido> = emptyList(),
    val mensaje: String? = null
)

class PizzeriaViewModel : ViewModel() {

    private val repositorio = PizzeriaRepositorio()

    private val _estado = MutableLiveData(EstadoPizzeria())
    val estado: LiveData<EstadoPizzeria> = _estado

    init {
        // Carga inicial asíncrona con corrutinas (simula leer desde una base de datos o API).
        viewModelScope.launch {
            delay(800)
            repositorio.cargarDatosDeEjemplo()
            actualizarEstado()
        }
    }

    // Registra un cliente nuevo (cada uno con un correo distinto).
    fun agregarCliente() {
        val numero = repositorio.totalClientes() + 1
        val nombre = NOMBRES_DE_PRUEBA[numero % NOMBRES_DE_PRUEBA.size]
        val resultado = ejecutarSeguro {
            repositorio.registrarCliente(
                nombre = nombre,
                direccion = "Calle Ejemplo $numero",
                telefono = "+56 (911) 222-3333",
                correo = "cliente$numero@correo.cl"
            )
        }
        procesar(resultado) { cliente -> "Cliente ${cliente.nombre} registrado." }
    }

    // Crea un pedido para el último cliente registrado con 2 productos al azar.
    fun agregarPedido() {
        val resultado = ejecutarSeguro {
            val cliente = checkNotNull(repositorio.ultimoCliente()) { "No hay clientes registrados." }
            repositorio.crearPedido(cliente, repositorio.catalogo.shuffled().take(2))
        }
        procesar(resultado) { pedido -> "Pedido N° ${pedido.id} creado para ${pedido.cliente.nombre}." }
    }

    // Demuestra el manejo de errores: un pedido sin productos no es válido.
    fun probarPedidoVacio() {
        val resultado = ejecutarSeguro {
            val cliente = checkNotNull(repositorio.ultimoCliente()) { "No hay clientes registrados." }
            repositorio.crearPedido(cliente, emptyList())
        }
        procesar(resultado) { pedido -> "Pedido N° ${pedido.id} creado." }
    }

    // Convierte el Resultado en un mensaje para la pantalla.
    private fun <T> procesar(resultado: Resultado<T>, mensajeExito: (T) -> String) {
        val mensaje = when (resultado) {
            is Resultado.Exito -> mensajeExito(resultado.valor)
            is Resultado.Error -> "Error: ${resultado.mensaje}"
        }
        actualizarEstado(mensaje)
    }

    // Lee los datos del repositorio y los publica; Compose se redibuja automáticamente.
    private fun actualizarEstado(mensaje: String? = null) {
        _estado.value = EstadoPizzeria(
            cargando = false,
            totalClientes = repositorio.totalClientes(),
            totalPedidos = repositorio.totalPedidos(),
            ultimoCliente = repositorio.ultimoCliente()?.nombre,
            pedidos = repositorio.obtenerPedidos(),
            mensaje = mensaje
        )
    }

    companion object {
        private val NOMBRES_DE_PRUEBA = listOf(
            "Valentina Soto", "Matías Pérez", "Fernanda Díaz", "Joaquín Torres", "Isidora Vega"
        )
    }
}