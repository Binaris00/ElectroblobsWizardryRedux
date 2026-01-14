package com.binaris.wizardry.core.mixin.accessor;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemProperties.class)
public interface ItemPropertiesAccessor {
    @Invoker
    static void callRegister(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
        //throw new UnsupportedOperationException();
    }
}
