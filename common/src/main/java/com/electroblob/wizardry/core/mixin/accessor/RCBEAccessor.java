package com.electroblob.wizardry.core.mixin.accessor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RandomizableContainerBlockEntity.class)
public interface RCBEAccessor {
    @Accessor
    ResourceLocation getLootTable();

    @Accessor
    void setLootTable(ResourceLocation lootTable);
}
