package com.electroblob.wizardry.cca.player;

import com.electroblob.wizardry.api.content.data.WizardData;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.cca.EBComponents;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.google.common.collect.EvictingQueue;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class WizardDataHolder implements WizardData, ComponentV3, AutoSyncedComponent {
    private final Player provider;

    public final Set<UUID> allies = new HashSet<>();
    public Set<String> allyNames = new HashSet<>();
    public SpellModifiers itemModifiers = new SpellModifiers();
    private SpellTier maxTierReached = SpellTiers.NOVICE;
    private Queue<AbstractMap.SimpleEntry<Spell, Long>> recentSpells = EvictingQueue.create(EBConfig.MAX_RECENT_SPELLS);
    private Random random = new Random();
    private @Nullable BlockPos containmentPos = null;

    public WizardDataHolder(Player provider) {
        this.provider = provider;
    }

    private void sync(){
        EBComponents.WIZARD_DATA.sync(provider);
    }

    @Override
    public void setTierReached(SpellTier tier) {
        if (!hasReachedTier(tier)) this.maxTierReached = tier;
        sync();
    }

    @Override
    public boolean hasReachedTier(SpellTier tier) {
        return tier.level >= maxTierReached.level;
    }

    @Override
    public boolean toggleAlly(Player friend) {
        if (this.isPlayerAlly(friend)) {
            this.allies.remove(friend.getUUID());
            this.allyNames.remove(friend.getDisplayName().getString());
            sync();
            return false;
        }

        this.allies.add(friend.getUUID());
        this.allyNames.add(friend.getDisplayName().getString());
        sync();
        return true;

    }

    @Override
    public boolean isPlayerAlly(Player ally) {
        return this.allies.contains(ally.getUUID()) || (provider != null && provider.getTeam() != null &&
                provider.getTeam().getPlayers().contains(ally.getDisplayName().getString()));
    }

    @Override
    public boolean isPlayerAlly(UUID playerUUID) {
        if (this.allies.contains(playerUUID)) return true;
        if (provider == null || provider.getTeam() == null) return false;
        return provider.getTeam().getPlayers().stream().anyMatch(allyNames::contains);
    }

    @Override
    public SpellModifiers getSpellModifiers() {
        return this.itemModifiers;
    }

    @Override
    public void setSpellModifiers(SpellModifiers modifiers) {
        this.itemModifiers = modifiers;
        sync();
    }

    @Override
    public void trackRecentSpell(Spell spell, long timestamp) {
        recentSpells.add(new AbstractMap.SimpleEntry<>(spell, timestamp));
        sync();
    }

    @Override
    public int countRecentCasts(Spell spell) {
        return (int) recentSpells.stream()
                .filter(entry -> entry.getKey().equals(spell))
                .count();
    }

    @Override
    public void removeRecentCasts(Predicate<AbstractMap.SimpleEntry<Spell, Long>> predicate) {
        recentSpells.removeIf(predicate);
        sync();
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public @Nullable BlockPos getContainmentPos() {
        return this.containmentPos;
    }

    @Override
    public void setContainmentPos(BlockPos pos) {
        this.containmentPos = pos;
        sync();
    }


    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        ResourceLocation tierLocation = ResourceLocation.tryParse(tag.getString("maxTier"));
        if (tierLocation != null) {
            SpellTier tier = Services.REGISTRY_UTIL.getTier(tierLocation);
            if (tier != null) {
                this.maxTierReached = tier;
            }
        }

        ListTag alliesTag = tag.getList("alliesUUID", Tag.TAG_STRING);
        this.allies.clear();
        for (int i = 0; i < alliesTag.size(); i++) {
            String uuidString = alliesTag.getString(i);
            try {
                UUID uuid = UUID.fromString(uuidString);
                this.allies.add(uuid);
            } catch (IllegalArgumentException e) {
                // Invalid UUID string, skip it
            }
        }

        ListTag allyNamesTag = tag.getList("allyNames", Tag.TAG_STRING);
        this.allyNames.clear();
        for (int i = 0; i < allyNamesTag.size(); i++) {
            String name = allyNamesTag.getString(i);
            this.allyNames.add(name);
        }

        if (tag.contains("itemModifiers")) {
            this.itemModifiers = SpellModifiers.fromNBT(tag.getCompound("itemModifiers"));
        }


        ListTag recentSpellsTag = tag.getList("recentSpells", Tag.TAG_COMPOUND);
        this.recentSpells.clear();
        for (int i = 0; i < recentSpellsTag.size(); i++) {
            CompoundTag spellEntryTag = recentSpellsTag.getCompound(i);
            ResourceLocation spellLocation = ResourceLocation.tryParse(spellEntryTag.getString("spell"));
            long timestamp = spellEntryTag.getLong("timestamp");
            if (spellLocation != null) {
                Spell spell = Services.REGISTRY_UTIL.getSpell(spellLocation);
                if (spell != null) {
                    this.recentSpells.add(new AbstractMap.SimpleEntry<>(spell, timestamp));
                }
            }
        }

        if (tag.contains("randomSeed")) {
            long seed = tag.getLong("randomSeed");
            this.random = new Random(seed);
        }

        if (tag.contains("containmentPos")) {
            CompoundTag posTag = tag.getCompound("containmentPos");
            int x = posTag.getInt("x");
            int y = posTag.getInt("y");
            int z = posTag.getInt("z");
            this.containmentPos = new BlockPos(x, y, z);
        } else {
            this.containmentPos = null;
        }
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        tag.putString("maxTier", maxTierReached.getOrCreateLocation().toString());

        ListTag alliesTag = new ListTag();
        allies.forEach(uuid -> alliesTag.add(StringTag.valueOf(uuid.toString())));
        tag.put("alliesUUID", alliesTag);

        ListTag allyNamesTag = new ListTag();
        allyNames.forEach(name -> allyNamesTag.add(StringTag.valueOf(name)));
        tag.put("allyNames", allyNamesTag);

        tag.put("itemModifiers", itemModifiers.toNBT());

        ListTag recentSpellsTag = new ListTag();
        for (AbstractMap.SimpleEntry<Spell, Long> entry : recentSpells) {
            CompoundTag spellEntryTag = new CompoundTag();
            spellEntryTag.putString("spell", entry.getKey().getLocation().toString());
            spellEntryTag.putLong("timestamp", entry.getValue());
            recentSpellsTag.add(spellEntryTag);
        }
        tag.put("recentSpells", recentSpellsTag);

        tag.putLong("randomSeed", this.random.nextLong());

        if (containmentPos != null) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", containmentPos.getX());
            posTag.putInt("y", containmentPos.getY());
            posTag.putInt("z", containmentPos.getZ());
            tag.put("containmentPos", posTag);
        }
    }
}
