package com.electroblob.wizardry.content.entity.construct;

import com.electroblob.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.electroblob.wizardry.content.entity.projectile.IceShardEntity;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HailstormConstruct extends ScaledConstructEntity {

    public HailstormConstruct(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public HailstormConstruct(Level level) {
        super(EBEntities.HAILSTORM.get(), level);
        this.lifetime = Spells.HAILSTORM.property(DefaultProperties.DURATION);
    }

    @Override
    public void tick() {

        super.tick();

        if (!this.level().isClientSide) {

            double x = getX() + (level().random.nextDouble() - 0.5D) * (double) getBbWidth();
            double y = getY() + level().random.nextDouble() * (double) getBbHeight();
            double z = getZ() + (level().random.nextDouble() - 0.5D) * (double) getBbWidth();

            IceShardEntity iceshard = new IceShardEntity(level());
            iceshard.setPos(x, y, z);

            iceshard.setDeltaMovement(Mth.cos((float) Math.toRadians(this.getYRot() + 90)), -0.6, Mth.sin((float) Math.toRadians(this.getYRot() + 90)));
            iceshard.setOwner(this.getCaster());
            iceshard.damageMultiplier = this.damageMultiplier;

            this.level().addFreshEntity(iceshard);
        }
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(2 * 2, 5);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }
}
