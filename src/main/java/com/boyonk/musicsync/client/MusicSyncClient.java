package com.boyonk.musicsync.client;

import com.boyonk.musicsync.MusicSync;
import com.boyonk.musicsync.network.packet.s2c.play.PlayMusicS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class MusicSyncClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(MusicSync.PACKET_STOP_MUSIC, (client, handler, buf, responseSender) -> {
			client.getMusicTracker().stop();
		});

		ClientPlayNetworking.registerGlobalReceiver(MusicSync.PACKET_PLAY_MUSIC, (client, handler, buf, responseSender) -> {
			PlayMusicS2CPacket packet = new PlayMusicS2CPacket(buf);
			((ClientMusicTracker) client.getMusicTracker()).play(packet.getSound(), packet.getSeed());
		});
	}
}
