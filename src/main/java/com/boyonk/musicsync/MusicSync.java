package com.boyonk.musicsync;

import com.boyonk.musicsync.network.packet.c2s.play.MusicTrackerUpdateC2SPacket;
import com.boyonk.musicsync.network.packet.s2c.play.PlayMusicS2CPacket;
import com.boyonk.musicsync.network.packet.s2c.play.StopMusicS2CPacket;
import com.boyonk.musicsync.server.command.MusicCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;

public class MusicSync implements ModInitializer {

	public static final String NAMESPACE = "musicsync";

	public static final PacketCodec<RegistryByteBuf, MusicSound> MUSIC_SOUND_PACKET_CODEC = PacketCodec.tuple(
			SoundEvent.ENTRY_PACKET_CODEC, MusicSound::getSound,
			PacketCodecs.VAR_INT, MusicSound::getMinDelay,
			PacketCodecs.VAR_INT, MusicSound::getMaxDelay,
			PacketCodecs.BOOL, MusicSound::shouldReplaceCurrentMusic,
			MusicSound::new
	);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(MusicCommand::register);

		PayloadTypeRegistry.playC2S().register(MusicTrackerUpdateC2SPacket.ID, MusicTrackerUpdateC2SPacket.CODEC);

		PayloadTypeRegistry.playS2C().register(PlayMusicS2CPacket.ID, PlayMusicS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(StopMusicS2CPacket.ID, StopMusicS2CPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(MusicTrackerUpdateC2SPacket.ID, (payload, context) -> ((ServerMusicTrackerHolder) context.player().getServerWorld().getServer()).getMusicTracker().onUpdate(payload, context.player()));

		ServerTickEvents.END_SERVER_TICK.register((server) -> ((ServerMusicTrackerHolder) server).getMusicTracker().tick());
	}
}
