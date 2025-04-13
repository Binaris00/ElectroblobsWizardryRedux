package com.electroblob.wizardry.api.content.hell;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.core.registry.SpellRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

// TODO ADD MAGIC MISSILE WHEN PLAYER INIT
public class BinWizardDataInternal {
    public Set<Spell> spellsDiscovered = new HashSet<>();

    public CompoundTag serializeNBT() {
        CompoundTag playerNbt = new CompoundTag();
        ListTag spellsDiscoveredTag = new ListTag();

        spellsDiscovered.forEach((spell -> {
            spellsDiscoveredTag.add(StringTag.valueOf(spell.getLocation().toString()));
        }));
        playerNbt.put("spellsDiscovered", spellsDiscoveredTag);

        return playerNbt;
    }

    public void deserializeNBT(CompoundTag tag) {
        BinWizardDataInternal wizardData = new BinWizardDataInternal();
        wizardData.spellsDiscovered = new HashSet<>();

        if(tag.contains("spellsDiscovered", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("spellsDiscovered", Tag.TAG_STRING);
            for (Tag element : listTag) {
                ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
                if(location != null) {

                    wizardData.spellsDiscovered.add(SpellRegistry.get(location));
                }
            }
        }
    }
}
