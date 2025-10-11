package com.electroblob.wizardry.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class EBClientConstants {
    public static final int PROGRESSION_BAR_WIDTH = 131;
    public static final int PROGRESSION_BAR_HEIGHT = 3;
    public static final Font FONT = Minecraft.getInstance().font;

    public static final ResourceLocation ARCANE_WORKBENCH_CONTAINER_TEXTURE = WizardryMainMod.location("textures/gui/container/arcane_workbench.png");
    public static final ResourceLocation ARCANE_WORKBENCH_EMPTY_SLOT_CRYSTAL = new ResourceLocation("item/empty_slot_crystal");
    public static final ResourceLocation ARCANE_WORKBENCH_EMPTY_SLOT_UPGRADE = new ResourceLocation("item/empty_slot_upgrade");

    public static final int TOOLTIP_WIDTH = 144;
    public static final int TOOLTIP_BORDER = 6;
    public static final int LINE_SPACING_WIDE = 5;
    public static final int LINE_SPACING_NARROW = 1;
    public static final int MAIN_GUI_WIDTH = 176;
    public static final int RUNE_LEFT = 38;
    public static final int RUNE_TOP = 22;
    public static final int RUNE_WIDTH = 100;
    public static final int RUNE_HEIGHT = 100;
    public static final int SCROLL_BAR_LEFT = 102;
    public static final int SCROLL_BAR_TOP = 34;
    public static final int SCROLL_BAR_WIDTH = 12;
    public static final int SCROLL_BAR_HEIGHT = 178;
    public static final int SCROLL_HANDLE_HEIGHT = 15;
    public static final int HALO_DIAMETER = 156;
    public static final int TEXTURE_WIDTH = 512;
    public static final int TEXTURE_HEIGHT = 512;
    public static final int ANIMATION_DURATION = 20;
    public static final int SEARCH_TOOLTIP_HOVER_TIME = 20;
    public static final Style TOOLTIP_SYNTAX = Style.EMPTY.withColor(ChatFormatting.YELLOW);
    public static final Style TOOLTIP_BODY = Style.EMPTY.withColor(ChatFormatting.WHITE);
}
