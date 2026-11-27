package com.miempresa.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miempresa.pizzeriaapp.modelo.Pedido
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme
import com.miempresa.pizzeriaapp.util.formatearPrecio

// Pantalla inicial que recibe el estado y las acciones
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaResumen(
    estado: EstadoPizzeria,
    alAgregarCliente: () -> Unit,
    alAgregarPedido: () -> Unit,
    alProbarError: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PizzeriaApp") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (estado.cargando) {
            // Se muestra mientras la corrutina del ViewModel carga los datos.
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Cargando datos…")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Bienvenidos a la mejor pizzería de la ciudad",
                    style = MaterialTheme.typography.headlineSmall
                )

                TarjetaResumen(estado)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = alAgregarCliente, modifier = Modifier.weight(1f)) {
                        Text("Agregar cliente")
                    }
                    Button(onClick = alAgregarPedido, modifier = Modifier.weight(1f)) {
                        Text("Nuevo pedido")
                    }
                }
                OutlinedButton(onClick = alProbarError, modifier = Modifier.fillMaxWidth()) {
                    Text("Probar pedido vacío")
                }

                estado.mensaje?.let { MensajeEstado(it) }

                Text("Pedidos registrados", style = MaterialTheme.typography.titleMedium)
                estado.pedidos.forEach { pedido -> TarjetaPedido(pedido) }
            }
        }
    }
}

// Mensaje de resumen solicitado (total de clientes, total de pedidos y último cliente).
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

// Mensaje de la última acción
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
            alAgregarCliente = {},
            alAgregarPedido = {},
            alProbarError = {}
        )
    }
}