package com.miempresa.pizzeriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme

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

// Conecta la lógica (ViewModel) con la UI (Compose).
@Composable
fun AppPizzeria(pizzeriaViewModel: PizzeriaViewModel = viewModel()) {
    // observeAsState: cada vez que cambia el LiveData, la pantalla se redibuja sola.
    val estado by pizzeriaViewModel.estado.observeAsState(EstadoPizzeria())

    PantallaResumen(
        estado = estado,
        alAgregarCliente = pizzeriaViewModel::agregarCliente,
        alAgregarPedido = pizzeriaViewModel::agregarPedido,
        alProbarError = pizzeriaViewModel::probarPedidoVacio
    )
}