package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.data.ISpellVar;
import com.electroblob.wizardry.api.content.data.IStoredSpellVar;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.network.PlayerCapabilitySyncPacketS2C;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class SpellManagerDataHolder implements INBTSerializable<CompoundTag>, SpellManagerData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("spell_manager_data");
    public static final Capability<SpellManagerDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public Set<Spell> spellsDiscovered = new HashSet<>();
    @SuppressWarnings("rawtypes") public final Map<ISpellVar, Object> spellData = new HashMap<>();
    @SuppressWarnings("rawtypes") public static final Set<IStoredSpellVar> storedVariables = new HashSet<>();

    private final Player provider;

    public SpellManagerDataHolder(Player player) {
        this.provider = player;
        spellsDiscovered.add(Spells.NONE);
        spellsDiscovered.add(Spells.MAGIC_MISSILE);
    }

    @Override
    public void sync(){
        if (!this.provider.level().isClientSide()) {
            CompoundTag tag = this.serializeNBT();

            PlayerCapabilitySyncPacketS2C packet = new PlayerCapabilitySyncPacketS2C(PlayerCapabilitySyncPacketS2C.CapabilityType.SPELL_MANAGER, tag);
            Services.NETWORK_HELPER.sendTo((ServerPlayer) this.provider, packet);
        }
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
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag spellsDiscoveredTag = new ListTag();
        spellsDiscovered.forEach((spell -> spellsDiscoveredTag.add(StringTag.valueOf(spell.getLocation().toString()))));
        tag.put("spellsDiscovered", spellsDiscoveredTag);
        storedVariables.forEach(k -> k.write(tag, this.spellData.get(k)));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
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

    public void copyFrom(@NotNull SpellManagerDataHolder old) {
        this.spellsDiscovered.clear();
        this.spellsDiscovered.addAll(old.spellsDiscovered);

        this.spellData.clear();
        this.spellData.putAll(old.spellData);
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<SpellManagerDataHolder> dataHolder;

        public Provider(Player player) {
            this.dataHolder = LazyOptional.of(() -> new SpellManagerDataHolder(player));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return SpellManagerDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return dataHolder.orElseThrow(NullPointerException::new).serializeNBT();
        }
        @Override
        public void deserializeNBT(CompoundTag arg) {
            dataHolder.orElseThrow(NullPointerException::new).deserializeNBT(arg);
        }
    }
}
