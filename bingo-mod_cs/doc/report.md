# Reporte del proyecto — BingoMod

Estado vigente del trabajo, organizado por fases. Las tareas completadas se describen una sola vez; lo pendiente se indica donde corresponde.

## Fase 1 — Modelo base

**Estado: completada (Tasks 1.1–1.5).**

- `BingoPool`: catálogo base de 85 ítems de supervivencia; también acepta listas personalizadas. Los ítems del catálogo base no se repiten.
- `BingoSlot`: contiene un `Item` fijo y estado `completed`, inicialmente `false`.
- `GameMode`: modalidades `LINE`, `DIAGONAL`, `MATRIX` y `RANDOM`. La evaluación de victoria corresponde a Fase 5.
- `BingoBoard`: matriz de 5×5; selecciona 25 ítems únicos incluso si un pool personalizado contiene duplicados. La matriz devuelta no permite reemplazar las casillas internas.
- `PlayerRoundStats` y `RankingManager`: validan ronda y ticks no negativos; registran tiempos de rondas completadas y ordenan el ranking por menor tiempo acumulado. Las rondas incompletas no suman y las listas expuestas no permiten alterar el ranking.

**Criterio del catálogo:** ítems obtenibles en supervivencia vanilla; se permiten retos difíciles pero posibles en una partida. Se excluyen ítems imposibles o no factibles, como netherite, huevos de generador, huevo de dragón y estrella del Nether.

**Verificación:** pruebas unitarias cubren selección sin duplicados, validación de datos, copia de la matriz y agregación/encapsulación del ranking.

## Fase 2 — HUD

**Estado: completada (Tasks 2.1–2.5).**

- `BingoHudOverlay` está registrado en `HudRenderCallback.EVENT`.
- El tablero usa celdas de 18×18 px, con fondo, ícono de ítem y superposición verde para casillas completadas.
- El temporizador se dibuja en formato `MM:SS` usando los ticks de `ClientBingoState`.
- La visualización requiere que el servidor envíe el tablero y el tiempo; su activación desde el flujo de partida aún depende de las fases posteriores.

## Fase 3 — Red y sincronización

**Estado: completada (Tasks 3.1–3.4).**

- **3.1 — Registro:** `BingoNetworking` registra los payloads S2C `sync_board`, `update_slot` y `sync_timer` al iniciar el mod.
- **3.2 — Tablero:** `sendBoard` y `sendBoardToAll` envían los 25 IDs de ítems. El cliente reconstruye el tablero y lo guarda en `ClientBingoState`.
- **3.3 — Casillas:** `sendSlotUpdate` y `sendSlotUpdateToAll` envían fila, columna y estado. El cliente ignora actualizaciones si no hay tablero o los índices son inválidos; en caso contrario, actualiza el tachado del HUD.
- **3.4 — Temporizador:** `sendTimer` y `sendTimerToAll` envían los ticks restantes. El cliente actualiza `ClientBingoState` y el HUD muestra el valor recibido.

La Fase 3 proporciona registro, envío y recepción. Los comandos que usan esos paquetes y el conteo del temporizador se implementan en las fases de comandos y servidor correspondientes.

**Verificación:** compilación completa y pruebas unitarias pasan. La prueba de integración dentro del juego queda para cuando Fases 4–5 conecten los comandos y la lógica del servidor.

## Fase 4 — Comandos

**Estado: en curso (Tasks 4.1–4.3 completadas; Task 4.4 pendiente; Task 4.5 reservada para el final).**

- `/bingo create` genera un tablero aleatorio independiente para cada jugador conectado y lo sincroniza solo con ese jugador.
- Acepta `line`, `diagonal`, `matrix`, `random` y las banderas SDD `-L`, `-D`, `-M`, `-R`.
- Si se omite el modo, se usa `RANDOM`.
- El modo seleccionado solo vive en memoria durante la ejecución actual; no se guarda y al reiniciar vuelve a `RANDOM`.
- `/bingo time <minutos>` acepta enteros de 5 a 60; el valor predeterminado es 10 minutos y se convierte a ticks al recibir el comando.
- `/bingo rounds <total>` acepta enteros de 3 a 15; el valor predeterminado es 5 rondas.
- Los ajustes se conservan solo durante la sesión del servidor y vuelven a sus valores predeterminados al iniciar una nueva.
- `/star` inicia una ronda con los valores configurados (10 minutos, 5 rondas y modo `RANDOM` por defecto), resuelve `RANDOM` a una condición concreta para esa ronda y distribuye a los jugadores en radio de 32 bloques.
- Si no se configuró `/bingo lobby`, `/star` guarda como lobby la posición y dimensión del jugador que lo ejecuta. La ronda requiere jugadores conectados y que todos tengan tablero.
- El temporizador se sincroniza al HUD cada segundo; al llegar a cero se detiene la ronda y también el escaneo del inventario.
- **4.4 — `/bingo finish`:** pendiente; deberá rechazar el cierre si no se inició ninguna ronda con `/star`.
- **4.5 — `/bingo help` y `/help`:** se implementarán al final, cuando todos los comandos estén definidos, para que la guía esté completa.
- `/bingo lobby` guarda la posición y dimensión actuales del jugador como lobby de la sesión; se limpia al iniciar un nuevo servidor.
- **Verificación:** compilación completa y pruebas unitarias de valores por defecto y resolución del modo aleatorio pasan.

## Fase 5 — Lógica de servidor, ranking y teletransporte

**Estado: en curso (Tasks 5.1–5.3 completadas; Tasks 5.4–5.5 pendientes).**

- `SpawnManager` guarda la posición del lobby y calcula posiciones `Vec3d` en círculo según índice, cantidad de jugadores y radio; conserva la altura del lobby y valida los parámetros.
- `/bingo lobby` configura la posición y dimensión del lobby que usará el flujo de ronda.
- Esta tarea calcula coordenadas; el teletransporte se conectará al inicio de ronda en Task 4.2/5.4.
- `BingoInventoryScanner` escanea los inventarios al final de cada tick del servidor para jugadores con tablero asignado.
- Cada UUID conserva su propio tablero y progreso; las actualizaciones se envían únicamente al HUD correspondiente. Crear otro tablero reemplaza el tablero y reinicia el progreso de ese jugador.
- El escaneo registra los ítems que permanecen en el inventario; no implementa todavía evaluación de victoria ni temporizador.
- `BingoWinConditions` comprueba filas o columnas completas, cualquiera de las dos diagonales principales y el cartón completo sobre una matriz 5×5.
- Los métodos rechazan matrices con tamaño incorrecto y no modifican el progreso. `RANDOM` se resuelve al iniciar la ronda; su integración con la victoria individual queda para Task 5.4.
- **Verificación:** pruebas unitarias de dispersión, detección idempotente y condiciones de victoria; compilación y pruebas pasan.
- El escaneo de inventarios solo corre durante una ronda activa iniciada por `/star`.
- Task 5.4 implementará la evaluación al ganar, la captura del tiempo individual y el teletransporte al lobby.

## Extras registrados

Los extras no forman parte del SDD y se implementan únicamente cuando corresponda a su fase o condición acordada.

| Extra | Fase prevista | Estado |
|---|---|---|
| `/bingo ranking reset` para limpiar el ranking en memoria | Fase 4 | Pendiente |
| Mostrar en el HUD la ronda actual y el total de rondas | Fase 5 | Pendiente |
| Botón para ampliar y centrar el HUD | Después de completar Fases 1–5 | Pendiente |

## Decisión pendiente de proceso

- El comando `/bingo pause` queda fuera del alcance actual y no se implementará mientras se define su comportamiento mediante un modelo Bizagi. No debe considerarse parte de los comandos planificados hasta revisar ese modelo.
