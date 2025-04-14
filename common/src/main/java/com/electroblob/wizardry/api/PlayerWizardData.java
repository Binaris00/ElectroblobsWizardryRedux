package com.electroblob.wizardry.api;

import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.core.registry.SpellRegistry;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class PlayerWizardData {
    public Set<Spell> spellsDiscovered = new HashSet<>();

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


    // ===========================================
    // Compound Tags
    // Just save the data in a compound tag, used in loaders to abstract the way it's saved
    // ===========================================

    /** Returns a CompoundTag containing the player's wizard data */
    public CompoundTag serializeNBT(CompoundTag tag) {
        ListTag spellsDiscoveredTag = new ListTag();

        spellsDiscovered.forEach((spell -> spellsDiscoveredTag.add(StringTag.valueOf(spell.getLocation().toString()))));
        tag.put("spellsDiscovered", spellsDiscoveredTag);

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
        return wizardData;
    }
}
