package com.boyonk.musicsync;

import com.boyonk.musicsync.network.packet.c2s.play.MusicTrackerUpdateC2SPacket;
import com.boyonk.musicsync.server.command.MusicCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class MusicSync implements ModInitializer {

	public static final String NAMESPACE = "musicsync";

	public static final Identifier PACKET_MUSIC_TRACKER_UPDATE = new Identifier(NAMESPACE, "music_tracker_update");
	public static final Identifier PACKET_PLAY_MUSIC = new Identifier(NAMESPACE, "play_music");
	public static final Identifier PACKET_STOP_MUSIC = new Identifier(NAMESPACE, "stop_music");

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(MusicCommand::register);
		ServerPlayNetworking.registerGlobalReceiver(MusicSync.PACKET_MUSIC_TRACKER_UPDATE, (server, player, handler, buf, responseSender) -> {
			MusicTrackerUpdateC2SPacket packet = new MusicTrackerUpdateC2SPacket(buf);
			((ServerMusicTrackerHolder) server).getMusicTracker().onUpdate(packet, player);
		});

		ServerTickEvents.END_SERVER_TICK.register((server) -> ((ServerMusicTrackerHolder) server).getMusicTracker().tick());

	}
}
