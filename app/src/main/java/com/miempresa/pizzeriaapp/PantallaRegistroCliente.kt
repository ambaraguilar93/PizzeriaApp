package com.miempresa.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miempresa.pizzeriaapp.ui.theme.PizzeriaAppTheme

// Formulario de registro.
@Composable
fun PantallaRegistroCliente(
    mensaje: String?,
    alGuardar: (nombre: String, direccion: String, telefono: String, correo: String) -> Unit,
    modifier: Modifier = Modifier
) {
    // rememberSaveable: lo escrito no se pierde si se gira el teléfono.
    var nombre by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Completa los datos del nuevo cliente", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            supportingText = { Text("10 dígitos, ej: 9876543210") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),  // teclado numérico
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            supportingText = { Text("Ej: nombre@correo.cl") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),  // teclado con @
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        mensaje?.let { MensajeEstado(it) }

        Button(
            onClick = { alGuardar(nombre, direccion, telefono, correo) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Guardar cliente")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaRegistroClientePreview() {
    PizzeriaAppTheme {
        PantallaRegistroCliente(
            mensaje = "Error: El correo 'camila@' no es válido. Ejemplo: nombre@correo.cl",
            alGuardar = { _, _, _, _ -> }
        )
    }
}