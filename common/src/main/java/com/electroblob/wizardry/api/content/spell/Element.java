package com.electroblob.wizardry.api.content.spell;

import com.electroblob.wizardry.WizardryMainMod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

// TODO: Need improvements
public class Element {
    private final ChatFormatting color;
    private final ResourceLocation location;
    private final ResourceLocation icon;

    public Element(ChatFormatting color, String name){
        this(color, name, WizardryMainMod.MOD_ID);
    }

    Element(ChatFormatting colour, String name, String modid){
        this.color = colour;
        this.location = new ResourceLocation(modid, name);
        this.icon = new ResourceLocation(modid, "textures/gui/container/element_icon_" + name + ".png");
    }

    public Component getDisplayName(){
        return Component.translatable("element." + getLocation());
    }

    public ChatFormatting getColor(){
        return color;
    }

    public Component getWizardName(){
        return Component.translatable("element." + getLocation() + ".wizard");
    }

    public ResourceLocation getIcon(){
        return icon;
    }

    public @NotNull ResourceLocation getLocation() {
        return location;
    }
}
