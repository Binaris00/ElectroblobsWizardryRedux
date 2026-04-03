package com.binaris.wizardry.integration.curios;

import com.binaris.wizardry.api.content.item.ArtifactItem;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CuriosArtifactItem extends ArtifactItem implements ICurioItem {
    public CuriosArtifactItem(Rarity rarity, @Nullable IArtifactEffect effect) {
        super(rarity, effect);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
    }
}
