package com.binaris.wizardry.platform;

import com.binaris.wizardry.api.content.data.*;
import com.binaris.wizardry.cca.EBComponents;
import com.binaris.wizardry.cca.player.SpellManagerDataHolder;
import com.binaris.wizardry.content.spell.abstr.ConjureItemSpell;
import com.binaris.wizardry.core.platform.services.IObjectData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Arrays;

public class FabricObjectData implements IObjectData {
    @Override
    public WizardData getWizardData(Player player) {
        return player.getComponent(EBComponents.WIZARD_DATA);
    }

    @Override
    public MinionData getMinionData(Mob mob) {
        return EBComponents.MINION_DATA.get(mob);
    }

    @Override
    public ContainmentData getContainmentData(LivingEntity entity) {
        return EBComponents.CONTAINMENT_DATA.get(entity);
    }

    @Override
    public boolean isMinion(Entity entity) {
        return entity instanceof Mob mob && EBComponents.MINION_DATA.get(mob).isSummoned();
    }

    @Override
    public @Nullable ConjureData getConjureData(ItemStack stack) {
        if (!ConjureItemSpell.isSummonableItem(stack.getItem())) return null;
        return EBComponents.CONJURE.get(stack);
    }

    @Override
    public ImbuementEnchantData getImbuementData(ItemStack stack) {
        return EBComponents.IMBUEMENT_ENCHANTS.get(stack);
    }

    @Override
    public CastCommandData getCastCommandData(Player player) {
        return EBComponents.CAST_COMMAND_DATA.get(player);
    }

    @Override
    public SpellManagerData getSpellManagerData(Player player) {
        return EBComponents.SPELL_MANAGER_DATA.get(player);
    }

    @Override
    public void spellStoredVariables(IStoredSpellVar<?>... variables) {
        SpellManagerDataHolder.storedVariables.addAll(Arrays.asList(variables));
    }

    @Override
    public ArcaneLockData getArcaneLockData(BlockEntity blockEntity) {
        return EBComponents.ARCANE_LOCK_DATA.get(blockEntity);
    }
}
