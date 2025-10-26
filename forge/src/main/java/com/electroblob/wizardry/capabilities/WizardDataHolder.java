package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.WizardData;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class WizardDataHolder implements INBTSerializable<CompoundTag>, WizardData {
    private static final ResourceLocation LOCATION = WizardryMainMod.location("wizard_data");
    public static final Capability<WizardDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public final Set<UUID> allies = new HashSet<>();
    public Set<String> allyNames = new HashSet<>();
    public SpellModifiers itemModifiers = new SpellModifiers();
    private SpellTier maxTierReached = SpellTiers.NOVICE;

    private final Player provider;

    public WizardDataHolder(Player player) {
        this.provider = player;
    }

    private void sync(){
        // TODO THX FORGE FOR MAKING THE CAPABILITY SYSTEM SO HARD FOR ME
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
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("maxTier", maxTierReached.getLocation().toString());

        ListTag alliesTag = new ListTag();
        allies.forEach(uuid -> alliesTag.add(StringTag.valueOf(uuid.toString())));
        tag.put("alliesUUID", alliesTag);

        ListTag allyNamesTag = new ListTag();
        allyNames.forEach(name -> allyNamesTag.add(StringTag.valueOf(name)));
        tag.put("allyNames", allyNamesTag);

        tag.put("itemModifiers", itemModifiers.toNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
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
    }

    @Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<WizardDataHolder> dataHolder;

        public Provider(Player player) {
            this.dataHolder = LazyOptional.of(() -> new WizardDataHolder(player));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return WizardDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return dataHolder.orElseThrow(NullPointerException::new).serializeNBT();
        }
        @Override
        public void deserializeNBT(CompoundTag arg) {
            dataHolder.orElseThrow(NullPointerException::new).deserializeNBT(arg);
        }

        @SubscribeEvent
        public static void attachCapability(final AttachCapabilitiesEvent<Entity> event) {
            if(event.getObject() instanceof Player player){
                event.addCapability(LOCATION, new WizardDataHolder.Provider(player));
            }
        }
    }
}
