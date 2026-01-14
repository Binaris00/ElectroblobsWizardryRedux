package com.binaris.wizardry.content.spell.necromancy;

import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SummonWitherSkeleton extends MinionSpell<WitherSkeleton> {
    public SummonWitherSkeleton() {
        super((l) -> new WitherSkeleton(EntityType.WITHER_SKELETON, l));
    }

    @Override
    protected void addMinionExtras(WitherSkeleton minion, CastContext ctx, int alreadySpawned) {
        minion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
        minion.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }
}
