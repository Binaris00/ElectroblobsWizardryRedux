package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.content.data.*;
import com.electroblob.wizardry.capabilities.*;
import com.electroblob.wizardry.core.platform.services.IObjectData;
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
        return stack.getCapability(ConjureDataHolder.INSTANCE).orElse(null);
    }

    @Override
    public ImbuementEnchantData getImbuementData(ItemStack stack) {
        return stack.getCapability(ImbuementEnchantDataHolder.INSTANCE).orElse(null);
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
    public ContainmentData getContainmentData(LivingEntity entity) {
        return entity.getCapability(ContainmentDataHolder.INSTANCE).orElseThrow(
                () -> new IllegalStateException("ContainmentData capability not present on entity " + entity)
        );
    }

    @Override
    public boolean isMinion(Entity mob) {
        if (!(mob instanceof Mob)) return false;
        if (!mob.getCapability(MinionDataHolder.INSTANCE).isPresent()) return false;

        MinionDataHolder minionData = mob.getCapability(MinionDataHolder.INSTANCE).orElseThrow(
                () -> new IllegalStateException("MinionData capability not present on mob " + mob)
        );
        return minionData.isSummoned();
    }

    @Override
    public ArcaneLockData getArcaneLockData(BlockEntity blockEntity) {
        return blockEntity.getCapability(ArcaneLockDataHolder.INSTANCE).orElse(null);
    }
}
