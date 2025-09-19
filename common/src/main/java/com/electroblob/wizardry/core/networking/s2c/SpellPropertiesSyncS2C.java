package com.electroblob.wizardry.core.networking.s2c;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SpellPropertiesSyncS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("spell_properties_sync");
    public final Map<ResourceLocation, SpellProperties> propertiesMap;

    public SpellPropertiesSyncS2C(Map<ResourceLocation, SpellProperties> spellPropertiesMap) {
        this.propertiesMap = spellPropertiesMap;
    }

    public SpellPropertiesSyncS2C(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.propertiesMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation spellId = buf.readResourceLocation();
            SpellProperties props = SpellProperties.fromNbt(buf.readNbt());
            propertiesMap.put(spellId, props);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(propertiesMap.size());
        for (Map.Entry<ResourceLocation, SpellProperties> entry : propertiesMap.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            CompoundTag nbt = entry.getValue().toNbt();
            buf.writeNbt(nbt);
        }
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        for(Map.Entry<ResourceLocation, SpellProperties> entry : propertiesMap.entrySet()) {
            Optional<Spell> spell = Optional.ofNullable(Services.REGISTRY_UTIL.getSpell(entry.getKey()));
            if (spell.isEmpty()) {
                EBLogger.warn("Received spell properties for unknown spell: {}", entry.getKey());
                continue;
            }
            spell.get().setProperties(entry.getValue());
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
