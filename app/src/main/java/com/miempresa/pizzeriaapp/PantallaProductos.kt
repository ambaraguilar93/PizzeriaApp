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
import com.miempresa.pizzeriaapp.modelo.Producto
import com.miempresa.pizzeriaapp.modelo.crearCatalogo
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme
import com.miempresa.pizzeriaapp.util.formatearPrecio

// Catálogo agrupado por categoría (Pizzas, Bebidas, Ingredientes, Combos).
@Composable
fun PantallaProductos(catalogo: List<Producto>, modifier: Modifier = Modifier) {
    val porCategoria = catalogo.groupBy { it.categoria }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        porCategoria.forEach { (categoria, productos) ->
            item {
                // "pizza" → "Pizzas"
                Text(
                    categoria.etiqueta.replaceFirstChar { it.uppercase() } + "s",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(productos) { producto -> TarjetaProducto(producto) }
        }
    }
}

@Composable
fun TarjetaProducto(producto: Producto) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(producto.nombre, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(
                    formatearPrecio(producto.precio),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            if (producto.ingredientes.isNotEmpty()) {
                Text(
                    "Ingredientes: ${producto.ingredientes.keys.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaProductosPreview() {
    PizzeriaAppTheme {
        PantallaProductos(catalogo = crearCatalogo())
    }
}