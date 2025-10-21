// language: java
// File: fabric/src/main/java/com/electroblob/wizardry/cca/FabricConjureItemDataHolder.java
package com.electroblob.wizardry.cca;

import com.electroblob.wizardry.api.ConjureItemData;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ConjureItemDataHolder extends ItemComponent implements ConjureItemComponent {

    private static final String KEY = "conjure_item_data";

    private ConjureItemData conjureItemData;
    private final ItemStack provider;

    public ConjureItemDataHolder(ItemStack stack) {
        super(stack);
        this.provider = stack;
        // lazy init only
    }

    @Override
    public ConjureItemData getConjureItemData() {
        if (this.conjureItemData == null) {
            CompoundTag tag = this.getCompound(KEY);
            if (tag.isEmpty()) {
                this.conjureItemData = new ConjureItemData(provider);
            } else {
                this.conjureItemData = new ConjureItemData(provider).deserializeNBT(tag);
            }
        }
        return this.conjureItemData;
    }

    /**
     * Persist the current ConjureItemData into the component's NBT on the stack.
     * Call after any mutation.
     */
    public void saveConjureItemData() {
        if (this.conjureItemData == null) return;
        CompoundTag tag = new CompoundTag();
        this.conjureItemData.serializeNBT(tag);
        this.putCompound(KEY, tag);
    }

    @Override
    public void onTagInvalidated() {
        super.onTagInvalidated();
        this.conjureItemData = null;
    }
}
