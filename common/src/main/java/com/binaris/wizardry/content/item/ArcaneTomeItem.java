package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.content.item.ITierValue;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArcaneTomeItem extends Item implements ITierValue {
    public ArcaneTomeItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        SpellTier tier = getTier(stack);
        return switch (tier.getLevel()) {
            case 1 -> Rarity.UNCOMMON;
            case 2 -> Rarity.RARE;
            case 3 -> Rarity.EPIC;
            default -> Rarity.COMMON;
        };
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        SpellTier tier = getTier(stack);
        List<SpellTier> tiers = Services.REGISTRY_UTIL.getTiers().stream().toList();
        int index = tiers.indexOf(tier);

        tooltip.add(tier.getDescriptionFormatted());

        if (index > 0) {
            SpellTier tier2 = tiers.get(index - 1);
            tooltip.add(Component.translatable("item.ebwizardry.arcane_tome.desc",
                    tier2.getDescriptionFormatted().getString(),
                    tier.getDescriptionFormatted().getString()
            ).withStyle(ChatFormatting.GRAY));
        }
    }


    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public SpellTier getTier(ItemStack stack) {
        String tierKey = stack.getOrCreateTag().getString("Tier");
        SpellTier tier = Services.REGISTRY_UTIL.getTier(ResourceLocation.tryParse(tierKey));
        return tier != null ? tier : SpellTiers.NOVICE;
    }
}
