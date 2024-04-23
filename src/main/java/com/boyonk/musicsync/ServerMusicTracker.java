package com.boyonk.musicsync;

import com.boyonk.musicsync.network.packet.c2s.play.MusicTrackerUpdateC2SPacket;
import com.boyonk.musicsync.network.packet.s2c.play.PlayMusicS2CPacket;
import com.boyonk.musicsync.network.packet.s2c.play.StopMusicS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerMusicTracker {

	private static final TrackerData DEFAULT_TRACKING_DATA = new TrackerData() {

		@Override
		public Optional<MusicSound> type() {
			return Optional.empty();
		}

		@Override
		public boolean playing() {
			return false;
		}
	};
	public static final int DEFAULT_TIME_UNTIL_NEXT_SONG = 100;

	private final MinecraftServer server;

	private final Map<ServerPlayerEntity, TrackerData> trackerData = new HashMap<>();

	@Nullable
	private RegistryEntry<SoundEvent> current;
	private int timeUntilNextSong = DEFAULT_TIME_UNTIL_NEXT_SONG;

	private final Random random = Random.create();

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
			if (!type.getSound().getKey().equals(this.current.getKey()) && type.shouldReplaceCurrentMusic()) {
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
			this.server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, packet));

			this.current = null;
		}
	}

	protected void play(MusicSound type) {
		this.timeUntilNextSong = Integer.MAX_VALUE;

		this.current = type.getSound();

		PlayMusicS2CPacket packet = new PlayMusicS2CPacket(this.current, this.random.nextLong());
		this.server.getPlayerManager().getPlayerList().forEach(player -> ServerPlayNetworking.send(player, packet));
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
		List<MusicSound> types = this.trackerData.values().stream().map(TrackerData::type).filter(Optional::isPresent).map(Optional::get).toList();

		if (types.isEmpty()) return null;

		List<MusicSound> shouldReplaceTypes = types.stream().filter(MusicSound::shouldReplaceCurrentMusic).toList();

		if (!shouldReplaceTypes.isEmpty()) {
			return shouldReplaceTypes.get(this.random.nextInt(shouldReplaceTypes.size()));
		}

		return types.get(this.random.nextInt(types.size()));
	}

	protected boolean isPlaying() {
		return !this.trackerData.isEmpty() && this.trackerData.values().stream().anyMatch(TrackerData::playing);
	}


	public void onUpdate(MusicTrackerUpdateC2SPacket packet, ServerPlayerEntity player) {
		this.trackerData.put(player, packet);
	}

	public interface TrackerData {

		Optional<MusicSound> type();

		boolean playing();
	}

}

