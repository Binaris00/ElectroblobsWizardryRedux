package com.binaris.wizardry.cca.player;

import com.binaris.wizardry.api.EBLogger;
import com.binaris.wizardry.api.content.data.ISpellVar;
import com.binaris.wizardry.api.content.data.IStoredSpellVar;
import com.binaris.wizardry.api.content.data.SpellManagerData;
import com.binaris.wizardry.api.content.spell.NoneSpell;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.cca.EBComponents;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpellManagerDataHolder implements SpellManagerData, ComponentV3, AutoSyncedComponent {
    @SuppressWarnings("rawtypes")
    public static final Set<IStoredSpellVar> storedVariables = new HashSet<>();
    @SuppressWarnings("rawtypes")
    public final Map<ISpellVar, Object> spellData = new HashMap<>();
    private final Player provider;
    public Set<Spell> spellsDiscovered = new HashSet<>();

    public SpellManagerDataHolder(Player provider) {
        this.provider = provider;
        spellsDiscovered.add(Spells.NONE);
        spellsDiscovered.add(Spells.MAGIC_MISSILE);
    }

    @Override
    public void sync() {
        EBComponents.SPELL_MANAGER_DATA.sync(provider);
    }

    @Override
    public <T> T getVariable(ISpellVar<T> var) {
        return (T) spellData.get(var);
    }

    @Override
    public <T> void setVariable(ISpellVar<? super T> variable, T value) {
        this.spellData.put(variable, value);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<ISpellVar, Object> getSpellData() {
        return spellData;
    }

    @Override
    public boolean hasSpellBeenDiscovered(Spell spell) {
        return spellsDiscovered.contains(spell) || spell instanceof NoneSpell;
    }

    @Override
    public boolean discoverSpell(Spell spell) {
        if (spell instanceof NoneSpell) return false;
        boolean result = spellsDiscovered.add(spell);
        if (result) sync();
        return result;
    }

    @Override
    public boolean undiscoverSpell(Spell spell) {
        boolean result = spellsDiscovered.remove(spell);
        if (result) sync();
        return result;
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        spellsDiscovered.clear();
        if (tag.contains("spellsDiscovered", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("spellsDiscovered", Tag.TAG_STRING);
            for (Tag element : listTag) {
                ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
                if (location != null) {
                    spellsDiscovered.add(Services.REGISTRY_UTIL.getSpell(location));
                }
            }
        }

        try {
            storedVariables.forEach(k -> spellData.put(k, k.read(tag)));
        } catch (ClassCastException e) {
            EBLogger.error("Wizard data NBT tag was not of expected type!", e);
        }
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        ListTag spellsDiscoveredTag = new ListTag();
        spellsDiscovered.forEach((spell -> spellsDiscoveredTag.add(StringTag.valueOf(spell.getLocation().toString()))));
        tag.put("spellsDiscovered", spellsDiscoveredTag);
        storedVariables.forEach(k -> k.write(tag, this.spellData.get(k)));
    }
}
