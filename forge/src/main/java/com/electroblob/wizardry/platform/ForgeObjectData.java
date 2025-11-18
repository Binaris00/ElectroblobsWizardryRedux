package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.content.data.*;
import com.electroblob.wizardry.capabilities.MinionDataHolder;
import com.electroblob.wizardry.capabilities.CastCommandDataHolder;
import com.electroblob.wizardry.capabilities.SpellManagerDataHolder;
import com.electroblob.wizardry.capabilities.WizardDataHolder;
import com.electroblob.wizardry.capabilities.ConjureDataHolder;
import com.electroblob.wizardry.capabilities.TemporaryEnchantmentDataHolder;
import com.electroblob.wizardry.core.platform.services.IObjectData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class ForgeObjectData implements IObjectData {
    @Override
    public ConjureData getConjureData(ItemStack stack) {
        return stack.getCapability(ConjureDataHolder.INSTANCE).orElse(null);
    }

    @Override
    public TemporaryEnchantmentData getTemporaryEnchantmentData(ItemStack stack) {
        return stack.getCapability(TemporaryEnchantmentDataHolder.INSTANCE).orElse(null);
    }

    @Override
    public CastCommandData getCastCommandData(Player player) {
        return player.getCapability(CastCommandDataHolder.INSTANCE).orElseThrow(
                () -> new IllegalStateException("CastCommandData capability not present on player " + player)
        );
    }

    @Override
    public SpellManagerData getSpellManagerData(Player player) {
        return player.getCapability(SpellManagerDataHolder.INSTANCE).orElseThrow(
                () -> new IllegalStateException("SpellManagerData capability not present on player " + player)
        );
    }

    @Override
    public void spellStoredVariables(IStoredSpellVar<?>... variables) {
        SpellManagerDataHolder.storedVariables.addAll(Arrays.asList(variables));
    }

    @Override
    public WizardData getWizardData(Player player) {
        return player.getCapability(WizardDataHolder.INSTANCE).orElseThrow(
                () -> new IllegalStateException("WizardData capability not present on player " + player)
        );
    }

    @Override
    public MinionData getMinionData(Mob mob) {
        return mob.getCapability(MinionDataHolder.INSTANCE).orElseThrow(
                () -> new IllegalStateException("MinionData capability not present on mob " + mob)
        );
    }

    @Override
    public boolean isMinion(Entity mob) {
        return mob.getCapability(MinionDataHolder.INSTANCE).isPresent();
    }
}
