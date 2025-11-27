package com.electroblob.wizardry.content;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * Abstract base class for all spell forfeits. A forfeit is a negative effect that is applied to the caster when they
 * use a spell scroll or wand and the casts fail. Each forfeit has an associated sound and message that can be
 * displayed to the player...
 */
public class Forfeit {
    protected final SoundEvent sound;
    private final ResourceLocation name;
    private final Element element;
    private final SpellTier spellTier;
    private @Nullable BiConsumer<Level, Player> effect;

    @Deprecated
    public Forfeit(ResourceLocation name) {
        this.name = name;
        this.sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(name.getNamespace(), "forfeit." + name.getPath()));
        element = Elements.SORCERY;
        spellTier = SpellTiers.ADVANCED;
    }

    public Forfeit(ResourceLocation name, Element element, SpellTier spellTier, @Nullable BiConsumer<Level, Player> effect) {
        this.name = name;
        this.sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(name.getNamespace(), "forfeit." + name.getPath()));
        this.element = element;
        this.spellTier = spellTier;
        this.effect = effect;
    }

    public Forfeit(String name, Element element, SpellTier spellTier, @Nullable BiConsumer<Level, Player> effect) {
        this(WizardryMainMod.location(name), element, spellTier, effect);
    }

    public void apply(Level world, Player player) {

    }

    public Component getMessage(Component implementName) {
        return Component.translatable("forfeit." + name.getNamespace() + "." + name.getPath(), implementName);
    }

    public Component getMessageForWand() {
        return getMessage(Component.translatable("item." + WizardryMainMod.MOD_ID + ".wand.generic"));
    }

    public Component getMessageForScroll() {
        return getMessage(Component.translatable("item." + WizardryMainMod.MOD_ID + ".scroll.generic"));
    }

    public SoundEvent getSound() {
        return sound;
    }

    public Element getElement() {
        return element;
    }

    public SpellTier getSpellTier() {
        return spellTier;
    }
}
