package com.electroblob.wizardry.core.networking.s2c;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.entity.living.ISpellCaster;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class NPCSpellCastS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("npc_spell_cast");
    int casterID;
    int targetID;
    InteractionHand hand;
    Spell spell;
    SpellModifiers modifiers;

    public NPCSpellCastS2C(int casterID, int targetID, InteractionHand hand, Spell spell, SpellModifiers modifiers) {
        this.casterID = casterID;
        this.targetID = targetID;
        this.hand = hand;
        this.spell = spell;
        this.modifiers = modifiers;
    }

    public NPCSpellCastS2C(FriendlyByteBuf pBuf) {
        this.casterID = pBuf.readInt();
        this.targetID = pBuf.readInt();
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
        pBuf.writeInt(targetID);
        pBuf.writeBoolean(hand == InteractionHand.MAIN_HAND);
        pBuf.writeResourceLocation(spell.getLocation());
        pBuf.writeNbt(modifiers.toNBT());
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        Level level = minecraft.level;
        Entity caster = level.getEntity(casterID);
        Entity target = targetID == -1 ? null : level.getEntity(targetID);

        // Safety check, the npc cannot be a non-living entity and the target must be a living entity
        if (!(caster instanceof LivingEntity livingCaster) || !(target instanceof LivingEntity livingTarget)) return;

        spell.cast(new EntityCastContext(level, livingCaster, hand, 0, livingTarget, modifiers));
        WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.NPC, spell, livingCaster, modifiers));

        if (caster instanceof ISpellCaster spellCaster) {
            if (!spell.isInstantCast() || spell instanceof NoneSpell) {
                spellCaster.setContinuousSpell(spell);
                spellCaster.setSpellCounter(spell instanceof NoneSpell ? 0 : 1);
            } else {
                spellCaster.setSpellCounter(0);
            }
        }
    }
}
