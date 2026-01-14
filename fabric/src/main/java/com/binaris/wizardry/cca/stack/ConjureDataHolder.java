package com.binaris.wizardry.cca.stack;

import com.binaris.wizardry.api.content.data.ConjureData;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.world.item.ItemStack;

/**
 * Here for loading/storing conjure item data. CCA components rely on NBT storage, so this could be easily synced and
 * persisted without any problems. This is also why there's no fields here; all data is stored in the ItemStack's NBT.
 */
public class ConjureDataHolder extends ItemComponent implements ConjureData {
    public ConjureDataHolder(ItemStack stack) {
        super(stack);
    }

    @Override
    public long getExpireTime() {
        if (!this.hasTag("expire_time", NbtType.LONG)) this.putLong("expire_time", -1L);
        return this.getLong("expire_time");
    }

    @Override
    public void setExpireTime(long expireTime) {
        this.putLong("expire_time", expireTime);
    }

    @Override
    public int getDuration() {
        if (!this.hasTag("duration", NbtType.INT)) this.putInt("duration", 0);
        return this.getInt("duration");
    }

    @Override
    public void setDuration(int duration) {
        this.putInt("duration", duration);
    }

    @Override
    public boolean isSummoned() {
        if (!this.hasTag("is_summoned", NbtType.BYTE)) this.putBoolean("is_summoned", false);
        return this.getBoolean("is_summoned");
    }

    @Override
    public void setSummoned(boolean summoned) {
        this.putBoolean("is_summoned", summoned);
    }
}
