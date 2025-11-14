package com.electroblob.wizardry.content.entity.projectile;

import com.electroblob.wizardry.setup.registries.EBEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConjuredArrowEntity extends AbstractArrow {

    public ConjuredArrowEntity(EntityType<ConjuredArrowEntity> entityType, Level world) {
        super(entityType, world);
    }

    public ConjuredArrowEntity(double x, double y, double z, Level level) {
        super(EBEntities.CONJURED_ARROW.get(), x, y, z, level);
    }

    public ConjuredArrowEntity(EntityType<? extends AbstractArrow> type, LivingEntity entity, Level level) {
        super(type, entity, level);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 400) {
            this.discard();
        }
        super.tick();
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
