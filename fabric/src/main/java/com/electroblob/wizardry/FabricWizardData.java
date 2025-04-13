package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.hell.BinWizardDataInternal;
import com.electroblob.wizardry.registry.SpellRegistryFabric;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class FabricWizardData extends SavedData {
    HashMap<UUID, BinWizardDataInternal> players = new HashMap<>();

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        CompoundTag playersNbt = new CompoundTag();
        players.forEach(((uuid, wizardData) -> {
            CompoundTag playerNbt = new CompoundTag();
            ListTag spellsDiscoveredTag = new ListTag();
            wizardData.spellsDiscovered.forEach((spell -> {
                spellsDiscoveredTag.add(StringTag.valueOf(spell.getLocation().toString()));
            }));
            playerNbt.put("spellsDiscovered", spellsDiscoveredTag);
            playersNbt.put(uuid.toString(), playerNbt);
        }));

        tag.put("players", playersNbt);
        return tag;
    }

    public static FabricWizardData createFromNbt(CompoundTag tag) {
        FabricWizardData data = new FabricWizardData();

        CompoundTag playersNbt = tag.getCompound("players");
        for (String keyUUID : playersNbt.getAllKeys()) {
            BinWizardDataInternal wizardData = new BinWizardDataInternal();
            wizardData.spellsDiscovered = new HashSet<>();

            CompoundTag playerNbt = playersNbt.getCompound(keyUUID);
            if(playerNbt.contains("spellsDiscovered", Tag.TAG_LIST)) {
                ListTag listTag = playerNbt.getList("spellsDiscovered", Tag.TAG_STRING);
                for (Tag element : listTag) {
                    ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
                    if(location != null) {
                        wizardData.spellsDiscovered.add(SpellRegistryFabric.get().get(location));
                    }
                }
            }

            UUID playerUUID = UUID.fromString(keyUUID);
            data.players.put(playerUUID, wizardData);
        }

        return data;
    }

    /**
     * This function gets the 'PersistentStateManager' and creates or returns the filled in 'StateSaveAndLoader'.
     * It does this by calling 'StateSaveAndLoader::createFromNbt' passing it the previously saved 'NbtCompound' we wrote in 'writeNbt'.
     */
    public static FabricWizardData getServerState(MinecraftServer server) {
        DimensionDataStorage persistentStateManager = server.getLevel(Level.OVERWORLD).getDataStorage();

        FabricWizardData state = persistentStateManager.computeIfAbsent(
                FabricWizardData::createFromNbt,
                FabricWizardData::new,
                WizardryMainMod.MOD_ID
        );

        state.setDirty();

        return state;
    }

    public static BinWizardDataInternal getPlayerState(LivingEntity player) {
        FabricWizardData serverState = getServerState(player.level().getServer());
        return serverState.players.computeIfAbsent(player.getUUID(), uuid -> new BinWizardDataInternal());
    }

}
