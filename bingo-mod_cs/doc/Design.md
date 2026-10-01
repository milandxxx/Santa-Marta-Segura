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