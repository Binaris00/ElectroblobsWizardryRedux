package com.electroblob.wizardry.api;

import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.core.registry.SpellRegistry;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerWizardData {
    public Set<Spell> spellsDiscovered = new HashSet<>();

    private Spell castCommandSpell = Spells.NONE;
    private int castCommandTick;
    private SpellModifiers castCommandModifiers = new SpellModifiers();
    private int castCommandDuration;

    public PlayerWizardData(){
        spellsDiscovered.add(Spells.MAGIC_MISSILE);
    }

    // ===========================================
    // Utils
    // Save-check-use methods related to player data
    // ===========================================

    /** Checks if the player has discovered the given spell, or if it's a NoneSpell */
    public boolean hasSpellBeenDiscovered(Spell spell){
        return spellsDiscovered.contains(spell) || spell instanceof NoneSpell;
    }

    /** Add the spell to the list of discovered spells, returns false if it was already present */
    public boolean discoverSpell(Spell spell){
        if(spell instanceof NoneSpell) return false;
        return spellsDiscovered.add(spell);
    }

    /** Starts casting the given spell with the given modifiers. */
    public void startCastingContinuousSpell(Player player, Spell spell, SpellModifiers modifiers, int duration){
        this.castCommandSpell = spell;
        this.castCommandModifiers = modifiers;
        this.castCommandDuration = duration;

        if(!player.level().isClientSide){
            // TODO PACKET
//            PacketCastContinuousSpell.Message message = new PacketCastContinuousSpell.Message(this.player, spell, modifiers, duration);
//            WizardryPacketHandler.net.sendToDimension(message, this.player.world.provider.getDimension());
        }

        Services.WIZARD_DATA.onUpdate(this, player);
    }

    /** Stops casting the current spell. */
    public void stopCastingContinuousSpell(Player player){
        this.castCommandSpell = Spells.NONE;
        this.castCommandTick = 0;
        this.castCommandModifiers.reset();

        if(!player.level().isClientSide){
            // TODO PACKET
//            PacketCastContinuousSpell.Message message = new PacketCastContinuousSpell.Message(this.player, Spells.none, this.castCommandModifiers, this.castCommandDuration);
//            WizardryPacketHandler.net.sendToDimension(message, this.player.world.provider.getDimension());
        }

        Services.WIZARD_DATA.onUpdate(this, player);
    }

    /** Casts the current continuous spell, fires relevant events and updates the castCommandTick field. */
    public void updateContinuousSpellCasting(Player player){
        if((this.castCommandSpell == null || this.castCommandSpell instanceof NoneSpell) || this.castCommandSpell.isInstantCast()){
            this.castCommandTick = 0;
        }

        if(castCommandTick >= castCommandDuration){
            this.stopCastingContinuousSpell(player);
            return;
        }
        //player.sendSystemMessage(Component.literal("Tick " + castCommandTick));

        if(WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(SpellCastEvent.Source.COMMAND, castCommandSpell, player, castCommandModifiers, castCommandTick))){
            this.stopCastingContinuousSpell(player);
            return;
        }

        if(this.castCommandSpell.cast(new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, this.castCommandTick, this.castCommandModifiers))
                && this.castCommandTick == 0){
            //player.sendSystemMessage(Component.literal("Finish"));
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.COMMAND, castCommandSpell, player, castCommandModifiers));
        }

        castCommandTick++;


        Services.WIZARD_DATA.onUpdate(this, player);
    }

    /** Returns whether this player is currently casting a continuous spell via commands. */
    public boolean isCommandCasting(){
        return this.castCommandSpell != null && this.castCommandSpell != Spells.NONE;
    }

    public Spell currentlyCasting(){
        return castCommandSpell;
    }


    // ===========================================
    // Compound Tags
    // Just save the data in a compound tag, used in loaders to abstract the way it's saved
    // ===========================================

    /** Returns a CompoundTag containing the player's wizard data */
    public CompoundTag serializeNBT(CompoundTag tag) {
        ListTag spellsDiscoveredTag = new ListTag();

        spellsDiscovered.forEach((spell -> spellsDiscoveredTag.add(StringTag.valueOf(spell.getLocation().toString()))));
        tag.put("spellsDiscovered", spellsDiscoveredTag);
        tag.put("castCommandSpell", StringTag.valueOf(castCommandSpell.getLocation().toString()));
        tag.putInt("castCommandDuration", castCommandDuration);
        tag.putInt("castCommandTick", castCommandTick);
        tag.put("castCommandModifiers", castCommandModifiers.toNBT());

        return tag;
    }

    /** Deserializes a CompoundTag containing the player's wizard data */
    public PlayerWizardData deserializeNBT(CompoundTag tag) {
        PlayerWizardData wizardData = new PlayerWizardData();

        if(tag.contains("spellsDiscovered", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("spellsDiscovered", Tag.TAG_STRING);
            for (Tag element : listTag) {
                ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
                if(location != null) {
                    wizardData.spellsDiscovered.add(SpellRegistry.get(location));
                }
            }
        }

        if(tag.contains("castCommandSpell", Tag.TAG_STRING)) {
            ResourceLocation location = ResourceLocation.tryParse(tag.getString("castCommandSpell"));
            if(location != null) {
                wizardData.castCommandSpell = SpellRegistry.get(location);
            }
        }


        wizardData.castCommandDuration = tag.getInt("castCommandDuration");
        wizardData.castCommandTick = tag.getInt("castCommandTick");

        if(tag.contains("castCommandModifiers", Tag.TAG_COMPOUND)) {
            wizardData.castCommandModifiers = SpellModifiers.fromNBT(tag.getCompound("castCommandModifiers"));
        }

        return wizardData;
    }
}
