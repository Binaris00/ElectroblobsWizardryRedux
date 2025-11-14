package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.core.mixin.accessor.ItemPropertiesAccessor;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class EBItemProperties {
    private EBItemProperties() {
    }

    public static void register() {
        pullingItem(EBItems.FLAMECATCHER.get());
        conjureItem(EBItems.FLAMECATCHER.get());

        conjureItem(EBItems.FROST_AXE.get());
        conjureItem(EBItems.FLAMING_AXE.get());

        pullingItem(EBItems.SPECTRAL_BOW.get());
        conjureItem(EBItems.SPECTRAL_BOW.get());
    }

    private static void pullingItem(Item item) {
        ItemPropertiesAccessor.callRegister(item, new ResourceLocation("pull"), (stack, clientLevel, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
            }
        });

        ItemPropertiesAccessor.callRegister(item, new ResourceLocation("pulling"), (stack, clientLevel, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);

    }

    private static void conjureItem(Item item) {
        ItemPropertiesAccessor.callRegister(item, new ResourceLocation("conjure"), (stack, clientLevel, entity, seed) -> {
            ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
            if (data != null && data.isSummoned()) {
                int frames = 8;
                int damage = data.getRemainingLifetime(clientLevel.getGameTime());
                return damage < frames ? (float) damage / frames : (float) (stack.getMaxDamage() - damage) / frames;
            }
            return 0.0F;
        });

        ItemPropertiesAccessor.callRegister(item, new ResourceLocation("conjuring"), (stack, clientLevel, entity, seed) -> {
            ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
            if (data != null && data.isSummoned()) {
                int frames = 8;
                int damage = data.getRemainingLifetime(clientLevel.getGameTime());
                return damage < frames || damage > stack.getMaxDamage() - frames ? 1.0F : 0.0F;
            }
            return 0.0F;
        });
    }


}
