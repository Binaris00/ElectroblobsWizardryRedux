package com.binaris.wizardry.content.entity;

import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.binaris.wizardry.content.entity.projectile.ConjuredArrowEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ArrowRainConstruct extends ScaledConstructEntity {
    public ArrowRainConstruct(Level world) {
        super(EBEntities.ARROW_RAIN.get(), world);
        setSizeMultiplier(Spells.ARROW_RAIN.property(DefaultProperties.EFFECT_RADIUS) * 4);
    }

    public ArrowRainConstruct(EntityType<ArrowRainConstruct> entityType, Level level) {
        super(entityType, level);
        setSizeMultiplier(Spells.ARROW_RAIN.property(DefaultProperties.EFFECT_RADIUS) * 4);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            double x = this.xo + (this.level().random.nextDouble() - 0.5D) * (double) getBbWidth();
            double y = this.yo + this.level().random.nextDouble() * (double) getBbHeight();
            double z = this.zo + (this.level().random.nextDouble() - 0.5D) * (double) getBbWidth();

            ConjuredArrowEntity arrow = new ConjuredArrowEntity(x, y, z, this.level());

            arrow.setDeltaMovement(Mth.cos((float) Math.toRadians(this.getYRot() + 90)), -0.6, Mth.sin((float) Math.toRadians(this.getYRot() + 90)));
            arrow.setOwner(this.getCaster());
            arrow.setBaseDamage(7.0d * damageMultiplier);
            this.level().addFreshEntity(arrow);
        }
    }
}
