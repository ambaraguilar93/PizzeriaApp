package com.miempresa.pizzeriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miempresa.pizzeriaapp.modelo.Cliente
import com.miempresa.pizzeriaapp.modelo.Pedido
import com.miempresa.pizzeriaapp.modelo.Producto
import com.miempresa.pizzeriaapp.modelo.crearCatalogo
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme
import com.miempresa.pizzeriaapp.util.formatearPrecio

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PizzeriaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaPizzeria(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Pedido de ejemplo armado con las clases migradas desde el proyecto de consola.
fun crearPedidoEjemplo(catalogo: List<Producto>): Pedido {
    val (pepperoni, margarita, hawaiana, bebida) = catalogo
    val cliente = Cliente("Camila Rojas", "Av. Providencia 1234", "+56 (987) 654-3210", "camila@correo.cl")
    return Pedido(1, cliente, listOf(pepperoni, pepperoni, margarita, hawaiana, bebida), notas = "Sin cebolla")
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Text(
        text = "Bienvenidos a la mejor pizzería de la ciudad",
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier
    )
}

@Composable
fun PantallaPizzeria(modifier: Modifier = Modifier) {
    val catalogo = remember { crearCatalogo() }
    val pedido = remember { crearPedidoEjemplo(catalogo) }
    val totales = pedido.totales

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Greeting()

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Nuestro menú", style = MaterialTheme.typography.titleMedium)
        catalogo.forEach { producto ->
            Text(text = "• ${producto.detallarProducto()}")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Pedido N° ${pedido.id}", style = MaterialTheme.typography.titleMedium)
        Text(text = pedido.cliente.descripcion())
        Text(text = "Productos: ${pedido.resumenProductos()}")
        Text(text = "Estado: ${pedido.estado.etiqueta}")
        Text(text = "Subtotal: ${formatearPrecio(totales.subtotal)}")
        Text(text = "Descuento: ${formatearPrecio(totales.descuento)}")
        Text(text = "IVA: ${formatearPrecio(totales.impuestos)}")
        Text(text = "Total: ${formatearPrecio(totales.total)}")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PizzeriaAppTheme {
        PantallaPizzeria()
    }
}