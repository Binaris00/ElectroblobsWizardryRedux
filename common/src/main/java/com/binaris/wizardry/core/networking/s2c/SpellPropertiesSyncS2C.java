package com.binaris.wizardry.core.networking.s2c;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.core.networking.ClientMessageHandler;
import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

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
    public void handleClient() {
        ClientMessageHandler.spellPropertiesSync(this);
    }

    public Map<ResourceLocation, SpellProperties> getPropertiesMap() {
        return propertiesMap;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
