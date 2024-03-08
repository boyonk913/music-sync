package com.boyonk.musicsync.client.mixin;

import com.boyonk.musicsync.client.TemporaryRandomSetter;
import net.minecraft.client.sound.WeightedSoundSet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(WeightedSoundSet.class)
public class MixinWeightedSoundSet implements TemporaryRandomSetter {

	@Unique
	@Nullable
	private Random musicsync$tempRandom = null;

	@Override
	public void setTemporaryRandom(Random random) {
		this.musicsync$tempRandom = random;
	}

	@Redirect(method = "getSound()Lnet/minecraft/client/sound/Sound;", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
	int musicsync$useTemporaryRandom(Random instance, int i) {
		if (this.musicsync$tempRandom != null) {
			int result = this.musicsync$tempRandom.nextInt(i);
			this.musicsync$tempRandom = null;
			return result;
		}
		return instance.nextInt(i);
	}
}
