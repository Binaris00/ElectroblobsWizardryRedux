package com.electroblob.wizardry.common.content.entity;

import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.common.content.entity.projectile.ConjuredArrow;
import com.electroblob.wizardry.setup.registries.EBEntities;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityArrowRain extends ScaledConstructEntity {
    public EntityArrowRain(Level world) {
        super(EBEntities.ARROW_RAIN.get(), world);
        setSizeMultiplier(2 * 4);
    }

    public EntityArrowRain(EntityType<EntityArrowRain> entityType, Level level) {
        super(entityType, level);
        setSizeMultiplier(2 * 4);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if(!this.level().isClientSide){

            double x = this.xo + (this.level().random.nextDouble() - 0.5D) * (double)getBbWidth();
            double y = this.yo + this.level().random.nextDouble() * (double)getBbHeight();
            double z = this.zo + (this.level().random.nextDouble() - 0.5D) * (double)getBbWidth();

            ConjuredArrow arrow = new ConjuredArrow(x, y, z, this.level());

            arrow.setDeltaMovement(Mth.cos((float)Math.toRadians(this.getYRot() + 90)), -0.6, Mth.sin((float) Math.toRadians(this.getYRot() + 90)));

            arrow.setOwner(this.getCaster());
            arrow.setBaseDamage(7.0d * damageMultiplier);

            this.level().addFreshEntity(arrow);
        }
    }
}
