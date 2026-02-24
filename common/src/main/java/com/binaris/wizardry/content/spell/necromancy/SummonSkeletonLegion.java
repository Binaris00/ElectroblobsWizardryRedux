package com.binaris.wizardry.content.spell.necromancy;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.InteractionHand;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonSkeletonLegion extends MinionSpell<AbstractSkeleton> {
    public SummonSkeletonLegion() {
        super((level -> new Skeleton(EntityType.SKELETON, level)));
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
        if (alreadySpawned % 2 == 0) {
            // Archers
            minion.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
        } else {
            // Swordsmen
            minion.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        }

        minion.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
        minion.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
        minion.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        minion.setDropChance(EquipmentSlot.OFFHAND, 0.0f);
        minion.setDropChance(EquipmentSlot.HEAD, 0.0f);
        minion.setDropChance(EquipmentSlot.CHEST, 0.0f);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.NECROMANCY, SpellType.MINION, SpellAction.SUMMON, 100, 20, 400)
                .add(DefaultProperties.MINION_LIFETIME, 1200)
                .add(DefaultProperties.MINION_COUNT, 6)
                .add(DefaultProperties.SUMMON_RADIUS, 3)
                .build();
    }
}
