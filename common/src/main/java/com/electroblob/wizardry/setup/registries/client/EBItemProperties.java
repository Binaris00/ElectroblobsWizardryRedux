package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.core.mixin.ItemPropertiesAccessor;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.resources.ResourceLocation;

public final class EBItemProperties {
    private EBItemProperties(){
    }

    public static void register(){
        ItemPropertiesAccessor.callRegister(EBItems.FLAMECATCHER.get(), new ResourceLocation("pull"), (stack, clientLevel, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
            }
        });

        ItemPropertiesAccessor.callRegister(EBItems.FLAMECATCHER.get(), new ResourceLocation("pulling"), (stack, clientLevel, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        });

        ItemPropertiesAccessor.callRegister(EBItems.FLAMECATCHER.get(), new ResourceLocation("conjure"), (stack, clientLevel, entity, seed) -> {
            if (stack.getOrCreateTag().contains("lifetime")) {
                int frames = 8;
                int damage = stack.getOrCreateTag().getInt("lifetime");
                return damage < frames ? (float) damage / frames : (float) (stack.getMaxDamage() - damage) / frames;
            }

            return 0.0F;
        });

        ItemPropertiesAccessor.callRegister(EBItems.FLAMECATCHER.get(), new ResourceLocation("conjuring"), (stack, clientLevel, entity, seed) -> {
            if (stack.getOrCreateTag().contains("lifetime")) {
                int frames = 8;
                int damage = stack.getOrCreateTag().getInt("lifetime");
                return damage < frames || damage > stack.getMaxDamage() - frames ? 1.0F : 0.0F;
            }

            return 0.0F;
        });
    }


}
