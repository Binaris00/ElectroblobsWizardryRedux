package com.electroblob.wizardry.api;

import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ConjureItemData {
    private static final HashSet<Item> APPLY_ITEMS = new HashSet<>();
    private ItemStack stack;
    private int lifetime = -1;
    private int maxLifetime = -1;
    private boolean summoned = false;

    public ConjureItemData(ItemStack stack) {
        this.stack = stack;
    }

    public void tick(){
        if(!summoned) return;

        EBLogger.info("Ticking summoned item: " + stack.getHoverName().getString() + " with lifetime " + this.getLifetime());

        if (this.getLifetime() <= 0) {
            this.stack.shrink(1);
            this.summoned = false;
            Services.WIZARD_DATA.onConjureItemDataUpdate(this, stack);
            return;
        }

        stack.setDamageValue(stack.getDamageValue() + 1);
        this.setLifetime(getLifetime() -1);
    }

    public static boolean isSummonedItem(ItemStack stack) {
        return stack != ItemStack.EMPTY && Services.WIZARD_DATA.getConjureItemData(stack) != null && Services.WIZARD_DATA.getConjureItemData(stack).summoned;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public int getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(int maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public boolean isSummoned() {
        return summoned;
    }

    public void summoned(boolean summoned) {
        this.summoned = summoned;
    }

    public static boolean applyItem(Item item) {
        return APPLY_ITEMS.contains(item);
    }

    public static void addApplyItem(Item item) {
        APPLY_ITEMS.add(item);
    }

    public void serializeNBT(@NotNull CompoundTag tag) {
        tag.putInt("lifetime", this.lifetime);
        tag.putBoolean("summoned", this.summoned);
        tag.putInt("maxLifetime", this.maxLifetime);
    }

    public ConjureItemData deserializeNBT(@NotNull CompoundTag tag) {
        this.lifetime = tag.getInt("lifetime");
        this.summoned = tag.getBoolean("summoned");
        this.maxLifetime = tag.getInt("maxLifetime");
        return this;
    }

    public static void onPlayerTickEvent(EBLivingTick event) {
        if(event.getLevel().isClientSide) return;
        if(!(event.getEntity() instanceof Player player)) return;

        player.getInventory().offhand.stream().filter(ConjureItemData::isSummonedItem)
                .forEach(ConjureItemData::checkStack);

        player.getInventory().items.stream().filter(ConjureItemData::isSummonedItem)
                .forEach(ConjureItemData::checkStack);

        player.getInventory().armor.stream().filter(ConjureItemData::isSummonedItem)
                .forEach(ConjureItemData::checkStack);
    }

    private static void checkStack(ItemStack stack){
        ConjureItemData data = Services.WIZARD_DATA.getConjureItemData(stack);
        if(data != null) data.tick();
    }
}
