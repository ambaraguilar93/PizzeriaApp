# PizzeriaApp — Semana 5: Integrando Kotlin en Android Studio

Aplicación Android en Kotlin para gestionar clientes y pedidos de una pizzería, organizada en módulos con Gradle y con una interfaz en Jetpack Compose.

## Estructura modular

| Módulo | Responsabilidad | Contenido |
|---|---|---|
| `:app` | Interfaz de usuario | `MainActivity`, `PantallaResumen` (Compose), `PizzeriaViewModel` |
| `:data` | Modelo y lógica de negocio | `Cliente`, `Producto`, `Pedido`, `Usuario`, `Catalogo`, `Operaciones`, `PizzeriaRepositorio` |
| `:utils` | Funciones reutilizables | `Validaciones`, `Formato`, `Resultado` (manejo de errores) |

Dependencias entre módulos: `:app → :data, :utils` y `:data → :utils`.

## Dependencias principales

- Jetpack Compose + Material 3
- ViewModel y LiveData (`lifecycle 2.11.0`)
- Kotlin Coroutines (`1.10.2`)

Todas las versiones se gestionan en `gradle/libs.versions.toml`.

## Funcionamiento

- Al iniciar, una corrutina carga los datos de ejemplo mientras se muestra un indicador de carga.
- La pantalla inicial muestra un resumen con el total de clientes, el total de pedidos y el último cliente registrado.
- Los botones permiten agregar clientes y pedidos; el resumen se actualiza automáticamente mediante LiveData.
- Los errores de validación (por ejemplo, un pedido sin productos) se muestran en pantalla sin cerrar la aplicación.

## Evidencias

### 1. Estructura modular
![Estructura modular](Evidencias/01.%20Estructura%20Modular.png)

### 2. Dependencias en Gradle
![Dependencias Gradle](Evidencias/02.%20Dependencias%20Gradle.png)

### 3. Pantalla de carga (corrutina)
![Pantalla de carga](Evidencias/03.%20Pantalla%20de%20carga.png)

### 4. Pantalla inicial con resumen
![Pantalla inicial](Evidencias/04.%20Pantalla%20inicial.png)

### 5. Actualización dinámica: nuevos clientes y pedidos
![Nuevos clientes y pedidos](Evidencias/05.%20Nuevos%20clientes%20y%20pedidos.png)

### 6. Manejo de errores
![Error controlado](Evidencias/06.%20Error%20controlado.png)