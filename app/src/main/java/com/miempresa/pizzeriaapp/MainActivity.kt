package com.miempresa.pizzeriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme

// Duración de las animaciones (milisegundos).
private const val DURACION_FADE_IN = 500
private const val DURACION_FADE_OUT = 300

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PizzeriaAppTheme {
                AppPizzeria()
            }
        }
    }
}

// Contenedor común: barra con menú, indicador de progreso y pantallas con Fade In/Out.
@Composable
fun AppPizzeria(pizzeriaViewModel: PizzeriaViewModel = viewModel()) {
    // observeAsState: cada vez que cambia el LiveData, la pantalla se redibuja sola.
    val estado by pizzeriaViewModel.estado.observeAsState(EstadoPizzeria())

    // Botón "atrás" del teléfono: fuera de Inicio vuelve en vez de cerrar la app.
    BackHandler(enabled = estado.pantalla != Pantalla.INICIO) {
        pizzeriaViewModel.volver()
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                pantalla = estado.pantalla,
                alVolver = pizzeriaViewModel::volver,
                alNavegar = { destino -> pizzeriaViewModel.navegarA(destino) }
            )
        }
    ) { innerPadding ->
        // Box: las pantallas se superponen, así la que sale se desvanece mientras aparece el loader.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Una AnimatedVisibility por pantalla: solo es visible la actual, y solo cuando no está cargando.
            // Al iniciar la app, Inicio parte oculta (cargando = true), por eso aparece con Fade In.
            Pantalla.entries.forEach { pantalla ->
                AnimatedVisibility(
                    visible = !estado.cargando && estado.pantalla == pantalla,
                    enter = fadeIn(animationSpec = tween(DURACION_FADE_IN)),
                    exit = fadeOut(animationSpec = tween(DURACION_FADE_OUT))
                ) {
                    ContenidoPantalla(pantalla, estado, pizzeriaViewModel)
                }
            }

            // Indicador de progreso con el mensaje de la pantalla que se está cargando.
            AnimatedVisibility(
                visible = estado.cargando,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                IndicadorCarga(estado.mensajeCarga)
            }
        }
    }
}

// Decide qué pantalla dibujar y le entrega sus datos y acciones.
@Composable
fun ContenidoPantalla(pantalla: Pantalla, estado: EstadoPizzeria, pizzeriaViewModel: PizzeriaViewModel) {
    when (pantalla) {
        Pantalla.INICIO -> PantallaResumen(
            estado = estado,
            alIrA = { destino -> pizzeriaViewModel.navegarA(destino) }
        )
        Pantalla.CLIENTES -> PantallaClientes(
            clientes = estado.clientes,
            mensaje = estado.mensaje,
            alRegistrarCliente = { pizzeriaViewModel.navegarA(Pantalla.REGISTRO_CLIENTE) }
        )
        Pantalla.REGISTRO_CLIENTE -> PantallaRegistroCliente(
            mensaje = estado.mensaje,
            alGuardar = pizzeriaViewModel::registrarCliente
        )
        Pantalla.NUEVO_PEDIDO -> PantallaNuevoPedido(
            clientes = estado.clientes,
            catalogo = estado.catalogo,
            mensaje = estado.mensaje,
            alCrearPedido = pizzeriaViewModel::crearPedido
        )
        Pantalla.PEDIDOS -> PantallaPedidos(
            pedidos = estado.pedidos,
            mensaje = estado.mensaje,
            alAvanzarEstado = pizzeriaViewModel::avanzarEstado
        )
        Pantalla.PRODUCTOS -> PantallaProductos(catalogo = estado.catalogo)
    }
}

@Composable
fun IndicadorCarga(mensaje: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text(mensaje, style = MaterialTheme.typography.bodyLarge)
    }
}