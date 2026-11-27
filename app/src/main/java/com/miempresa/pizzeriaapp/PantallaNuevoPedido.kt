package com.miempresa.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miempresa.pizzeriaapp.modelo.Cliente
import com.miempresa.pizzeriaapp.modelo.Producto
import com.miempresa.pizzeriaapp.modelo.crearCatalogo
import com.miempresa.pizzeriaapp.negocio.PIZZAS_PARA_DESCUENTO
import com.miempresa.pizzeriaapp.negocio.TASA_IVA
import com.miempresa.pizzeriaapp.negocio.Totales
import com.miempresa.pizzeriaapp.negocio.calcularTotales
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme
import com.miempresa.pizzeriaapp.util.formatearPrecio

// Formulario de pedido para elegir cliente, cantidades por producto y notas. El total se calcula en vivo.
@Composable
fun PantallaNuevoPedido(
    clientes: List<Cliente>,
    catalogo: List<Producto>,
    mensaje: String?,
    alCrearPedido: (cliente: Cliente?, cantidades: Map<Producto, Int>, notas: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var clienteElegido by remember { mutableStateOf<Cliente?>(null) }
    var listaClientesAbierta by remember { mutableStateOf(false) }
    val cantidades = remember { mutableStateMapOf<Producto, Int>() }   // Producto → cantidad
    var notas by rememberSaveable { mutableStateOf("") }

    // Se recalcula sola cada vez que cambia una cantidad.
    val totales = calcularTotales(cantidades.flatMap { (producto, cantidad) -> List(cantidad) { producto } })

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Selección de cliente (DropdownMenu anclado al botón)
        Text("Cliente", style = MaterialTheme.typography.titleMedium)
        Box {
            OutlinedButton(onClick = { listaClientesAbierta = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(clienteElegido?.nombre ?: "Seleccionar cliente", modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = listaClientesAbierta,
                onDismissRequest = { listaClientesAbierta = false }
            ) {
                clientes.forEach { cliente ->
                    DropdownMenuItem(
                        text = { Text(cliente.nombre) },
                        onClick = {
                            clienteElegido = cliente
                            listaClientesAbierta = false
                        }
                    )
                }
            }
        }

        // Cantidades
        Text("Productos", style = MaterialTheme.typography.titleMedium)
        catalogo.forEach { producto ->
            FilaCantidad(
                producto = producto,
                cantidad = cantidades[producto] ?: 0,
                alCambiar = { nueva -> cantidades[producto] = nueva }
            )
        }

        OutlinedTextField(
            value = notas,
            onValueChange = { notas = it },
            label = { Text("Notas (opcional)") },
            placeholder = { Text("Ej: sin cebolla") },
            modifier = Modifier.fillMaxWidth()
        )

        TarjetaTotales(totales)

        mensaje?.let { MensajeEstado(it) }

        // El botón siempre está activo, si falta algo, el ViewModel responde con un mensaje de error.
        Button(
            onClick = { alCrearPedido(clienteElegido, cantidades.toMap(), notas) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Crear pedido")
        }
    }
}

// Un producto con botones − y + para elegir la cantidad.
@Composable
fun FilaCantidad(producto: Producto, cantidad: Int, alCambiar: (Int) -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text(formatearPrecio(producto.precio), style = MaterialTheme.typography.bodySmall)
            }
            FilledTonalIconButton(onClick = { alCambiar(cantidad - 1) }, enabled = cantidad > 0) {
                Text("−", style = MaterialTheme.typography.titleLarge)
            }
            Text("$cantidad", modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
            FilledTonalIconButton(onClick = { alCambiar(cantidad + 1) }, enabled = cantidad < 100) {
                Icon(Icons.Default.Add, contentDescription = "Agregar ${producto.nombre}")
            }
        }
    }
}

// Subtotal, descuento, IVA y total del pedido.
@Composable
fun TarjetaTotales(totales: Totales) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            FilaMonto("Subtotal", formatearPrecio(totales.subtotal))
            if (totales.descuento > 0) {
                FilaMonto("Descuento (más de $PIZZAS_PARA_DESCUENTO pizzas)", "−" + formatearPrecio(totales.descuento))
            }
            FilaMonto("IVA ($TASA_IVA%)", formatearPrecio(totales.impuestos))
            HorizontalDivider()
            FilaMonto("Total", formatearPrecio(totales.total), destacado = true)
        }
    }
}

@Composable
fun FilaMonto(etiqueta: String, valor: String, destacado: Boolean = false) {
    val peso = if (destacado) FontWeight.Bold else FontWeight.Normal
    Row(Modifier.fillMaxWidth()) {
        Text(etiqueta, modifier = Modifier.weight(1f), fontWeight = peso)
        Text(valor, fontWeight = peso)
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun PantallaNuevoPedidoPreview() {
    PizzeriaAppTheme {
        PantallaNuevoPedido(
            clientes = listOf(
                Cliente("Camila Rojas", "Av. Providencia 1234", "+56 (987) 654-3210", "camila@correo.cl")
            ),
            catalogo = crearCatalogo(),
            mensaje = null,
            alCrearPedido = { _, _, _ -> }
        )
    }
}