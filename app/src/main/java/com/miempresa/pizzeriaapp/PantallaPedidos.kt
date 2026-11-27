package com.miempresa.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miempresa.pizzeriaapp.modelo.Cliente
import com.miempresa.pizzeriaapp.modelo.EstadoPedido
import com.miempresa.pizzeriaapp.modelo.Pedido
import com.miempresa.pizzeriaapp.modelo.crearCatalogo
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme
import com.miempresa.pizzeriaapp.util.formatearPrecio

// Lista de pedidos con botón para avanzar su estado.
@Composable
fun PantallaPedidos(
    pedidos: List<Pedido>,
    mensaje: String?,
    alAvanzarEstado: (Pedido) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Aquí irán las estadísticas, si queda tiempo.
        mensaje?.let { item { MensajeEstado(it) } }

        if (pedidos.isEmpty()) {
            item { Text("Aún no hay pedidos registrados.") }
        }
        items(pedidos, key = { it.id }) { pedido ->
            TarjetaPedido(
                pedido = pedido,
                estado = pedido.estado,
                alAvanzarEstado = { alAvanzarEstado(pedido) }
            )
        }
    }
}

// Tarjeta de un pedido.
@Composable
fun TarjetaPedido(
    pedido: Pedido,
    estado: EstadoPedido = pedido.estado,
    alAvanzarEstado: (() -> Unit)? = null
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Pedido N° ${pedido.id} — ${pedido.cliente.nombre}", fontWeight = FontWeight.Bold)
            Text(pedido.resumenProductos(), style = MaterialTheme.typography.bodySmall)
            pedido.notas?.let { Text("Notas: $it", style = MaterialTheme.typography.bodySmall) }
            Text("Estado: ${estado.etiqueta}")
            Text("Total: ${formatearPrecio(pedido.calcularTotal())}")

            if (alAvanzarEstado != null) {
                Spacer(Modifier.height(6.dp))
                // Si ya está entregado, el botón sigue activo para probar el mensaje de error.
                val textoBoton = estado.siguiente?.let { "Pasar a ${it.etiqueta}" } ?: "Avanzar estado"
                FilledTonalButton(onClick = alAvanzarEstado, modifier = Modifier.align(Alignment.End)) {
                    Text(textoBoton)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaPedidosPreview() {
    val (pepperoni, margarita, _, bebida) = crearCatalogo()
    val cliente = Cliente("Camila Rojas", "Av. Providencia 1234", "+56 (987) 654-3210", "camila@correo.cl")
    PizzeriaAppTheme {
        PantallaPedidos(
            pedidos = listOf(
                Pedido(1, cliente, listOf(pepperoni, pepperoni, bebida), notas = "Sin cebolla"),
                Pedido(2, cliente, listOf(margarita), estadoInicial = EstadoPedido.ENTREGADO)
            ),
            mensaje = null,
            alAvanzarEstado = {}
        )
    }
}