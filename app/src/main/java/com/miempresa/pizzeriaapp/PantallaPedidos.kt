package com.miempresa.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miempresa.pizzeriaapp.modelo.Cliente
import com.miempresa.pizzeriaapp.modelo.EstadoPedido
import com.miempresa.pizzeriaapp.modelo.Pedido
import com.miempresa.pizzeriaapp.modelo.crearCatalogo
import com.miempresa.pizzeriaapp.negocio.EstadisticasVentas
import com.miempresa.pizzeriaapp.negocio.calcularEstadisticas
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme
import com.miempresa.pizzeriaapp.util.formatearPrecio

// Lista de pedidos con estadísticas y botón para avanzar su estado.
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
        // Se recalculan cada vez que cambia la lista (nuevo pedido o cambio de estado).
        if (pedidos.isNotEmpty()) {
            item { TarjetaEstadisticas(calcularEstadisticas(pedidos)) }
        }
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

// Tarjeta de un pedido. Si recibe alAvanzarEstado, muestra el botón para avanzar.
@Composable
fun TarjetaPedido(
    pedido: Pedido,
    estado: EstadoPedido = pedido.estado,
    alAvanzarEstado: (() -> Unit)? = null
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
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

// Tarjeta con el resumen de ventas.
@Composable
fun TarjetaEstadisticas(estadisticas: EstadisticasVentas) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Estadísticas de ventas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Tres indicadores principales, repartidos en partes iguales.
            Row(Modifier.fillMaxWidth()) {
                DatoEstadistica(formatearPrecio(estadisticas.totalRecaudado), "Recaudado", Modifier.weight(1f))
                DatoEstadistica("${estadisticas.cantidadPedidos}", "Pedidos", Modifier.weight(1f))
                DatoEstadistica(formatearPrecio(estadisticas.ticketPromedio), "Ticket promedio", Modifier.weight(1f))
            }

            Row(Modifier.fillMaxWidth()) {
                Text("Pendientes: ${estadisticas.pendientes}", modifier = Modifier.weight(1f))
                Text("Entregados: ${estadisticas.entregados}")
            }

            HorizontalDivider()

            Text(
                "Unidades vendidas por producto (${estadisticas.unidadesTotales})",
                style = MaterialTheme.typography.titleSmall
            )
            // La barra más larga es la del más vendido; las demás son proporcionales a ella.
            val maximo = estadisticas.unidadesPorProducto.firstOrNull()?.second ?: 1
            estadisticas.unidadesPorProducto.forEach { (producto, unidades) ->
                BarraProducto(producto, unidades, maximo)
            }

            estadisticas.masVendido?.let {
                Text("⭐ Más vendido: $it", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DatoEstadistica(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
    }
}

// Nombre del producto + barra (LinearProgressIndicator) + cantidad.
@Composable
fun BarraProducto(producto: String, unidades: Int, maximo: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            producto,
            modifier = Modifier.width(110.dp),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis   // si el nombre es muy largo, termina en "…"
        )
        LinearProgressIndicator(
            progress = { unidades.toFloat() / maximo },   // de 0.0 a 1.0
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
        )
        Text(
            "$unidades",
            modifier = Modifier.width(32.dp),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End
        )
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