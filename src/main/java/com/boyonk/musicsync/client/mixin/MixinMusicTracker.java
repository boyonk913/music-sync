package com.boyonk.musicsync.client.mixin;

import com.boyonk.musicsync.client.ClientMusicTracker;
import com.boyonk.musicsync.client.TemporaryRandomSetter;
import com.boyonk.musicsync.network.packet.c2s.play.MusicTrackerUpdateC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(MusicTracker.class)
public class MixinMusicTracker implements ClientMusicTracker {

	@Unique
	private @Nullable MusicSound musicsync$type = null;

	@Unique
	private boolean musicsync$playing = false;

	@Unique
	private boolean musicsync$dirty = true;

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private @Nullable SoundInstance current;

	@Shadow
	private int timeUntilNextSong;

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	void tick(CallbackInfo ci) {
		if (this.isInGame()) {
			MusicSound type = this.client.getMusicType();
			this.setType(type);

			if (this.current != null) {
				if (!this.client.getSoundManager().isPlaying(this.current)) {
					this.current = null;
				}
			}

			this.setPlaying(this.current != null);

			this.popDirty();

			ci.cancel();
		}
	}

	@Unique
	private void setType(MusicSound type) {
		if (!type.equals(musicsync$type)) {
			this.musicsync$type = type;
			this.markDirty();
		}
	}

	private void setPlaying(boolean playing) {
		if (playing != musicsync$playing) {
			this.musicsync$playing = playing;
			this.markDirty();
		}
	}

	private void markDirty() {
		this.musicsync$dirty = true;
	}

	private void popDirty() {
		if (!this.musicsync$dirty) return;

		this.musicsync$dirty = false;

		MusicTrackerUpdateC2SPacket packet = new MusicTrackerUpdateC2SPacket(Optional.ofNullable(this.musicsync$type), this.musicsync$playing);
		ClientPlayNetworking.send(packet);
	}

	private boolean isInGame() {
		return MinecraftClient.getInstance().getNetworkHandler() != null;
	}

	@Override
	public void play(@Nullable RegistryEntry<SoundEvent> event, long seed) {
		if (event == null) return;

		this.current = PositionedSoundInstance.music(event.value());
		((TemporaryRandomSetter) this.current).setTemporaryRandom(Random.create(seed));
		if (this.current.getSound() != SoundManager.MISSING_SOUND) {
			this.client.getSoundManager().play(this.current);
		}

		this.timeUntilNextSong = Integer.MAX_VALUE;
	}
}
