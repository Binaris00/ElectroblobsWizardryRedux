package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.content.spell.abstr.MinionSpell;
import com.electroblob.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SummonSkeleton extends MinionSpell<AbstractSkeleton> {
    public SummonSkeleton() {
        super((l) -> new Skeleton(EntityType.SKELETON, l));
    }

    @Override
    protected AbstractSkeleton createMinion(Level world, @Nullable LivingEntity caster, SpellModifiers modifiers) {
        if (caster instanceof Player player && EBAccessoriesIntegration.isEquipped(player, EBItems.CHARM_MINION_VARIANTS.get())) {
            return new Stray(EntityType.STRAY, world);
        } else {
            return super.createMinion(world, caster, modifiers);
        }
    }

    @Override
    protected void addMinionExtras(AbstractSkeleton minion, CastContext ctx, int alreadySpawned) {
        minion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        minion.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }
}
