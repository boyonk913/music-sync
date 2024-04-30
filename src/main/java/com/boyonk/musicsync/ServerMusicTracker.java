package com.boyonk.musicsync;

import com.boyonk.musicsync.network.packet.c2s.play.MusicTrackerUpdateC2SPacket;
import com.boyonk.musicsync.network.packet.s2c.play.PlayMusicS2CPacket;
import com.boyonk.musicsync.network.packet.s2c.play.StopMusicS2CPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerMusicTracker {

	private static final TrackerData DEFAULT_TRACKING_DATA = new TrackerData() {

		@Override
		public @Nullable MusicSound getType() {
			return null;
		}

		@Override
		public boolean isPlaying() {
			return false;
		}
	};
	public static final int DEFAULT_TIME_UNTIL_NEXT_SONG = 100;

	private final MinecraftServer server;

	private final Map<ServerPlayerEntity, TrackerData> trackerData = new HashMap<>();

	@Nullable
	private SoundEvent current;
	private int timeUntilNextSong = DEFAULT_TIME_UNTIL_NEXT_SONG;

	private final Random random = new Random();

	private boolean enabled = true;

	public ServerMusicTracker(MinecraftServer server) {
		this.server = server;
	}

	public void tick() {
		if (!this.enabled) {
			if (!this.trackerData.isEmpty()) this.trackerData.clear();
			return;
		}

		if (!this.refreshPlayers()) return;

		MusicSound type = this.getMusicType();
		if (type == null) return;

		if (this.current != null) {
			if (!type.getSound().getId().equals(this.current.getId()) && type.shouldReplaceCurrentMusic()) {
				this.stop(MathHelper.nextInt(this.random, 0, type.getMinDelay() / 2));
			}
			if (!this.isPlaying()) {
				this.stop(Math.min(this.timeUntilNextSong, MathHelper.nextInt(this.random, type.getMinDelay(), type.getMaxDelay())));
			}
		}

		this.timeUntilNextSong = Math.min(this.timeUntilNextSong, type.getMaxDelay());
		this.timeUntilNextSong--;

		if (this.current == null && this.timeUntilNextSong <= 0) {
			this.play(type);
		}
	}

	public void enable() {
		if (!this.enabled) this.enabled = true;
	}

	public void disable() {
		if (this.enabled) this.enabled = false;
	}


	protected void stop() {
		this.stop(DEFAULT_TIME_UNTIL_NEXT_SONG);
	}

	public void stop(int timeUntilNextSong) {
		this.timeUntilNextSong = timeUntilNextSong;

		if (this.current != null) {
			StopMusicS2CPacket packet = new StopMusicS2CPacket();
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			packet.write(buf);

			this.server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, MusicSync.PACKET_STOP_MUSIC, buf));

			this.current = null;
		}
	}

	protected void play(MusicSound type) {
		this.timeUntilNextSong = Integer.MAX_VALUE;

		this.current = type.getSound();


		PlayMusicS2CPacket packet = new PlayMusicS2CPacket(this.current, this.random.nextLong());
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		packet.write(buf);

		this.server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, MusicSync.PACKET_PLAY_MUSIC, buf));

	}

	/**
	 * @return whether there are any players online
	 */
	protected boolean refreshPlayers() {
		Set<ServerPlayerEntity> currentPlayers = new HashSet<>(this.server.getPlayerManager().getPlayerList());

		boolean anyPlayers = !currentPlayers.isEmpty();

		Set<ServerPlayerEntity> trackedPlayers = trackerData.keySet();

		if (currentPlayers.equals(trackedPlayers)) return anyPlayers;

		List<ServerPlayerEntity> toAdd = currentPlayers.stream().filter(player -> !trackedPlayers.contains(player)).toList();
		List<ServerPlayerEntity> toRemove = trackedPlayers.stream().filter(player -> !currentPlayers.contains(player)).toList();

		toAdd.forEach(player -> this.trackerData.put(player, DEFAULT_TRACKING_DATA));
		toRemove.forEach(this.trackerData::remove);

		return anyPlayers;
	}

	@Nullable
	protected MusicSound getMusicType() {
		List<MusicSound> types = this.trackerData.values().stream().map(TrackerData::getType).filter(Objects::nonNull).toList();

		if (types.isEmpty()) return null;

		List<MusicSound> shouldReplaceTypes = types.stream().filter(MusicSound::shouldReplaceCurrentMusic).toList();

		if (!shouldReplaceTypes.isEmpty()) {
			return shouldReplaceTypes.get(this.random.nextInt(shouldReplaceTypes.size()));
		}

		return types.get(this.random.nextInt(types.size()));
	}

	protected boolean isPlaying() {
		return !this.trackerData.isEmpty() && this.trackerData.values().stream().anyMatch(TrackerData::isPlaying);
	}


	public void onUpdate(MusicTrackerUpdateC2SPacket packet, ServerPlayerEntity player) {
		this.trackerData.put(player, packet);
	}

	public interface TrackerData {

		@Nullable MusicSound getType();

		boolean isPlaying();
	}

}

