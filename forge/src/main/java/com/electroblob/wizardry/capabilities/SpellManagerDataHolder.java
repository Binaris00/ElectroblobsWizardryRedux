package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.data.ISpellVar;
import com.electroblob.wizardry.api.content.data.IStoredSpellVar;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.enchantment.Imbuement;
import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.ImbuementLoader;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.network.PlayerCapabilitySyncPacketS2C;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class SpellManagerDataHolder implements INBTSerializable<CompoundTag>, SpellManagerData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("spell_manager_data");
    public static final Capability<SpellManagerDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public Set<Spell> spellsDiscovered = new HashSet<>();
    public final List<ImbuementLoader> imbuementLoaders = new ArrayList<>();
    @SuppressWarnings("rawtypes") public final Map<ISpellVar, Object> spellData = new HashMap<>();
    @SuppressWarnings("rawtypes") public static final Set<IStoredSpellVar> storedVariables = new HashSet<>();

    private final Player provider;

    public SpellManagerDataHolder(Player player) {
        this.provider = player;
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
        return spellsDiscovered.add(spell);
    }


    @Override
    public void setImbuementDuration(ItemStack stack, Enchantment enchantment, int duration) {
        if (enchantment instanceof Imbuement) {
            ImbuementLoader loader = new ImbuementLoader(stack.getItem(), enchantment, duration);
            stack.getOrCreateTag().putString(ImbuementLoader.getTagName(enchantment), loader.getUuid());
            imbuementLoaders.add(loader);
            EBLogger.info("Set imbuement duration: " + enchantment.getDescriptionId() + " -> " + duration + " ticks on item " + stack.getItem().getDescriptionId());
            sync();
            return;
        }

        throw new IllegalArgumentException("Attempted to set an imbuement duration for something that isn't an Imbuement!");
    }

    @Override
    public int getImbuementDuration(ItemStack stack, Enchantment enchantment) {
        EBLogger.info("Getting imbuement duration for " + enchantment.getDescriptionId());
        for (ImbuementLoader loader : imbuementLoaders) {
            if (loader.getItem().equals(stack.getItem()) && loader.getImbuement().equals(enchantment)) {
                EBLogger.info("Found imbuement duration for " + enchantment.getDescriptionId() + " -> " + loader.getTimeLimit());
                return loader.getTimeLimit();
            }
        }
        return 0;
    }

    @Override
    public int getGeneralImbuementDuration(Enchantment enchantment) {
        EBLogger.info("Getting imbuement duration for " + enchantment.getDescriptionId());
        for (ImbuementLoader loader : imbuementLoaders) {
            if (loader.getImbuement().equals(enchantment)) {
                EBLogger.info("Found imbuement duration for " + enchantment.getDescriptionId() + " -> " + loader.getTimeLimit());
                return loader.getTimeLimit();
            }
        }

        EBLogger.info("No imbuement duration found for " + enchantment.getDescriptionId());
        return 0;
    }

    @Override
    public List<ImbuementLoader> getImbuementLoaders() {
        return imbuementLoaders;
    }

    @Override
    public boolean removeImbuement(ItemStack stack, Enchantment enchantment) {
        Iterator<ImbuementLoader> iterator = imbuementLoaders.iterator();
        while (iterator.hasNext()) {
            ImbuementLoader loader = iterator.next();

            if (stack.getOrCreateTag().getString(ImbuementLoader.getTagName(enchantment)).equals(loader.getUuid())) {
                stack.getOrCreateTag().remove(ImbuementLoader.getTagName(enchantment));
                InventoryUtil.removeEnchant(stack, loader.getImbuement());
                if (loader.getImbuement() instanceof Imbuement imbuement) imbuement.onImbuementRemoval(stack);
                iterator.remove();
                sync();
                return true;
            }
        }

        return false;
    }

    @Override
    public void removeImbuement(ImbuementLoader loader) {
        sync();

        for (ItemStack stack : provider.getInventory().items) {
            if (loader.isValid(stack)) {
                InventoryUtil.removeEnchant(stack, loader.getImbuement());
                stack.getOrCreateTag().remove(ImbuementLoader.getTagName(loader.getImbuement()));
                if (loader.getImbuement() instanceof Imbuement imbuement) imbuement.onImbuementRemoval(stack);
                return;
            }
        }
        for (ItemStack stack : provider.getInventory().armor) {
            if (loader.isValid(stack)) {
                InventoryUtil.removeEnchant(stack, loader.getImbuement());
                stack.getOrCreateTag().remove(ImbuementLoader.getTagName(loader.getImbuement()));
                if (loader.getImbuement() instanceof Imbuement imbuement) imbuement.onImbuementRemoval(stack);
                return;
            }
        }
        for (ItemStack stack : provider.getInventory().offhand) {
            if (loader.isValid(stack)) {
                InventoryUtil.removeEnchant(stack, loader.getImbuement());
                stack.getOrCreateTag().remove(ImbuementLoader.getTagName(loader.getImbuement()));
                if (loader.getImbuement() instanceof Imbuement imbuement) imbuement.onImbuementRemoval(stack);
                return;
            }
        }
        sync();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag spellsDiscoveredTag = new ListTag();
        spellsDiscovered.forEach((spell -> spellsDiscoveredTag.add(StringTag.valueOf(spell.getLocation().toString()))));
        tag.put("spellsDiscovered", spellsDiscoveredTag);

        ListTag imbuedItemsTag = new ListTag();
        for (ImbuementLoader loader : imbuementLoaders) {
            imbuedItemsTag.add(loader.serializeNbt(new CompoundTag()));
        }
        tag.put("imbuedItems", imbuedItemsTag);
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

        imbuementLoaders.clear();
        if (tag.contains("imbuedItems", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("imbuedItems", Tag.TAG_COMPOUND);
            for (Tag element : listTag) {
                imbuementLoaders.add(ImbuementLoader.deserializeNbt((CompoundTag) element));
            }
        }

        try {
            storedVariables.forEach(k -> spellData.put(k, k.read(tag)));
        } catch (ClassCastException e) {
            EBLogger.error("Wizard data NBT tag was not of expected type!", e);
        }
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
