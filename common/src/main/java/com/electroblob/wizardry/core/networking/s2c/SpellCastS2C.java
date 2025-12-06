package com.electroblob.wizardry.core.networking.s2c;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.networking.ClientMessageHandler;
import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public class SpellCastS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("spell_cast");

    int casterID;
    InteractionHand hand;
    Spell spell;
    SpellModifiers modifiers;

    public SpellCastS2C(int casterID, InteractionHand hand, Spell spell, SpellModifiers modifiers) {
        this.casterID = casterID;
        this.hand = hand;
        this.spell = spell;
        this.modifiers = modifiers;
    }

    public SpellCastS2C(FriendlyByteBuf pBuf) {
        this.casterID = pBuf.readInt();
        this.hand = pBuf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        this.spell = Services.REGISTRY_UTIL.getSpell(pBuf.readResourceLocation());
        this.modifiers = SpellModifiers.fromNBT(pBuf.readNbt());
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
        pBuf.writeInt(casterID);
        pBuf.writeBoolean(hand == InteractionHand.MAIN_HAND);
        pBuf.writeResourceLocation(spell.getLocation());
        pBuf.writeNbt(modifiers.toNBT());
    }

    @Override
    public void handleClient() {
        ClientMessageHandler.spellCast(this);
    }

    public int getCasterID() {
        return casterID;
    }

    public SpellModifiers getModifiers() {
        return modifiers;
    }

    public Spell getSpell() {
        return spell;
    }

    public InteractionHand getHand() {
        return hand;
    }
}
