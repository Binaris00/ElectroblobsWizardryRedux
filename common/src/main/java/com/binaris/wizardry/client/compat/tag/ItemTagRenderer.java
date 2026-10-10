package com.binaris.wizardry.client.compat.tag;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;


// ItemStackRenderer
public class ItemTagRenderer implements IIngredientRenderer<ItemTag> {
    @Override
    public void render(GuiGraphics guiGraphics, ItemTag itemTag) {
        ItemStack[] items = Ingredient.of(itemTag.tag()).getItems();
        for (ItemStack item : items) {
            render(guiGraphics, item, itemTag, 0, 0);
        }
    }

    public void render(GuiGraphics guiGraphics, ItemStack ingredient, ItemTag tag, int posX, int posY) {
        RenderSystem.enableDepthTest();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = this.getFontRenderer(minecraft, tag);
        guiGraphics.renderFakeItem(ingredient, posX, posY);
        guiGraphics.renderItemDecorations(font, ingredient, posX, posY);
        RenderSystem.disableBlend();
    }

    @Override
    public @NotNull List<Component> getTooltip(ItemTag itemTag, @NotNull TooltipFlag tooltipFlag) {
        ItemStack[] items = Ingredient.of(itemTag.tag()).getItems();
        for (ItemStack item : items) {
            return getTooltip(item, tooltipFlag);
        }
        return List.of();
    }

    public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        return ingredient.getTooltipLines(player, tooltipFlag);
    }
}
