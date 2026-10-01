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
- [ ] **Task 4.4:** Registrar `/bingo finish`; solo puede finalizar una partida previamente iniciada con `/star`.
- [ ] **Task 4.5:** Registrar `/bingo help` (y alias `/help`) al finalizar los demás comandos del proyecto.

## Fase 5: Lógica de Servidor, Ranking y Teletransporte
- [x] **Task 5.1:** Implementar `SpawnManager` (almacenamiento de lobby y algoritmo de dispersión matemática); `/bingo lobby` configura la ubicación de sesión.
- [x] **Task 5.2:** Implementar el bucle de escaneo de inventario en `ServerTickEvents.END_SERVER_TICK`.
- [x] **Task 5.3:** Implementar los métodos puros de victoria (`checkLines`, `checkDiagonals`, `checkFullCard`).
- [ ] **Task 5.4:** Implementar el flujo de victoria individual: captura de tiempo en ticks y teletransporte automático al lobby (pendiente hasta integrar Task 4.2).
- [ ] **Task 5.5:** Implementar el cálculo, ordenamiento y despliegue del ranking general al concluir las rondas.