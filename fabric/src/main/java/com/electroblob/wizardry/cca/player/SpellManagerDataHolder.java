package com.electroblob.wizardry.cca.player;


import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.data.ISpellVar;
import com.electroblob.wizardry.api.content.data.IStoredSpellVar;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.enchantment.Imbuement;
import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.ImbuementLoader;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.api.content.util.TemporaryEnchantmentLoader;
import com.electroblob.wizardry.cca.EBComponents;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.Spells;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SpellManagerDataHolder implements SpellManagerData, ComponentV3, AutoSyncedComponent {
    public Set<Spell> spellsDiscovered = new HashSet<>();
    public final List<ImbuementLoader> imbuementLoaders = new ArrayList<>();
    public final List<TemporaryEnchantmentLoader> temporaryEnchantmentLoaders = new ArrayList<>();
    @SuppressWarnings("rawtypes") public final Map<ISpellVar, Object> spellData = new HashMap<>();
    @SuppressWarnings("rawtypes") public static final Set<IStoredSpellVar> storedVariables = new HashSet<>();

    private final Player provider;

    public SpellManagerDataHolder(Player provider) {
        this.provider = provider;
        spellsDiscovered.add(Spells.NONE);
    }

    @Override
    public void sync(){
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

    // ===== NEW TEMPORARY ENCHANTMENT SYSTEM =====

    @Override
    public void setTemporaryEnchantment(ItemStack stack, Enchantment enchantment, int level, int duration) {
        TemporaryEnchantmentLoader loader = new TemporaryEnchantmentLoader(stack.getItem(), enchantment, level, duration);
        stack.getOrCreateTag().putString(TemporaryEnchantmentLoader.getTagName(enchantment), loader.getUuid());
        
        // Add the enchantment to the item
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(enchantment, level);
        EnchantmentHelper.setEnchantments(enchantments, stack);
        
        temporaryEnchantmentLoaders.add(loader);
        EBLogger.info("Set temporary enchantment: " + enchantment.getDescriptionId() + " level " + level + " -> " + duration + " ticks on item " + stack.getItem().getDescriptionId());
        sync();
    }

    @Override
    public int getTemporaryEnchantmentDuration(ItemStack stack, Enchantment enchantment) {
        EBLogger.info("Getting temporary enchantment duration for " + enchantment.getDescriptionId());
        for (TemporaryEnchantmentLoader loader : temporaryEnchantmentLoaders) {
            if (loader.getItem().equals(stack.getItem()) && loader.getEnchantment().equals(enchantment)) {
                EBLogger.info("Found temporary enchantment duration for " + enchantment.getDescriptionId() + " -> " + loader.getTimeLimit());
                return loader.getTimeLimit();
            }
        }
        return 0;
    }

    @Override
    public List<TemporaryEnchantmentLoader> getTemporaryEnchantmentLoaders() {
        return temporaryEnchantmentLoaders;
    }

    @Override
    public boolean removeTemporaryEnchantment(ItemStack stack, Enchantment enchantment) {
        Iterator<TemporaryEnchantmentLoader> iterator = temporaryEnchantmentLoaders.iterator();
        while (iterator.hasNext()) {
            TemporaryEnchantmentLoader loader = iterator.next();

            if (stack.getOrCreateTag().getString(TemporaryEnchantmentLoader.getTagName(enchantment)).equals(loader.getUuid())) {
                stack.getOrCreateTag().remove(TemporaryEnchantmentLoader.getTagName(enchantment));
                InventoryUtil.removeEnchant(stack, loader.getEnchantment());
                iterator.remove();
                sync();
                return true;
            }
        }

        return false;
    }

    @Override
    public void removeTemporaryEnchantment(TemporaryEnchantmentLoader loader) {
        sync();

        for (ItemStack stack : provider.getInventory().items) {
            if (loader.isValid(stack)) {
                InventoryUtil.removeEnchant(stack, loader.getEnchantment());
                stack.getOrCreateTag().remove(TemporaryEnchantmentLoader.getTagName(loader.getEnchantment()));
                return;
            }
        }
        for (ItemStack stack : provider.getInventory().armor) {
            if (loader.isValid(stack)) {
                InventoryUtil.removeEnchant(stack, loader.getEnchantment());
                stack.getOrCreateTag().remove(TemporaryEnchantmentLoader.getTagName(loader.getEnchantment()));
                return;
            }
        }
        for (ItemStack stack : provider.getInventory().offhand) {
            if (loader.isValid(stack)) {
                InventoryUtil.removeEnchant(stack, loader.getEnchantment());
                stack.getOrCreateTag().remove(TemporaryEnchantmentLoader.getTagName(loader.getEnchantment()));
                return;
            }
        }
        sync();
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

        imbuementLoaders.clear();
        if (tag.contains("imbuedItems", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("imbuedItems", Tag.TAG_COMPOUND);
            for (Tag element : listTag) {
                imbuementLoaders.add(ImbuementLoader.deserializeNbt((CompoundTag) element));
            }
        }

        // Deserialize temporary enchantments
        temporaryEnchantmentLoaders.clear();
        if (tag.contains("temporaryEnchantments", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("temporaryEnchantments", Tag.TAG_COMPOUND);
            for (Tag element : listTag) {
                temporaryEnchantmentLoaders.add(TemporaryEnchantmentLoader.deserializeNbt((CompoundTag) element));
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

        ListTag imbuedItemsTag = new ListTag();
        for (ImbuementLoader loader : imbuementLoaders) {
            imbuedItemsTag.add(loader.serializeNbt(new CompoundTag()));
        }
        tag.put("imbuedItems", imbuedItemsTag);
        
        // Serialize temporary enchantments
        ListTag tempEnchantsTag = new ListTag();
        for (TemporaryEnchantmentLoader loader : temporaryEnchantmentLoaders) {
            tempEnchantsTag.add(loader.serializeNbt(new CompoundTag()));
        }
        tag.put("temporaryEnchantments", tempEnchantsTag);
        
        storedVariables.forEach(k -> k.write(tag, this.spellData.get(k)));
    }
}
