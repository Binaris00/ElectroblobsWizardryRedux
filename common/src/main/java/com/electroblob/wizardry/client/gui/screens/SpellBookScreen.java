package com.electroblob.wizardry.client.gui.screens;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.client.gui.screens.abstr.SpellInfoScreen;
import com.electroblob.wizardry.content.item.SpellBookItem;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SpellBookScreen extends SpellInfoScreen {
    private final SpellBookItem book;
    private final Spell spell;

    public SpellBookScreen(ItemStack stack) {
        super(288, 180, Component.literal(""));
        if (!(stack.getItem() instanceof SpellBookItem)) {
            throw new ClassCastException("Cannot create spell book GUI for item that does not extend ItemSpellBook!");
        }
        this.book = (SpellBookItem) stack.getItem();
        Spell spell = SpellUtil.getSpell(stack);
        if (spell == Spells.NONE) {
            spell = Spells.MAGIC_MISSILE;
            EBLogger.warn("SpellBookItem has no spell assigned to it! Defaulting to Magic Missile, where did this item come from?");
        }
        this.spell = spell;
    }

    @Override
    public Spell getSpell() {
        return spell;
    }

    @Override
    public ResourceLocation getTexture() {
        return book.getGuiTexture(spell);
    }
}
