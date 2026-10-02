# 3. Implementation Tasks (Tasks)

## Fase 1: Estructura de Datos y Modelo Base
- [x] **Task 1.1:** Crear `BingoPool` con el catálogo base de ítems supervivencia.
- [x] **Task 1.2:** Crear `BingoSlot` con campos de `Item` y estado booleano de completado.
- [x] **Task 1.3:** Crear enum `GameMode` (`LINE`, `DIAGONAL`, `MATRIX`, `RANDOM`).
- [x] **Task 1.4:** Crear `BingoBoard` con matriz 5x5 y barajado sin duplicados.
- [x] **Task 1.5:** Crear las estructuras de datos para métricas: `PlayerRoundStats` y `RankingManager`.

## Fase 2: Visualización en el Cliente (HUD)
- [x] **Task 2.1:** Registrar `BingoHudOverlay` en `HudRenderCallback.EVENT`.
- [x] **Task 2.2:** Programar funciones auxiliares para cálculo de coordenadas de celdas.
- [x] **Task 2.3:** Implementar el renderizado nativo de ítems con `drawContext.drawItem(...)`.
- [x] **Task 2.4:** Renderizar el filtro translúcido sobre celdas completadas.
- [x] **Task 2.5:** Renderizar el temporizador en formato `MM:SS`.

## Fase 3: Red y Sincronización (Networking)
- [x] **Task 3.1:** Registrar identificadores y codecs (`CustomPayload`) de Fabric.
- [x] **Task 3.2:** Implementar paquete de envío de matriz (`SyncBoardS2CPayload`).
- [x] **Task 3.3:** Implementar paquete de actualización de casillas (`UpdateSlotS2CPayload`).
- [x] **Task 3.4:** Implementar sincronización de reloj (`SyncTimerS2CPayload`).

## Fase 4: Comandos de Control (Brigadier)
- [x] **Task 4.1:** Registrar `/bingo create` con banderas de modo (`-L`, `-D`, `-M`, `-R`) y generar un tablero aleatorio individual para cada jugador.
- [x] **Task 4.2:** Registrar `/star` acoplado al teletransporte de dispersión y arranque de reloj; usa 10 minutos, 5 rondas, modo aleatorio y radio de dispersión de 32 bloques por defecto. Si no se configuró lobby, toma la posición/dimensión del jugador que inicia.
- [x] **Task 4.3:** Registrar `/bingo time <minutos>` (entero 5–60, predeterminado 10) y `/bingo rounds <total>` (entero 3–15, predeterminado 5).
- [ ] **Task 4.4:** Registrar `/bingo finish`; solo puede finalizar una partida previamente iniciada con `/star`. Si la partida está en pausa, la cierra y libera el congelado.
- [ ] **Task 4.5:** Registrar `/bingo help` (y alias `/help`) al finalizar los demás comandos del proyecto; se implementa después de las Tasks 4.6–4.11 e incluye `/pause` y `/resume`.
- [ ] **Task 4.6:** Agregar el estado de pausa a `BingoRoundManager` (`pauseRound`, `resumeRound`, `isPaused`). La pausa solo es válida con una ronda activa y no pausada; la reanudación solo si está pausada. Durante la pausa el temporizador no avanza ni se sincroniza y `BingoInventoryScanner` no escanea. Métodos puros de validación con pruebas unitarias.
- [ ] **Task 4.7:** Crear `WorldFreezeController`: congela y descongela el mundo con el gestor de ticks del servidor (mecanismo de `/tick freeze`).
- [ ] **Task 4.8:** Crear `PlayerFreezeController`: durante la pausa bloquea mover, romper, colocar, usar ítems, interactuar, atacar y recibir daño; cierra los menús abiertos en servidor; congela a quien entre durante la pausa y restaura a quien salga o al detener el servidor.
- [ ] **Task 4.9:** Implementar `PauseS2CPayload` y su envío (`sendPauseToAll` y `sendPause` para quien entra durante la pausa); el cliente guarda el estado en `ClientBingoState`.
- [ ] **Task 4.10:** Implementar `BingoPauseScreen` en el cliente: mensaje "Partida en pausa" / "Esperando a que reanude la partida…" con el temporizador congelado. Bloquea el input; ESC abre el menú del juego y la pantalla reaparece al cerrarlo mientras siga la pausa; se cierra al reanudar o al desconectarse.
- [ ] **Task 4.11:** Registrar `/pause` y `/resume` (sin restricción de permisos, solo con una partida en curso), conectar las Tasks 4.6–4.10 y avisar en el chat al pausar y al reanudar.

## Fase 5: Lógica de Servidor, Ranking y Teletransporte
- [x] **Task 5.1:** Implementar `SpawnManager` (almacenamiento de lobby y algoritmo de dispersión matemática); `/bingo lobby` configura la ubicación de sesión.
- [x] **Task 5.2:** Implementar el bucle de escaneo de inventario en `ServerTickEvents.END_SERVER_TICK`.
- [x] **Task 5.3:** Implementar los métodos puros de victoria (`checkLines`, `checkDiagonals`, `checkFullCard`).
- [ ] **Task 5.4:** Implementar el flujo de victoria individual: captura de tiempo en ticks y teletransporte automático al lobby (pendiente hasta integrar Task 4.2). No evalúa la victoria mientras la partida esté en pausa.
- [ ] **Task 5.5:** Implementar el cálculo, ordenamiento y despliegue del ranking general al concluir las rondas.
