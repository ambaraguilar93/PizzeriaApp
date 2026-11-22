package com.miempresa.pizzeriaapp.util

// Representa el resultado de una operación que puede fallar,
// para que la UI muestre un mensaje en vez de que la app se cierre.
sealed class Resultado<out T> {
    data class Exito<T>(val valor: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
}

// Ejecuta una acción y convierte los errores de require/check en un Resultado.Error.
inline fun <T> ejecutarSeguro(accion: () -> T): Resultado<T> =
    try {
        Resultado.Exito(accion())
    } catch (e: IllegalArgumentException) {   // lanzado por require(...)
        Resultado.Error(e.message ?: "Dato inválido.")
    } catch (e: IllegalStateException) {      // lanzado por check(...)
        Resultado.Error(e.message ?: "Operación no permitida.")
    }