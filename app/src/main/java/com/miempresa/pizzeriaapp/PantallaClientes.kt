package com.miempresa.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miempresa.pizzeriaapp.modelo.Cliente
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme

// Lista de clientes registrados con un botón para ir al formulario de registro.
@Composable
fun PantallaClientes(
    clientes: List<Cliente>,
    mensaje: String?,
    alRegistrarCliente: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(onClick = alRegistrarCliente, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Registrar cliente")
            }
        }
        mensaje?.let { item { MensajeEstado(it) } }
        item {
            Text("${clientes.size} clientes registrados", style = MaterialTheme.typography.titleMedium)
        }
        items(clientes) { cliente -> TarjetaCliente(cliente) }
    }
}

@Composable
fun TarjetaCliente(cliente: Cliente) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(cliente.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            DatoConIcono(Icons.Default.Email, cliente.correo)
            DatoConIcono(Icons.Default.Phone, cliente.telefono)
            DatoConIcono(Icons.Default.LocationOn, cliente.direccion)
        }
    }
}

// ícono pequeño + texto.
@Composable
fun DatoConIcono(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icono,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaClientesPreview() {
    PizzeriaAppTheme {
        PantallaClientes(
            clientes = listOf(
                Cliente("Camila Rojas", "Av. Providencia 1234", "+56 (987) 654-3210", "camila@correo.cl"),
                Cliente("Diego Muñoz", "Los Leones 456", "+56 (912) 345-6789", "diego@correo.cl")
            ),
            mensaje = "Cliente Diego Muñoz registrado.",
            alRegistrarCliente = {}
        )
    }
}