package com.electroblob.wizardry.content.effect;

import com.electroblob.wizardry.api.content.effect.MagicMobEffect;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.entity.construct.DecayConstruct;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DecayMobEffect extends MagicMobEffect {
    public DecayMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x3c006c);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "85602e0b-4801-4a87-94f3-bf617c97014e", -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity livingEntity, int $$1) {
        if (livingEntity.tickCount % 8 == 0 && !livingEntity.level().isClientSide && livingEntity.onGround()) {
            List<Entity> entities = livingEntity.level().getEntities(livingEntity, livingEntity.getBoundingBox());

            // Don't spawn another decay if there's already one there
            for (Entity entity : entities) {
                if (entity instanceof DecayConstruct) return;
            }

            // The victim spreading the decay is the 'caster' here, so that it can actually wear off, otherwise it
            // just gets infected with its own decay and the effect lasts forever.
            DecayConstruct decay = new DecayConstruct(EBEntities.DECAY.get(), livingEntity.level());
            if(livingEntity instanceof Player player) decay.setCaster(player);
            decay.setPos(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            livingEntity.level().addFreshEntity(decay);

            if(!EBMagicDamageSource.isEntityImmune(EBDamageSources.WITHER, livingEntity))
                livingEntity.hurt(livingEntity.damageSources().wither(), 1);
        }
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {

    }

    @Override
    public boolean isDurationEffectTick(int $$0, int $$1) {
        return true;
    }
}
