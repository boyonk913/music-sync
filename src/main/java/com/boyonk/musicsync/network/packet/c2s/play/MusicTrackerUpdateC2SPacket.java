package com.boyonk.musicsync.network.packet.c2s.play;

import com.boyonk.musicsync.ServerMusicTracker;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MusicTrackerUpdateC2SPacket implements Packet<ServerPlayPacketListener>, ServerMusicTracker.TrackerData {

	@Nullable
	private final MusicSound type;
	private final boolean playing;

	public MusicTrackerUpdateC2SPacket(@Nullable MusicSound type, boolean playing) {
		this.type = type;
		this.playing = playing;
	}

	public MusicTrackerUpdateC2SPacket(PacketByteBuf buf) {
		if (buf.readBoolean()) {
			RegistryEntry<SoundEvent> sound = buf.readRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), SoundEvent::fromBuf);
			int minDelay = buf.readVarInt();
			int maxDelay = buf.readVarInt();
			boolean shouldReplaceCurrentMusic = buf.readBoolean();
			this.type = new MusicSound(sound, minDelay, maxDelay, shouldReplaceCurrentMusic);
		} else {
			this.type = null;
		}
		this.playing = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {

		if (this.type != null) {
			buf.writeBoolean(true);
			buf.writeRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), this.type.getSound(), (b, sound) -> sound.writeBuf(b));
			buf.writeVarInt(type.getMinDelay());
			buf.writeVarInt(type.getMaxDelay());
			buf.writeBoolean(type.shouldReplaceCurrentMusic());
		} else {
			buf.writeBoolean(false);
		}
		buf.writeBoolean(this.playing);
	}

	public @Nullable MusicSound getType() {
		return type;
	}

	public boolean isPlaying() {
		return playing;
	}

	@Override
	public void apply(ServerPlayPacketListener listener) {

	}
}
