# PizzeriaApp — Semana 6: Avanzando hacia Android Studio

Aplicación Android en Kotlin para gestionar clientes y pedidos de una pizzería. Esta semana se suman 
animaciones Fade In/Out, un menú desplegable, indicadores de progreso con mensajes por pantalla y 
navegación entre pantallas. También se agregan formularios reales para registrar clientes y pedidos, 
el cambio de estado de los pedidos y estadísticas de ventas.

## Novedades de la semana 6

| Funcionalidad | Cómo se implementó | Dónde |
|---|---|---|
| Animaciones Fade In/Out | `AnimatedVisibility` con `fadeIn` (500 ms) y `fadeOut` (300 ms) para cada pantalla. Al abrir la app, Inicio aparece con Fade In después de la carga inicial. Al navegar, la pantalla actual se desvanece y la nueva aparece. | `MainActivity.kt` |
| Menú desplegable | `DropdownMenu` en la `TopAppBar` (botón ⋮) con íconos para Inicio, Clientes, Nuevo pedido, Pedidos y Productos. La pantalla actual aparece desactivada. | `MenuPrincipal.kt` |
| Notificaciones de progreso | `CircularProgressIndicator` controlado por el estado del ViewModel (LiveData), con un mensaje distinto por pantalla, por ejemplo, "Cargando clientes…", "Cargando catálogo…", "Generando resumen de pedidos…". | `MainActivity.kt`, `PizzeriaViewModel.kt` |
| Navegación | `enum class Pantalla` en el ViewModel. `navegarA()` muestra el indicador, carga los datos desde `:data` con una corrutina y cambia de pantalla. Flecha para volver en la barra y `BackHandler` para el botón atrás del sistema. | `PizzeriaViewModel.kt`, `MenuPrincipal.kt` |
| Registro de clientes | Formulario con validación de nombre, dirección, teléfono y correo (`:utils`), aviso de cliente repetido y correo único por cliente. | `PantallaRegistroCliente.kt`, `PizzeriaRepositorio.kt` |
| Nuevo pedido | Selección de cliente, cantidad por producto, notas y total calculado en vivo (descuento por más de 3 pizzas e IVA). Los datos se conservan al girar la pantalla. | `PantallaNuevoPedido.kt` |
| Estados del pedido | Botón para avanzar: En preparación → Listo → En camino → Entregado. | `PantallaPedidos.kt` |
| Estadísticas de ventas | Total recaudado, ticket promedio, pedidos pendientes y entregados, y unidades vendidas por producto con barras `LinearProgressIndicator`. | `Operaciones.kt` (`:data`), `PantallaPedidos.kt` |

## Estructura modular

| Módulo | Responsabilidad | Contenido |
|---|---|---|
| `:app` | Interfaz de usuario | `MainActivity`, `MenuPrincipal`, `PizzeriaViewModel` y las pantallas (`PantallaResumen`, `PantallaClientes`, `PantallaRegistroCliente`, `PantallaNuevoPedido`, `PantallaPedidos`, `PantallaProductos`) |
| `:data` | Modelo y lógica de negocio | `Cliente`, `Producto`, `Pedido`, `Usuario`, `Catalogo`, `Operaciones` (totales y estadísticas), `PizzeriaRepositorio` |
| `:utils` | Funciones reutilizables | `Validaciones` (incluye `formatearTelefono`), `Formato`, `Resultado` |

Dependencias entre módulos: `:app → :data, :utils` y `:data → :utils`.

## Dependencias principales

- Jetpack Compose + Material 3 + Material Icons (`material-icons-core`)
- ViewModel y LiveData (`lifecycle 2.11.0`)
- Kotlin Coroutines (`1.10.2`)

Todas las versiones se gestionan en `gradle/libs.versions.toml`.

## Pruebas realizadas

Dispositivos utilizados (emuladores de Android Studio):

| Dispositivo | Tipo | Resolución | Android |
|---|---|---|---|
| Medium Phone API 37.0 | Celular | Vertical | API 37 (Android 17) |
| Medium Tablet API 37.0 | Tablet | 2560 × 1600, horizontal y vertical | API 37 (Android 17) |

| # | Prueba | Dispositivo | Resultado esperado | Resultado | Ajuste realizado |
|---|---|---|---|---|---|
| 1 | Abrir la app | Celular | Indicador de carga y luego Fade In de Inicio | ✅ | — |
| 2 | Navegar entre pantallas con el menú | Celular y Tablet | Fade Out de la pantalla actual, indicador con su mensaje y Fade In de la nueva | ✅ | — |
| 3 | Abrir el menú ⋮ | Celular y Tablet | 5 opciones con íconos y la pantalla actual desactivada | ✅ | — |
| 4 | Mensajes de progreso por pantalla | Celular y Tablet | Mensaje propio para clientes, catálogo y pedidos | ✅ | — |
| 5 | Registrar un cliente con correo inválido (`ambar@`) | Celular | Mensaje de error y se mantiene el formulario | ✅ | — |
| 6 | Registrar un cliente con teléfono inválido (`123`) | Celular | Mensaje de error | ✅ | — |
| 7 | Registrar un cliente válido | Celular | Vuelve a Clientes con mensaje y el cliente en la lista | ✅ | — |
| 8 | Registrar un cliente con un correo ya usado por otro nombre | Celular | Rechazo | ❌ Se registraba duplicado | Validación de correo único en `PizzeriaRepositorio` |
| 9 | Crear un pedido sin cliente | Celular | Mensaje de error | ✅ | — |
| 10 | Pedido con más de 3 pizzas | Celular | Descuento del 10 % en el total | ✅ | — |
| 11 | Crear un pedido válido | Celular | Navega a Pedidos con mensaje y el pedido en la lista | ✅ | — |
| 12 | Avanzar un pedido hasta Entregado y volver a intentarlo | Celular | Avanza paso a paso y luego muestra un error | ✅ | — |
| 13 | Botón "Pasar a…" en tarjetas con texto corto | Celular | Botones alineados a la derecha | ❌ Quedaban desalineados | `fillMaxWidth()` en la columna de la tarjeta de pedido |
| 14 | Estadísticas después de crear y entregar pedidos | Celular y Tablet | Valores actualizados | ✅ | — |
| 15 | Catálogo de productos por categoría | Celular | Productos agrupados con precio e ingredientes | ✅ | — |
| 16 | Resumen de Inicio después de las pruebas | Celular | Totales y último cliente actualizados | ✅ | — |
| 17 | Formulario de Nuevo pedido en pantalla ancha | Tablet | Campos y tarjetas ocupan el ancho sin deformarse | ✅ | — |
| 18 | Girar la tablet con Nuevo pedido a medio llenar | Tablet | Se mantienen cliente, cantidades y notas | ❌ Se perdían el cliente y las cantidades | `remember` → `rememberSaveable`, guardando el correo del cliente y las cantidades por nombre |

## Evidencias

### 1. Animaciones (Fade In/Out)

| Carga inicial — Celular | Navegación — Celular | Navegación — Tablet |
|---|---|---|
| <img src="Evidencias/01.%20Animaciones/Carga%20inicial%20-%20Celular.gif" width="220"> | <img src="Evidencias/01.%20Animaciones/Navegacion%20con%20fade%20-%20Celular.gif" width="220"> | <img src="Evidencias/01.%20Animaciones/Navegacion%20con%20fade%20-%20Tablet.gif" width="300"> |

**Inicio — Tablet**

<img src="Evidencias/01.%20Animaciones/Inicio%20-%20Tablet.png" width="420">

### 2. Menú desplegable

| Desde Inicio — Celular | Desde Pedidos — Celular | Tablet |
|---|---|---|
| <img src="Evidencias/02.%20Menu%20Desplegable/Menu%20desplegable%20-%20Celular.png" width="220"> | <img src="Evidencias/02.%20Menu%20Desplegable/Menu%20desde%20otra%20pantalla%20-%20Celular.png" width="220"> | <img src="Evidencias/02.%20Menu%20Desplegable/Menu%20desplegable%20-%20Tablet.png" width="300"> |

### 3. Notificaciones de progreso

| Clientes | Catálogo | Pedidos | Tablet |
|---|---|---|---|
| <img src="Evidencias/03.%20Notificaciones%20de%20progreso/Progreso%20clientes%20-%20Celular.png" width="180"> | <img src="Evidencias/03.%20Notificaciones%20de%20progreso/Progreso%20catalogo%20-%20Celular.png" width="180"> | <img src="Evidencias/03.%20Notificaciones%20de%20progreso/Progreso%20pedidos%20-%20Celular.png" width="180"> | <img src="Evidencias/04.%20Funcionalidades/Tablet/Progreso%20-%20Tablet.png" width="240"> |

### 4. Funcionalidades integradas — Celular

**Clientes**

| Lista de clientes | Error de correo | Error de teléfono | Cliente registrado |
|---|---|---|---|
| <img src="Evidencias/04.%20Funcionalidades/Celular/Clientes%20-%20Celular.png" width="180"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Error%20registro%20correo%20-%20Celular.png" width="180"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Error%20registro%20telefono%20-%20Celular.png" width="180"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Cliente%20registrado%20-%20Celular.png" width="180"> |

**Pedidos**

| Error sin cliente | Pedido con descuento | Pedido creado | Avanzar estado (error al estar entregado) |
|---|---|---|---|
| <img src="Evidencias/04.%20Funcionalidades/Celular/Error%20pedido%20sin%20cliente%20-%20Celular.png" width="180"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Pedido%20con%20descuento%20-%20Celular.png" width="180"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Pedido%20creado%20-%20Celular.png" width="180"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Avanzar%20estado%20-%20Celular.png" width="180"> |

**Estadísticas, productos e Inicio**

| Estadísticas | Productos | Inicio actualizado |
|---|---|---|
| <img src="Evidencias/04.%20Funcionalidades/Celular/Estadisticas%20-%20Celular.png" width="200"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Productos%20-%20Celular.png" width="200"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Inicio%20actualizado%20-%20Celular.png" width="200"> |

### 5. Funcionalidades integradas — Tablet

| Nuevo pedido | Estadísticas |
|---|---|
| <img src="Evidencias/04.%20Funcionalidades/Tablet/Nuevo%20pedido%20-%20Tablet.png" width="320"> | <img src="Evidencias/04.%20Funcionalidades/Tablet/Estadisticas%20-%20Tablet.png" width="320"> |

### 6. Ajustes realizados a partir de las pruebas

**Correo duplicado (prueba 8).** Antes, un cliente con otro nombre podía registrarse con un correo ya usado. Ahora se rechaza con un mensaje.

| Antes | Después |
|---|---|
| <img src="Evidencias/04.%20Funcionalidades/Celular/Ajuste%20antes%20-%20Correo%20duplicado.png" width="220"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Ajuste%20despues%20-%20Correo%20duplicado.png" width="220"> |

**Botones desalineados (prueba 13).** En las tarjetas con textos cortos, el botón "Pasar a…" quedaba al centro. Ahora todos quedan alineados a la derecha.

| Antes | Después |
|---|---|
| <img src="Evidencias/04.%20Funcionalidades/Celular/Ajuste%20antes%20-%20Botones%20desalineados%20-%20Celular.png" width="220"> | <img src="Evidencias/04.%20Funcionalidades/Celular/Pedido%20creado%20-%20Celular.png" width="220"> |

**Rotación en Nuevo pedido (prueba 18).** Antes, al girar la tablet se perdían el cliente y las cantidades. Ahora se conservan.

| Antes: vertical con datos | Antes: al girar, datos perdidos |
|---|---|
| <img src="Evidencias/04.%20Funcionalidades/Tablet/Ajuste%20antes%20-%20Rotacion%20nuevo%20pedido%20P1%20-%20Tablet.png" width="260"> | <img src="Evidencias/04.%20Funcionalidades/Tablet/Ajuste%20antes%20-%20Rotacion%20nuevo%20pedido%20P2%20-%20Tablet.png" width="320"> |

| Después: horizontal con datos | Después: al girar, datos conservados |
|---|---|
| <img src="Evidencias/04.%20Funcionalidades/Tablet/Ajuste%20despues%20-%20Rotacion%20nuevo%20pedido%20P1%20-%20Tablet.png" width="320"> | <img src="Evidencias/04.%20Funcionalidades/Tablet/Ajuste%20despues%20-%20Rotacion%20nuevo%20pedido%20P2%20-%20Tablet.png" width="260"> |

## Versiones anteriores

- Semana 4: tag `semana-4`
- Semana 5: tag `semana-5`
 