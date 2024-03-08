package com.boyonk.musicsync.client.mixin;

import com.boyonk.musicsync.client.TemporaryRandomSetter;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSoundInstance.class)
public class MixinAbstractSoundInstance implements TemporaryRandomSetter {

	@Unique
	@Nullable
	private Random musicsync$tempRandom = null;

	@Override
	public void setTemporaryRandom(Random random) {
		this.musicsync$tempRandom = random;
	}

	@Redirect(method = "getSoundSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/WeightedSoundSet;getSound(Lnet/minecraft/util/math/random/Random;)Lnet/minecraft/client/sound/Sound;"))
	Sound useTemporaryRandom(WeightedSoundSet instance, Random random) {
		if (musicsync$tempRandom != null) {
			Sound sound = instance.getSound(musicsync$tempRandom);
			musicsync$tempRandom = null;
			return sound;
		}
		return instance.getSound(random);
	}
}
