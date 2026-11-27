package com.miempresa.pizzeriaapp

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector

// Opciones del menú, las pantallas que lleva, cómo se llaman y su ícono.
data class OpcionMenu(val pantalla: Pantalla, val etiqueta: String, val icono: ImageVector)

private val OPCIONES_MENU = listOf(
    OpcionMenu(Pantalla.INICIO, "Inicio", Icons.Default.Home),
    OpcionMenu(Pantalla.CLIENTES, "Clientes", Icons.Default.Person),
    OpcionMenu(Pantalla.NUEVO_PEDIDO, "Nuevo pedido", Icons.Default.AddCircle),
    OpcionMenu(Pantalla.PEDIDOS, "Pedidos", Icons.AutoMirrored.Filled.List),
    OpcionMenu(Pantalla.PRODUCTOS, "Productos", Icons.Default.ShoppingCart)
)

// Barra superior común a todas las pantallas con título, flecha para volver y menú.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    pantalla: Pantalla,
    alVolver: () -> Unit,
    alNavegar: (Pantalla) -> Unit
) {
    TopAppBar(
        title = { Text(pantalla.titulo) },
        navigationIcon = {
            // En Inicio no hay a dónde volver, así que la flecha no se muestra.
            if (pantalla != Pantalla.INICIO) {
                IconButton(onClick = alVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = { MenuPrincipal(pantallaActual = pantalla, alElegir = alNavegar) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

// Botón que abre el DropdownMenu con las funcionalidades principales.
@Composable
fun MenuPrincipal(pantallaActual: Pantalla, alElegir: (Pantalla) -> Unit) {
    var abierto by remember { mutableStateOf(false) }

    // el menú se despliega justo debajo del botón.
    Box {
        IconButton(onClick = { abierto = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Abrir menú")
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            OPCIONES_MENU.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion.etiqueta) },
                    leadingIcon = { Icon(opcion.icono, contentDescription = null) },
                    onClick = {
                        abierto = false
                        alElegir(opcion.pantalla)
                    },
                    // La pantalla en la que estás aparece desactivada (en gris).
                    enabled = opcion.pantalla != pantallaActual
                )
            }
        }
    }
}