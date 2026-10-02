# 2. Technical Design (Design)

## 2.1 Componentes Técnicos Nuevos

### 1. Gestión Espacial (Lobby y Puntos de Inicio)
* **`SpawnManager`:**
    * Almacena la coordenada y dimensión del Lobby, configuradas con `/bingo lobby` desde la ubicación actual del jugador.
    * La ubicación se conserva únicamente en memoria durante la sesión y se limpia al iniciar una nueva.
    * Generador circular: `calculateSpreadPos(int playerIndex, int totalPlayers, double spreadRadius)` devuelve una posición `Vec3d` distribuida alrededor del lobby, conservando su altura.
    * Teletransporte en servidor mediante `ServerPlayerEntity.teleport(...)`.
* **Inicio de ronda (`BingoRoundManager`):**
    * `/star` inicia la ronda con el tiempo y las rondas configuradas (predeterminados: 10 minutos y 5 rondas).
    * Si no hay lobby guardado, usa como lobby la posición y dimensión del jugador que ejecutó `/star`.
    * Distribuye a los jugadores en círculo a un radio predeterminado de 32 bloques, activa el escaneo y sincroniza la cuenta regresiva cada segundo.
    * Resuelve `RANDOM` una vez por ronda entre `LINE`, `DIAGONAL` y `MATRIX`.
    * Rechaza iniciar si no hay jugadores, si falta algún tablero o si ya no quedan rondas configuradas.

### 2. Sistema de Registro y Ranking de Tiempos
* **`PlayerRoundStats`:**
    * Registro por jugador: `UUID playerId`, `int roundNumber`, `long completionTicks`, `boolean completed`.
* **`RankingManager`:**
    * Almacena el historial de tiempos: `Map<UUID, List<Long>> playerTimes`.
    * Método de agregación: `calculateTotalTime(UUID playerId)` que suma los ticks de rondas finalizadas con éxito.
    * Comparador para ordenar el podio: Orden ascendente de tiempo total acumulado (menor tiempo = mejor posición).

### 3. Máquina de Estados de Ronda (`BingoGameManager`)
* **Escaneo de inventarios (`BingoInventoryScanner`):**
    * Cada jugador recibe un tablero aleatorio propio; su progreso se conserva por UUID.
    * Al detectar en el inventario un ítem de su tablero aún no marcado, actualiza solo el HUD de ese jugador.
* **Condiciones de victoria (`BingoWinConditions`):**
    * Métodos puros `checkLines`, `checkDiagonals` y `checkFullCard` evalúan una matriz 5×5 de casillas completadas.
    * La selección de modalidad para `RANDOM` y la conexión con el flujo de ronda se realizan fuera de estos métodos.
* **Evento de Victoria Individual:**
    * Al detectar condición de victoria:
        1. Marca el estado del jugador como finalizado.
        2. Detiene su cronómetro personal de ronda y guarda el tiempo.
        3. Notifica globalmente el logro.
        4. Ejecuta `player.teleport(lobbyPos)`.
* **Cierre de Ronda:**
    * Ocurre si todos los jugadores terminan o si el temporizador global llega a cero.
    * Si `currentRound < totalRounds`, prepara el siguiente tablero y espera nuevo `/star`.
    * Si `currentRound == totalRounds`, ejecuta el cierre y publica el ranking global en el chat.
    * `/bingo finish` solo puede cerrar una partida que haya iniciado con `/star`.

### 4. Comando de Asistencia
* `/help` / `/bingo help`: Envía mensajes formateados con componentes de texto nativos (`Text.literal(...)`) detallando las banderas (`-L`, `-D`, `-M`, `-R`), comandos administrativos y el funcionamiento del ranking por suma de tiempos.

### 5. Pausa de Partida
* **Estado (`BingoRoundManager`):**
    * Bandera `paused`; `pauseRound` y `resumeRound` validan la transición (pausa solo con ronda activa y no pausada; reanudación solo si está pausada).
    * Con `paused` activo, `tick` no descuenta ni sincroniza el temporizador y `BingoInventoryScanner` suspende el escaneo; ambos continúan al reanudar.
* **`WorldFreezeController`:**
    * Usa `server.getTickManager().setFrozen(...)` (mecanismo de `/tick freeze`), que detiene mobs, ciclo día/noche, redstone, cultivos e ítems en el suelo.
    * Los jugadores conservan su tick en vanilla, por lo que se complementa con `PlayerFreezeController`.
* **`PlayerFreezeController`:**
    * Bloqueo de acciones con eventos de Fabric: `UseBlockCallback`, `UseItemCallback`, `UseEntityCallback`, `AttackBlockCallback`, `AttackEntityCallback` y `PlayerBlockBreakEvents.BEFORE` devuelven fallo durante la pausa; `ServerLivingEntityEvents.ALLOW_DAMAGE` cancela el daño a jugadores.
    * Inmovilización: guarda la posición de cada jugador al pausar, desactiva su gravedad y, en `END_SERVER_TICK`, lo devuelve a esa posición si se desvió (`ServerPlayerEntity.teleport`). Cierra los menús abiertos en servidor.
    * Ciclo de vida: `ServerPlayConnectionEvents.JOIN` congela a quien entra durante la pausa; `DISCONNECT` y `ServerLifecycleEvents.SERVER_STOPPING` restauran el estado antes de que Minecraft guarde al jugador. Como la gravedad desactivada se guarda con el jugador, se marca con una etiqueta de comando para corregirla al volver a entrar si el servidor cayó en pausa.
* **Red:**
    * `PauseS2CPayload(boolean paused)` registrado en `BingoNetworking`; se envía a todos al pausar y reanudar, y a quien entra durante la pausa.
    * El cliente guarda el estado en `ClientBingoState`.
* **Cliente (`BingoPauseScreen`):**
    * Pantalla que no pausa el juego (`shouldPause()` falso); dibuja el mensaje y el temporizador congelado, y ESC abre `GameMenuScreen`.
    * Un `ClientTickEvents.END_CLIENT_TICK` la vuelve a abrir mientras la partida siga pausada y no haya otra pantalla abierta; se cierra al reanudar o desconectarse.
* **Comandos (`BingoCommands`):** `/pause` y `/resume` de nivel superior, como `/star`, sin restricción de permisos. Delegan en `BingoRoundManager` y avisan en el chat.
* **Integración:** `/bingo finish` (Task 4.4) libera la pausa si existe; el flujo de victoria (Task 5.4) no evalúa durante la pausa.
