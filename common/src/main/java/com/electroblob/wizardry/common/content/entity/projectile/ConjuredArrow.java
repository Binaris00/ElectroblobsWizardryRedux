package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.setup.registries.EBEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConjuredArrow extends AbstractArrow {

    public ConjuredArrow(EntityType<ConjuredArrow> entityType, Level world) {
        super(entityType, world);
    }

    public ConjuredArrow(double x, double y, double z, Level level) {
        super(EBEntities.CONJURED_ARROW.get(), x, y, z, level);
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
