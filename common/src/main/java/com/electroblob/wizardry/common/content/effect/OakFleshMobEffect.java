package com.electroblob.wizardry.common.content.effect;

import com.electroblob.wizardry.api.common.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class OakFleshMobEffect extends MagicMobEffect {

    public OakFleshMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "98b4ba66-7c50-4a4c-9f3f-40bcb37313b5", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MAX_HEALTH, "ed9d0423-60f4-4998-bd8d-dc7c33bd45b8", 0.2f, AttributeModifier.Operation.MULTIPLY_BASE);
        this.addAttributeModifier(Attributes.ARMOR, "0b607c3f-fb14-43d7-96b5-1c1b6f6da242", 3.0f, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {

    }

    @Override
    public boolean isDurationEffectTick(int $$0, int $$1) {
        return true;
    }
}
