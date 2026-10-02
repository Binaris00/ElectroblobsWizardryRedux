package com.binaris.wizardry.api.content.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/// A [MobEffectInstance] applied by wizardry spells ("magic" effects). It stores a marker in its NBT
/// representation so it can be told apart from a regular [MobEffectInstance] after save/load.
public class MagicMobEffectInstance extends MobEffectInstance {
    public static final String MAGIC_TAG = "ebwizardry:magic";

    public MagicMobEffectInstance(MobEffect effect) {
        super(effect);
    }

    public MagicMobEffectInstance(MobEffect effect, int duration) {
        super(effect, duration);
    }

    public MagicMobEffectInstance(MobEffect effect, int duration, int amplifier) {
        super(effect, duration, amplifier);
    }

    public MagicMobEffectInstance(MobEffect effect, int duration, int amplifier, boolean ambient, boolean visible) {
        super(effect, duration, amplifier, ambient, visible);
    }

    public MagicMobEffectInstance(MobEffect effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
        super(effect, duration, amplifier, ambient, visible, showIcon);
    }

    public MagicMobEffectInstance(MobEffectInstance other) {
        super(other);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag = super.save(tag);
        tag.putBoolean(MAGIC_TAG, true);
        return tag;
    }
}