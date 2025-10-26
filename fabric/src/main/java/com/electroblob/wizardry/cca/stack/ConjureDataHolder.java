package com.electroblob.wizardry.cca.stack;

import com.electroblob.wizardry.api.content.data.ConjureData;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Here for loading/storing conjure item data. CCA components rely on NBT storage, so this could be easily synced and
 * persisted without any problems. This is also why there's no fields here; all data is stored in the ItemStack's NBT.
 */
public class ConjureDataHolder extends ItemComponent implements ConjureData {
    public ConjureDataHolder(ItemStack stack) {
        super(stack);
        init();
    }

    @Override
    public void tick() {
        if (!isSummoned()) return;

        if (this.getLifetime() <= 0) {
            this.stack.shrink(1);
            this.setSummoned(false);
            return;
        }

        lifetimeDecrement();
    }

    private void init() {
        if (!this.hasTag("lifetime")) this.putInt("lifetime", -1);
        if (!this.hasTag("max_lifetime")) this.putInt("max_lifetime", -1);
        if (!this.hasTag("is_summoned")) this.putBoolean("is_summoned", false);
    }

    @Override
    public void lifetimeDecrement() {
        int lifetime = getLifetime();
        if (lifetime > 0) this.putInt("lifetime", lifetime - 1);
    }

    @Override
    public int getLifetime() {
        return this.getInt("lifetime");
    }

    @Override
    public void setLifetime(int lifetime) {
        this.putInt("lifetime", lifetime);
    }

    @Override
    public int getMaxLifetime() {
        return this.getInt("max_lifetime");
    }

    @Override
    public void setMaxLifetime(int maxLifetime) {
        this.putInt("max_lifetime", maxLifetime);
    }

    @Override
    public boolean isSummoned() {
        return this.getBoolean("is_summoned");
    }

    @Override
    public void setSummoned(boolean summoned) {
        this.putBoolean("is_summoned", summoned);
    }
}
