package com.boyonk.musicsync.client;

import com.boyonk.musicsync.network.packet.s2c.play.PlayMusicS2CPacket;
import com.boyonk.musicsync.network.packet.s2c.play.StopMusicS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class MusicSyncClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(StopMusicS2CPacket.ID, (payload, context) -> context.client().getMusicTracker().stop());
		ClientPlayNetworking.registerGlobalReceiver(PlayMusicS2CPacket.ID, (payload, context) -> ((ClientMusicTracker) context.client().getMusicTracker()).play(payload.sound(), payload.seed()));
	}
}
