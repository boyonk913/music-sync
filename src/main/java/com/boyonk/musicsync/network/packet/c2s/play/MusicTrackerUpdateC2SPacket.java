package com.boyonk.musicsync.network.packet.c2s.play;

import com.boyonk.musicsync.MusicSync;
import com.boyonk.musicsync.ServerMusicTracker;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record MusicTrackerUpdateC2SPacket(Optional<MusicSound> type, boolean playing) implements CustomPayload, ServerMusicTracker.TrackerData {

	public static final Id<MusicTrackerUpdateC2SPacket> ID = new Id<>(new Identifier(MusicSync.NAMESPACE, "music_tracker_update"));
	public static final PacketCodec<RegistryByteBuf, MusicTrackerUpdateC2SPacket> CODEC = PacketCodec.tuple(
			PacketCodecs.optional(MusicSync.MUSIC_SOUND_PACKET_CODEC), MusicTrackerUpdateC2SPacket::type,
			PacketCodecs.BOOL, MusicTrackerUpdateC2SPacket::playing,
			MusicTrackerUpdateC2SPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
