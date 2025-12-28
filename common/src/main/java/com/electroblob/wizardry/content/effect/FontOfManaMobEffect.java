package com.electroblob.wizardry.content.effect;

import com.electroblob.wizardry.api.content.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;

public class FontOfManaMobEffect extends MagicMobEffect {
    public FontOfManaMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x66ccff);
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
    }
}
