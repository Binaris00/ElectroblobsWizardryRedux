package com.electroblob.wizardry.core.networking.s2c;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.content.item.ScrollItem;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

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
    public void handleClient(Minecraft minecraft, Player player) {
        Level level = minecraft.level;
        Entity caster = level.getEntity(casterID);
        if (caster instanceof Player playerCaster) {

            spell.cast(new PlayerCastContext(level, playerCaster, hand, 0, modifiers));

            SpellCastEvent.Source source = SpellCastEvent.Source.OTHER;
            Item item = playerCaster.getItemInHand(hand).getItem();

            if (item instanceof WandItem) {
                source = SpellCastEvent.Source.WAND;
            } else if (item instanceof ScrollItem) {
                source = SpellCastEvent.Source.SCROLL;
            }

            EBLogger.warn("[SpellCast] Casting spell on client side: " + spell.getDescriptionId() + " from " + source.name());

            // No need to check if the spell succeeded, because the packet is only ever sent when it succeeds.
            // The handler for this event now deals with discovery.
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(source, spell, playerCaster, modifiers));
        } else {
            EBLogger.warn("Received a PacketCastSpell, but the caster ID was not the ID of a player");
        }
    }
}
