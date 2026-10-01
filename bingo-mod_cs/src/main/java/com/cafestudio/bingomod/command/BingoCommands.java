package com.cafestudio.bingomod.command;

import com.cafestudio.bingomod.game.BingoInventoryScanner;
import com.cafestudio.bingomod.game.BingoRoundManager;
import com.cafestudio.bingomod.model.BingoBoard;
import com.cafestudio.bingomod.model.BingoPool;
import com.cafestudio.bingomod.model.GameMode;
import com.cafestudio.bingomod.network.BingoNetworking;
import com.cafestudio.bingomod.spawn.SpawnManager;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class BingoCommands {
	static final int DEFAULT_TIME_MINUTES = 10;
	static final int DEFAULT_TOTAL_ROUNDS = 5;
	private static final int TICKS_PER_SECOND = 20;
	private static final int SECONDS_PER_MINUTE = 60;

	private static GameMode currentMode = GameMode.RANDOM;
	private static long timeLimitTicks = minutesToTicks(DEFAULT_TIME_MINUTES);
	private static int totalRounds = DEFAULT_TOTAL_ROUNDS;

	private BingoCommands() {
	}

	public static void register() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			resetSessionSettings();
			SpawnManager.getInstance().clearLobby();
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("star")
					.executes(context -> BingoRoundManager.startRound(
							context.getSource(),
							currentMode,
							timeLimitTicks,
							totalRounds)));
			dispatcher.register(createBingoRoot());
		});
	}

	public static GameMode getCurrentMode() {
		return currentMode;
	}

	public static long getTimeLimitTicks() {
		return timeLimitTicks;
	}

	public static int getTotalRounds() {
		return totalRounds;
	}

	static long minutesToTicks(int minutes) {
		return minutes * (long) SECONDS_PER_MINUTE * TICKS_PER_SECOND;
	}

	private static LiteralArgumentBuilder<ServerCommandSource> createModeArgument(String name, GameMode gameMode) {
		return CommandManager.literal(name)
				.executes(context -> createBoard(context.getSource(), gameMode));
	}

	private static LiteralArgumentBuilder<ServerCommandSource> createCommand() {
		return CommandManager.literal("create")
				.executes(context -> createBoard(context.getSource(), GameMode.RANDOM))
				.then(createModeArgument("line", GameMode.LINE))
				.then(createModeArgument("diagonal", GameMode.DIAGONAL))
				.then(createModeArgument("matrix", GameMode.MATRIX))
				.then(createModeArgument("random", GameMode.RANDOM))
				.then(createModeArgument("-L", GameMode.LINE))
				.then(createModeArgument("-D", GameMode.DIAGONAL))
				.then(createModeArgument("-M", GameMode.MATRIX))
				.then(createModeArgument("-R", GameMode.RANDOM));
	}

	private static LiteralArgumentBuilder<ServerCommandSource> createBingoRoot() {
		return CommandManager.literal("bingo")
				.then(createCommand())
				.then(CommandManager.literal("lobby")
						.executes(context -> setLobby(context.getSource())))
				.then(CommandManager.literal("time")
						.then(CommandManager.argument("minutes", IntegerArgumentType.integer(5, 60))
								.executes(context -> setTimeLimit(
										context.getSource(),
										IntegerArgumentType.getInteger(context, "minutes")))))
				.then(CommandManager.literal("rounds")
						.then(CommandManager.argument("total", IntegerArgumentType.integer(3, 15))
								.executes(context -> setTotalRounds(
										context.getSource(),
										IntegerArgumentType.getInteger(context, "total")))));
	}

	private static void resetSessionSettings() {
		currentMode = GameMode.RANDOM;
		timeLimitTicks = minutesToTicks(DEFAULT_TIME_MINUTES);
		totalRounds = DEFAULT_TOTAL_ROUNDS;
	}

	private static int createBoard(ServerCommandSource source, GameMode gameMode) {
		currentMode = gameMode;
		for (var player : source.getServer().getPlayerManager().getPlayerList()) {
			BingoBoard board = BingoBoard.generateBoard(new BingoPool());
			BingoInventoryScanner.setBoard(player.getUuid(), board);
			BingoNetworking.sendBoard(player, board);
		}
		source.sendFeedback(
				() -> Text.literal("Tableros de Bingo individuales creados. Modo: " + gameMode.name()),
				false);
		return 1;
	}

	private static int setTimeLimit(ServerCommandSource source, int minutes) {
		timeLimitTicks = minutesToTicks(minutes);
		source.sendFeedback(
				() -> Text.literal("Tiempo por ronda establecido en " + minutes + " minutos."),
				false);
		return 1;
	}

	private static int setTotalRounds(ServerCommandSource source, int rounds) {
		totalRounds = rounds;
		source.sendFeedback(
				() -> Text.literal("Número de rondas establecido en " + rounds + "."),
				false);
		return 1;
	}

	private static int setLobby(ServerCommandSource source) throws CommandSyntaxException {
		var player = source.getPlayerOrThrow();
		SpawnManager.getInstance().setLobby(player.getServerWorld(), player.getBlockPos());
		var lobbyPos = player.getBlockPos();
		source.sendFeedback(
				() -> Text.literal("Lobby establecido en " + lobbyPos.getX() + ", "
						+ lobbyPos.getY() + ", " + lobbyPos.getZ() + "."),
				false);
		return 1;
	}
}
