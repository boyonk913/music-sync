package com.boyonk.musicsync.network.packet.c2s.play;

import com.boyonk.musicsync.ServerMusicTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;


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
			SoundEvent sound = Registry.SOUND_EVENT.get(buf.readIdentifier());
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
	public void read(PacketByteBuf buf) throws IOException {

	}

	@Override
	public void write(PacketByteBuf buf) {

		if (this.type != null) {
			buf.writeBoolean(true);
			buf.writeIdentifier(this.type.getEvent().getId());
			buf.writeVarInt(type.method_27280());
			buf.writeVarInt(type.method_27281());
			buf.writeBoolean(type.method_27282());
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
