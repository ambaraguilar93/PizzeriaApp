package com.miempresa.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme

// Pantalla inicial: resumen, accesos rápidos y últimos pedidos.
@Composable
fun PantallaResumen(
    estado: EstadoPizzeria,
    alIrA: (Pantalla) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Bienvenidos a la mejor pizzería de la ciudad",
            style = MaterialTheme.typography.headlineSmall
        )

        TarjetaResumen(estado)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { alIrA(Pantalla.REGISTRO_CLIENTE) }, modifier = Modifier.weight(1f)) {
                Text("Registrar cliente")
            }
            Button(onClick = { alIrA(Pantalla.NUEVO_PEDIDO) }, modifier = Modifier.weight(1f)) {
                Text("Nuevo pedido")
            }
        }
        OutlinedButton(onClick = { alIrA(Pantalla.PEDIDOS) }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver todos los pedidos")
        }

        estado.mensaje?.let { MensajeEstado(it) }

        Text("Últimos pedidos", style = MaterialTheme.typography.titleMedium)
        // Los 3 más recientes primero; el botón de avanzar estado está en la pantalla Pedidos.
        estado.pedidos.takeLast(3).reversed().forEach { pedido -> TarjetaPedido(pedido) }
    }
}

// Mensaje de resumen (total de clientes, total de pedidos y último cliente).
@Composable
fun TarjetaResumen(estado: EstadoPizzeria) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Resumen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Clientes registrados: ${estado.totalClientes}")
            Text("Pedidos realizados: ${estado.totalPedidos}")
            Text("Último cliente: ${estado.ultimoCliente ?: "Sin clientes"}")
        }
    }
}

// Mensaje de la última acción (en rojo si es un error).
@Composable
fun MensajeEstado(mensaje: String) {
    val esError = mensaje.startsWith("Error")
    Text(
        text = mensaje,
        color = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium
    )
}

@Preview(showBackground = true)
@Composable
fun PantallaResumenPreview() {
    PizzeriaAppTheme {
        PantallaResumen(
            estado = EstadoPizzeria(
                cargando = false,
                totalClientes = 2,
                totalPedidos = 2,
                ultimoCliente = "Diego Muñoz"
            ),
            alIrA = {}
        )
    }
}