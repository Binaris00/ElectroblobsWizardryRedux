package com.electroblob.wizardry.common.content.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConjuredArrow extends AbstractArrow {

    public ConjuredArrow(EntityType<ConjuredArrow> entityConjuredArrowEntityType, Level world) {
        super(entityConjuredArrowEntityType, world);
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
