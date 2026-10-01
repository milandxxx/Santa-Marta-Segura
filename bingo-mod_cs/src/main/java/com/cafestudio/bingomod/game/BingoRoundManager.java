package com.cafestudio.bingomod.game;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.cafestudio.bingomod.model.GameMode;
import com.cafestudio.bingomod.network.BingoNetworking;
import com.cafestudio.bingomod.spawn.SpawnManager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public final class BingoRoundManager {
	public static final double DEFAULT_SPREAD_RADIUS = 32.0;

	private static final BingoRoundManager INSTANCE = new BingoRoundManager();
	private static final int TICKS_PER_SECOND = 20;

	private boolean roundActive;
	private long remainingTicks;
	private int ticksSinceLastTimerSync;
	private int roundsStarted;
	private int totalRounds;
	private GameMode activeMode = GameMode.RANDOM;

	private BingoRoundManager() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(INSTANCE::tick);
		ServerLifecycleEvents.SERVER_STARTED.register(server -> INSTANCE.reset());
	}

	public static boolean isRoundActive() {
		return INSTANCE.roundActive;
	}

	public static boolean hasStartedRound() {
		return INSTANCE.roundsStarted > 0;
	}

	public static int getRoundsStarted() {
		return INSTANCE.roundsStarted;
	}

	public static GameMode getActiveMode() {
		return INSTANCE.activeMode;
	}

	public static int startRound(
			ServerCommandSource source,
			GameMode selectedMode,
			long timeLimitTicks,
			int configuredTotalRounds) {
		if (INSTANCE.roundActive) {
			source.sendError(Text.literal("Ya hay una ronda activa."));
			return 0;
		}
		if (INSTANCE.roundsStarted >= configuredTotalRounds) {
			source.sendError(Text.literal("Ya se iniciaron todas las rondas configuradas."));
			return 0;
		}

		List<ServerPlayerEntity> players = source.getServer().getPlayerManager().getPlayerList();
		if (players.isEmpty()) {
			source.sendError(Text.literal("No hay jugadores conectados para iniciar la ronda."));
			return 0;
		}
		for (ServerPlayerEntity player : players) {
			if (!BingoInventoryScanner.hasBoard(player.getUuid())) {
				source.sendError(Text.literal("Todos los jugadores deben tener un tablero. Usa /bingo create primero."));
				return 0;
			}
		}

		SpawnManager spawnManager = SpawnManager.getInstance();
		if (spawnManager.getLobbyPos().isEmpty() || spawnManager.getLobbyWorld().isEmpty()) {
			if (!(source.getEntity() instanceof ServerPlayerEntity commandPlayer)) {
				source.sendError(Text.literal("Configura el lobby con /bingo lobby o ejecuta /star como jugador."));
				return 0;
			}
			spawnManager.setLobby(commandPlayer.getServerWorld(), commandPlayer.getBlockPos());
		}

		ServerWorld lobbyWorld = spawnManager.getLobbyWorld().orElseThrow(
				() -> new IllegalStateException("Lobby position exists without a world"));
		for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
			teleportToSpreadPosition(players.get(playerIndex), playerIndex, players.size(), spawnManager, lobbyWorld);
		}

		INSTANCE.start(timeLimitTicks, configuredTotalRounds, resolveMode(selectedMode));
		BingoInventoryScanner.setRoundActive(true);
		BingoNetworking.sendTimerToAll(source.getServer(), timeLimitTicks);
		source.getServer().getPlayerManager().broadcast(
				Text.literal("Ronda " + INSTANCE.roundsStarted + " de " + INSTANCE.totalRounds
						+ " iniciada. Modo: " + INSTANCE.activeMode.name()),
				false);
		return 1;
	}

	private static void teleportToSpreadPosition(
			ServerPlayerEntity player,
			int playerIndex,
			int totalPlayers,
			SpawnManager spawnManager,
			ServerWorld lobbyWorld) {
		Vec3d position = spawnManager.calculateSpreadPos(
				playerIndex,
				totalPlayers,
				DEFAULT_SPREAD_RADIUS);
		player.teleport(
				lobbyWorld,
				position.x,
				position.y,
				position.z,
				player.getYaw(),
				player.getPitch());
	}

	static GameMode resolveMode(GameMode selectedMode) {
		if (selectedMode != GameMode.RANDOM) {
			return selectedMode;
		}

		GameMode[] concreteModes = {GameMode.LINE, GameMode.DIAGONAL, GameMode.MATRIX};
		return concreteModes[ThreadLocalRandom.current().nextInt(concreteModes.length)];
	}

	private void start(long timeLimitTicks, int configuredTotalRounds, GameMode gameMode) {
		roundActive = true;
		remainingTicks = timeLimitTicks;
		ticksSinceLastTimerSync = 0;
		totalRounds = configuredTotalRounds;
		roundsStarted++;
		activeMode = gameMode;
	}

	private void tick(MinecraftServer server) {
		if (!roundActive) {
			return;
		}

		remainingTicks = Math.max(0L, remainingTicks - 1L);
		ticksSinceLastTimerSync++;
		if (ticksSinceLastTimerSync >= TICKS_PER_SECOND || remainingTicks == 0L) {
			BingoNetworking.sendTimerToAll(server, remainingTicks);
			ticksSinceLastTimerSync = 0;
		}
		if (remainingTicks == 0L) {
			roundActive = false;
			BingoInventoryScanner.setRoundActive(false);
			server.getPlayerManager().broadcast(Text.literal("Tiempo de la ronda agotado."), false);
		}
	}

	private void reset() {
		roundActive = false;
		remainingTicks = 0L;
		ticksSinceLastTimerSync = 0;
		roundsStarted = 0;
		totalRounds = 0;
		activeMode = GameMode.RANDOM;
	}
}
