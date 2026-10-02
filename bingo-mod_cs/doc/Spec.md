# 1. Product Specification (Spec)

## 1.1 Objetivo del Proyecto
Desarrollar un mod de minijuego de Bingo en Fabric (Minecraft 1.21.1, Java 21) para servidor dedicado (VPS), distribuido a los clientes mediante un launcher personalizado. Soporta partidas multijugador por rondas, teletransporte táctico, gestión de lobby, tablero visual HUD y tabla de clasificación (ranking) acumulativa por tiempos.

## 1.2 Reglas y Mecánicas de Juego
* **Tablero 5x5:** Matriz de 25 casillas únicas con ítems obtenibles en supervivencia vanilla sin comandos.
* **Detección Automática:** El servidor escanea inventarios en tiempo real; al obtener el ítem, se tacha en el cartón visual del cliente.
* **Teletransporte de Inicio:** Al iniciar la ronda, los jugadores son distribuidos y teletransportados a coordenadas dispersas (puntos de aparición independientes) para evitar disputas de recursos en el mismo bloque.
* **Extracción al Lobby:** En el momento exacto en que un jugador completa la condición de victoria de su cartón, es teletransportado de regreso a la zona de espera (Lobby) en modo espectador/inmune hasta el cierre de la ronda.

## 1.3 Modos de Juego
1. **Línea (`LINE` / `-L`):** Completar 5 casillas en fila o columna.
2. **Diagonal (`DIAGONAL` / `-D`):** Completar una de las diagonales principales.
3. **Cartón Lleno / Matriz (`MATRIX` / `-M`):** Completar las 25 casillas.
4. **Aleatorio (`RANDOM` / `-R`):** Elección estocástica por ronda.

## 1.4 Sistema de Rondas y Ranking de Tiempos
* **Rondas:** Secuencia de $N$ rondas consecutivas configuradas antes del inicio.
* **Métrica de Victoria (Ranking Acumulativo):**
    * Si un jugador completa el bingo en la ronda, se registra el tiempo transcurrido (ej. $300\text{ s}$ en ronda 1, $420\text{ s}$ en ronda 2).
    * Si el jugador no completa el tablero antes de expirar el tiempo, no suma puntos/tiempo registrado para esa ronda (o se marca como incompleto sin bonificación).
    * El ranking final se ordena de menor a mayor tiempo acumulado sumando únicamente las rondas completadas con éxito.

## 1.5 Comandos de Control
* `/bingo create [-L | -D | -M | -R]`: Genera un tablero aleatorio individual por jugador y sincroniza su HUD. El modo predeterminado es `RANDOM`.
* `/bingo lobby`: Guarda la posición y dimensión actuales del jugador que ejecuta el comando como lobby de la sesión.
* `/star`: Inicia el temporizador de la ronda y dispersa a los jugadores 32 bloques alrededor del lobby. Usa 10 minutos, 5 rondas y modo aleatorio por defecto. Si no se configuró un lobby, guarda la posición y dimensión del jugador que ejecuta el comando.
* `/bingo finish`: Cierra una partida iniciada con `/star` y calcula el ranking final; no funciona si no se inició una partida.
* `/bingo time <minutos>`: Configura el límite de tiempo por ronda como entero de 5 a 60 minutos (predeterminado: 10).
* `/bingo rounds <total>`: Configura el total de rondas como entero de 3 a 15 (predeterminado: 5).
* `/pause`: Pausa la partida en curso para todos los jugadores (ver 1.6). Cualquier jugador puede usarlo, pero solo funciona si hay una ronda activa que no esté ya en pausa.
* `/resume`: Reanuda una partida en pausa. Solo funciona si la partida está pausada.
* `/help` (o `/bingo help`): Muestra el manual de comandos, sintaxis y reglas del minijuego. Se implementa al final, cuando todos los demás comandos estén definidos.

## 1.6 Pausa de Partida
* **Objetivo:** Detener la partida en curso sin perder el avance de las rondas, para resolver imprevistos sin recurrir a `/bingo finish` y reiniciar.
* **Quién y cuándo:** Cualquier jugador puede ejecutar `/pause` y `/resume`; no requieren permisos especiales. `/pause` solo funciona con una ronda activa que no esté pausada y `/resume` solo con una partida pausada.
* **Qué se detiene:**
    * El temporizador de la ronda y su sincronización con el HUD. El tiempo en pausa no cuenta para el ranking.
    * El escaneo de inventarios y, cuando exista, la detección de victoria.
    * El mundo: mobs, ciclo día/noche, redstone, cultivos e ítems en el suelo.
    * Los jugadores: no pueden moverse, romper ni colocar bloques, usar ítems, interactuar, atacar ni recibir daño, de modo que nadie obtiene ítems durante la pausa.
* **Pantalla de pausa:** Todos los jugadores ven "Partida en pausa — Esperando a que reanude la partida…" con el temporizador congelado. La pantalla bloquea el input; ESC abre el menú del juego para poder desconectarse y la pantalla reaparece al cerrarlo mientras siga la pausa.
* **Jugadores que entran durante la pausa:** Quedan congelados y ven la misma pantalla.
* **Reanudar:** La ronda continúa desde el mismo tick en que se pausó.
* **Cierre:** `/bingo finish` durante una pausa cierra la partida y libera el congelado.
* **Persistencia:** El estado de pausa solo vive en memoria durante la sesión del servidor.
* **Limitaciones conocidas:** Los efectos de poción y el hambre de los jugadores siguen su curso (los jugadores conservan su tick en vanilla) y no se cubren clientes modificados.
