package com.electroblob.wizardry.api;

import com.electroblob.wizardry.api.content.data.ISpellVar;
import com.electroblob.wizardry.api.content.data.IStoredSpellVar;
import com.electroblob.wizardry.api.content.enchantment.Imbuement;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.ImbuementLoader;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerWizardData {
    public Set<Spell> spellsDiscovered = new HashSet<>();
    public Spell castCommandSpell = Spells.NONE;
    public int castCommandTick;
    public SpellModifiers castCommandModifiers = new SpellModifiers();
    public int castCommandDuration;
    public final Set<UUID> allies = new HashSet<>();
    /** <b> Do not use this for any other purpose than displaying the names! </b> */
    public Set<String> allyNames = new HashSet<>();
    public final List<ImbuementLoader> imbuementLoaders = new ArrayList<>();
    @SuppressWarnings("rawtypes") public final Map<ISpellVar, Object> spellData = new HashMap<>();
    @SuppressWarnings("rawtypes") public static final Set<IStoredSpellVar> storedVariables = new HashSet<>();
    public SpellModifiers itemModifiers = new SpellModifiers();
    private SpellTier maxTierReached = SpellTiers.NOVICE;

    public PlayerWizardData(){
        spellsDiscovered.add(Spells.MAGIC_MISSILE);
    }

    // ===========================================
    // Utils
    // Save-check-use methods related to player data
    // ===========================================

    private void update(Player player){
        Services.WIZARD_DATA.onWizardDataUpdate(this, player);
    }

    public <T> void setVariable(ISpellVar<? super T> variable, T value) {
        this.spellData.put(variable, value);
    }

    public void setTierReached(SpellTier tier) {
        if (!hasReachedTier(tier)) this.maxTierReached = tier;
    }

    public boolean hasReachedTier(SpellTier tier) {
        return tier.level >= maxTierReached.level;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getVariable(ISpellVar<T> variable) {
        return (T) spellData.get(variable);
    }

    public Map<ISpellVar, Object> getSpellData() {
        return spellData;
    }

    public static void registerStoredVariables(IStoredSpellVar<?>... variables) {
        storedVariables.addAll(Arrays.asList(variables));
    }

    public static Set<?> getSyncedVariables() {
        return storedVariables.stream().filter(ISpellVar::isSynced).collect(Collectors.toSet());
    }

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

        Services.WIZARD_DATA.onWizardDataUpdate(this, player);
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

        update(player);
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


        Services.WIZARD_DATA.onWizardDataUpdate(this, player);
    }

    public boolean toggleAlly(Player original, Player friend){
        update(original);
        if(this.isPlayerAlly(original, friend)){
            this.allies.remove(friend.getUUID());
            this.allyNames.remove(friend.getDisplayName().getString());
            return false;
        }else{
            this.allies.add(friend.getUUID());
            this.allyNames.add(friend.getDisplayName().getString());
            return true;
        }
    }

    public boolean isPlayerAlly(@Nullable Player original, Player player){
        return this.allies.contains(player.getUUID()) || (original != null && original.getTeam() != null &&
                original.getTeam().getPlayers().contains(player.getDisplayName().getString()));
    }

    public boolean isPlayerAlly(@Nullable Player original, UUID playerUUID){
        if (this.allies.contains(playerUUID)) return true;
        if (original == null || original.getTeam() == null) return false;
        return original.getTeam().getPlayers().stream().anyMatch(allyNames::contains);
    }

    /** Returns whether this player is currently casting a continuous spell via commands. */
    public boolean isCommandCasting(){
        return this.castCommandSpell != null && this.castCommandSpell != Spells.NONE;
    }

    public Spell currentlyCasting(){
        return castCommandSpell;
    }


    /**
     * Sets the duration of a given Imbuement on a ItemStack. This adds a
     * new ImbuementLoader to the list of imbuementLoaders and a new tag to
     * the ItemStack for saving the UUID of the ImbuementLoader.
     *
     * @param player the Player
     * @param stack the ItemStack to modify
     * @param enchantment the Imbuement to add
     * @param duration the duration of the Imbuement in ticks
     *
     * @throws IllegalArgumentException if the given enchantment is not an Imbuement
     */
    public void setImbuementDuration(Player player, ItemStack stack, Enchantment enchantment, int duration) {
        if (enchantment instanceof Imbuement) {
            ImbuementLoader loader = new ImbuementLoader(stack.getItem(), enchantment, duration);
            stack.getOrCreateTag().putString(ImbuementLoader.getTagName(enchantment), loader.getUuid());
            imbuementLoaders.add(loader);
            EBLogger.info("Set imbuement duration: " + enchantment.getDescriptionId() + " -> " + duration + " ticks on item " + stack.getItem().getDescriptionId());
            update(player);
            return;
        }

        throw new IllegalArgumentException("Attempted to set an imbuement duration for something that isn't an Imbuement!");
    }


    /**
     * Gets the duration of the first Imbuement found in the list of ImbuementLoaders that matches the given Enchantment.
     * If no matching Imbuement is found, returns 0. <br>
     * If you want to get the duration of a specific item, use {@link #getImbuementDuration(ItemStack, Enchantment)}
     *
     * @param enchantment the Enchantment to search for
     * @return the duration of the Imbuement in ticks, or 0 if no matching Imbuement is found
     */
    public int getGeneralImbuementDuration(Enchantment enchantment) {
        EBLogger.info("Getting imbuement duration for " + enchantment.getDescriptionId());
        for(ImbuementLoader loader : imbuementLoaders){
            if (loader.getImbuement().equals(enchantment)) {
                EBLogger.info("Found imbuement duration for " + enchantment.getDescriptionId() + " -> " + loader.getTimeLimit());
                return loader.getTimeLimit();
            }
        }

        EBLogger.info("No imbuement duration found for " + enchantment.getDescriptionId());
        return 0;
    }


    /**
     * Gets the duration of the Imbuement on the ItemStack.
     * If no matching Imbuement is found, returns 0.
     *
     * @param stack the ItemStack to check for the Imbuement
     * @param enchantment the Enchantment
     * @return the duration of the Imbuement in ticks, or 0 if no matching Imbuement is found
     */
    public int getImbuementDuration(ItemStack stack, Enchantment enchantment) {
        EBLogger.info("Getting imbuement duration for " + enchantment.getDescriptionId());
        for(ImbuementLoader loader : imbuementLoaders){
            if (loader.getItem().equals(stack.getItem()) && loader.getImbuement().equals(enchantment)) {
                EBLogger.info("Found imbuement duration for " + enchantment.getDescriptionId() + " -> " + loader.getTimeLimit());
                return loader.getTimeLimit();
            }
        }
        return 0;
    }


    /**
     * Removes the Imbuement from the given ItemStack.
     * If the Imbuement is found and removed, this method will also remove the corresponding ImbuementLoader
     * from the list of ImbuementLoaders.
     * If the Imbuement is not found, this method will do nothing and return false.
     *
     * @param stack the ItemStack to remove the Imbuement from
     * @param enchantment the Enchantment to remove
     * @return true if the Imbuement was found and removed, false otherwise
     */
    public boolean removeImbuement(ItemStack stack, Enchantment enchantment){
        Iterator<ImbuementLoader> iterator = imbuementLoaders.iterator();
        while(iterator.hasNext()){
            ImbuementLoader loader = iterator.next();

            if(stack.getOrCreateTag().getString(ImbuementLoader.getTagName(enchantment)).equals(loader.getUuid())){
                stack.getOrCreateTag().remove(ImbuementLoader.getTagName(enchantment));
                InventoryUtil.removeEnchant(stack, loader.getImbuement());
                if(loader.getImbuement() instanceof Imbuement imbuement) imbuement.onImbuementRemoval(stack);
                iterator.remove();

                return true;
            }
        }

        return false;
    }



    /**
     * <b>Internal use only.</b> <br><br>
     * Updates the list of imbued items for the player, reducing the time limits
     * of each ImbuementLoader. If an imbued item's time limit has been reached,
     * the imbuement is removed from the item and the loader is removed from the list.
     *
     * @param player the Player whose imbued items are being updated
     */
    private void updateImbuedItems(Player player) {
        Iterator<ImbuementLoader> iterator = imbuementLoaders.iterator();
        while(iterator.hasNext()) {
            ImbuementLoader loader = iterator.next();
            boolean result = loader.hasReachedLimit();
            if(result){
                removeImbuement(player, loader);
                iterator.remove();
            }
        }
        update(player);
    }


    /**
     * <b>Internal use only.</b> <br><br>
     * This method is called when an Imbuement's time limit has been reached.
     * It goes through the player's inventory, armor, and offhand items, and
     * removes the Imbuement from the first item it finds that matches the given
     * ImbuementLoader. It then calls the {@link Imbuement#onImbuementRemoval(ItemStack)}
     * on that item.
     *
     * @param player the Player whose inventory is being checked
     * @param loader the ImbuementLoader to remove from the player's inventory
     */
    private void removeImbuement(Player player, ImbuementLoader loader) {
        update(player);

        for (ItemStack stack : player.getInventory().items) {
            if (loader.isValid(stack)) {
                InventoryUtil.removeEnchant(stack, loader.getImbuement());
                stack.getOrCreateTag().remove(ImbuementLoader.getTagName(loader.getImbuement()));
                if(loader.getImbuement() instanceof Imbuement imbuement) imbuement.onImbuementRemoval(stack);
                return;
            }
        }
        for (ItemStack stack : player.getInventory().armor) {
            if (loader.isValid(stack)) {
                InventoryUtil.removeEnchant(stack, loader.getImbuement());
                stack.getOrCreateTag().remove(ImbuementLoader.getTagName(loader.getImbuement()));
                if(loader.getImbuement() instanceof Imbuement imbuement) imbuement.onImbuementRemoval(stack);
                return;
            }
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (loader.isValid(stack)){
                InventoryUtil.removeEnchant(stack, loader.getImbuement());
                stack.getOrCreateTag().remove(ImbuementLoader.getTagName(loader.getImbuement()));
                if(loader.getImbuement() instanceof Imbuement imbuement) imbuement.onImbuementRemoval(stack);
                return;
            }
        }
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
        tag.putString("maxTier", maxTierReached.getLocation().toString());

        ListTag alliesTag = new ListTag();
        allies.forEach(uuid -> alliesTag.add(StringTag.valueOf(uuid.toString())));
        tag.put("alliesUUID", alliesTag);

        ListTag allyNamesTag = new ListTag();
        allyNames.forEach(name -> allyNamesTag.add(StringTag.valueOf(name)));
        tag.put("allyNames", allyNamesTag);

        ListTag imbuedItemsTag = new ListTag();
        for(ImbuementLoader loader : imbuementLoaders) {
            imbuedItemsTag.add(loader.serializeNbt(new CompoundTag()));
        }

        tag.put("imbuedItems", imbuedItemsTag);
        tag.put("itemModifiers", itemModifiers.toNBT());

        storedVariables.forEach(k -> k.write(tag, this.spellData.get(k)));
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
                    wizardData.spellsDiscovered.add(Services.REGISTRY_UTIL.getSpell(location));
                }
            }
        }

        if(tag.contains("castCommandSpell", Tag.TAG_STRING)) {
            ResourceLocation location = ResourceLocation.tryParse(tag.getString("castCommandSpell"));
            if(location != null) {
                wizardData.castCommandSpell = Services.REGISTRY_UTIL.getSpell(location);
            }
        }

        wizardData.castCommandDuration = tag.getInt("castCommandDuration");
        wizardData.castCommandTick = tag.getInt("castCommandTick");

        String tierKey = tag.getString("maxTier");
        SpellTier tier = Services.REGISTRY_UTIL.getTier(ResourceLocation.tryParse(tierKey));
        wizardData.maxTierReached = tier != null ? tier : SpellTiers.NOVICE;

        if(tag.contains("castCommandModifiers", Tag.TAG_COMPOUND)) wizardData.castCommandModifiers = SpellModifiers.fromNBT(tag.getCompound("castCommandModifiers"));

        if(tag.contains("alliesUUID", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("alliesUUID", Tag.TAG_STRING);
            for (Tag element : listTag) {
                wizardData.allies.add(UUID.fromString(element.getAsString()));
            }
        }

        if(tag.contains("allyNames", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("allyNames", Tag.TAG_STRING);
            for (Tag element : listTag) {
                wizardData.allyNames.add(element.getAsString());
            }
        }

        if(tag.contains("imbuedItems", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("imbuedItems", Tag.TAG_COMPOUND);
            for (Tag element : listTag) {
                wizardData.imbuementLoaders.add(ImbuementLoader.deserializeNbt((CompoundTag) element));
            }
        }

        if(tag.contains("itemModifiers", Tag.TAG_COMPOUND))
            wizardData.itemModifiers = SpellModifiers.fromNBT(tag.getCompound("itemModifiers"));

        try {
            storedVariables.forEach(k -> wizardData.spellData.put(k, k.read(tag)));
        } catch (ClassCastException e) {
            EBLogger.error("Wizard data NBT tag was not of expected type!", e);
        }

        return wizardData;
    }

    public static void onUpdate(EBLivingTick event) {
        if(!(event.getEntity() instanceof Player player)) return;
        PlayerWizardData wizardData = Services.WIZARD_DATA.getWizardData(player, player.level());

        wizardData.updateContinuousSpellCasting(player);
        wizardData.updateImbuedItems(player);

        wizardData.getSpellData().forEach((k, v) -> wizardData.getSpellData().put(k, k.update(player, v)));
        wizardData.getSpellData().keySet().removeIf(k -> k.canPurge(player, wizardData.getSpellData().get(k)));

        Services.WIZARD_DATA.onWizardDataUpdate(wizardData, player);
    }
}
