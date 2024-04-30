package com.boyonk.musicsync.server.command;

import com.boyonk.musicsync.ServerMusicTracker;
import com.boyonk.musicsync.ServerMusicTrackerHolder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MusicCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(literal("music")
				.requires(source -> source.hasPermissionLevel(3))
				.then(literal("reset")
						.executes(ctx -> executeReset(ctx.getSource(), ServerMusicTracker.DEFAULT_TIME_UNTIL_NEXT_SONG))
						.then(argument("delay", IntegerArgumentType.integer(0))
								.executes(ctx -> executeReset(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "delay")))
						)
				)
				.then(literal("enable")
						.executes(ctx -> executeEnable(ctx.getSource()))
				)
				.then(literal("disable")
						.executes(ctx -> executeDisable(ctx.getSource()))
				)
		);
	}


	private static int executeReset(ServerCommandSource source, int delay) {
		ServerMusicTracker musicTracker = ((ServerMusicTrackerHolder) source.getServer()).getMusicTracker();

		musicTracker.stop(delay);
		return 1;
	}

	private static int executeEnable(ServerCommandSource source) {
		ServerMusicTracker musicTracker = ((ServerMusicTrackerHolder) source.getServer()).getMusicTracker();

		musicTracker.enable();

		return 1;
	}

	private static int executeDisable(ServerCommandSource source) {
		ServerMusicTracker musicTracker = ((ServerMusicTrackerHolder) source.getServer()).getMusicTracker();

		musicTracker.disable();
		return 0;
	}


}
