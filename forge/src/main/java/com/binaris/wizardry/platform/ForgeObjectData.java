package com.binaris.wizardry.platform;

import com.binaris.wizardry.api.content.data.*;
import com.binaris.wizardry.capabilities.*;
import com.binaris.wizardry.core.platform.services.IObjectData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Arrays;

public class ForgeObjectData implements IObjectData {
    @Override
    public ConjureData getConjureData(ItemStack stack) {
        return stack.getCapability(ConjureDataHolder.INSTANCE)
                .orElse(new ConjureDataHolder(stack));
    }

    @Override
    public ImbuementEnchantData getImbuementData(ItemStack stack) {
        return stack.getCapability(ImbuementEnchantDataHolder.INSTANCE)
                .orElse(new ImbuementEnchantDataHolder());
    }

    @Override
    public CastCommandData getCastCommandData(Player player) {
        return player.getCapability(CastCommandDataHolder.INSTANCE)
                .orElse(new CastCommandDataHolder(player));
    }

    @Override
    public SpellManagerData getSpellManagerData(Player player) {
        return player.getCapability(SpellManagerDataHolder.INSTANCE)
                .orElse(new SpellManagerDataHolder(player));
    }

    @Override
    public WizardData getWizardData(Player player) {
        return player.getCapability(WizardDataHolder.INSTANCE)
                .orElse(new WizardDataHolder(player));
    }

    @Override
    public MinionData getMinionData(Mob mob) {
        return mob.getCapability(MinionDataHolder.INSTANCE)
                .orElse(new MinionDataHolder(mob));
    }

    @Override
    public ContainmentData getContainmentData(LivingEntity entity) {
        return entity.getCapability(ContainmentDataHolder.INSTANCE)
                .orElse(new ContainmentDataHolder(entity));
    }

    @Override
    public ArcaneLockData getArcaneLockData(BlockEntity blockEntity) {
        return blockEntity.getCapability(ArcaneLockDataHolder.INSTANCE)
                .orElse(new ArcaneLockDataHolder(blockEntity));
    }

    @Override
    public void spellStoredVariables(IStoredSpellVar<?>... variables) {
        SpellManagerDataHolder.storedVariables.addAll(Arrays.asList(variables));
    }

    @Override
    public boolean isMinion(Entity mob) {
        if (!(mob instanceof Mob)) return false;
        if (!mob.getCapability(MinionDataHolder.INSTANCE).isPresent()) return false;
        return getMinionData((Mob) mob).isSummoned();
    }
}
