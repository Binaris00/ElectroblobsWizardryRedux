package com.electroblob.wizardry.api.common.effect;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public abstract class MagicMobEffect extends MobEffect implements CustomMobEffectParticles{
    public MagicMobEffect(MobEffectCategory category, int color) {super(category, color);}
}
