package com.miempresa.pizzeriaapp.util

const val TELEFONO_POR_DEFECTO = "+56 (000) 000-0000"
const val CORREO_POR_DEFECTO = "correo@invalido.com"

// Formatos aceptados.
// letras (con tildes) y espacios
private val REGEX_NOMBRE = Regex("""^[\p{L} ]{2,}$""")
// usuario@dominio.cl
private val REGEX_CORREO = Regex("""^[\w.+-]+@[\w-]+(\.[\w-]+)+$""")
// +56 (987) 654-3210
private val REGEX_TELEFONO = Regex("""^\+56 \(\d{3}\) \d{3}-\d{4}$""")

fun esNombreValido(nombre: String): Boolean = REGEX_NOMBRE.matches(nombre.trim())

fun esCorreoValido(correo: String): Boolean = REGEX_CORREO.matches(correo.trim())

fun esTelefonoValido(telefono: String): Boolean = REGEX_TELEFONO.matches(telefono.trim())

// Si el dato no es válido, se reemplaza por el valor por defecto.
fun normalizarCorreo(correo: String): String =
    if (esCorreoValido(correo)) correo.trim().lowercase() else CORREO_POR_DEFECTO

fun normalizarTelefono(telefono: String): String =
    if (esTelefonoValido(telefono)) telefono.trim() else TELEFONO_POR_DEFECTO

// Devuelve el teléfono con formato +56 (XXX) XXX-XXXX, o null si no es válido.
fun formatearTelefono(entrada: String): String? {
    val digitos = entrada.filter { it.isDigit() }
    val completo = when {
        digitos.length == 10 -> "56$digitos"
        digitos.length == 12 && digitos.startsWith("56") -> digitos
        else -> return null
    }
    val numero = completo.drop(2)
    return "+56 (${numero.take(3)}) ${numero.substring(3, 6)}-${numero.takeLast(4)}"
}