package com.binaris.wizardry.core.networking.s2c;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.networking.ClientMessageHandler;
import com.binaris.wizardry.core.networking.abst.Message;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ConfigSyncS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("config_sync");
    private final String modId;
    private final Map<String, JsonElement> configData;

    public ConfigSyncS2C(String modId, Map<String, JsonElement> configData) {
        this.modId = modId;
        this.configData = configData;
    }

    public ConfigSyncS2C(FriendlyByteBuf buf) {
        this.modId = buf.readUtf();
        int size = buf.readInt();
        this.configData = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String key = buf.readUtf();
            String jsonString = buf.readUtf();
            JsonElement element = JsonParser.parseString(jsonString);
            configData.put(key, element);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(modId);
        buf.writeInt(configData.size());
        for (Map.Entry<String, JsonElement> entry : configData.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeUtf(entry.getValue().toString());
        }
    }

    @Override
    public void handleClient() {
        ClientMessageHandler.configSync(this);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public String getModId() {
        return modId;
    }

    public Map<String, JsonElement> getConfigData() {
        return configData;
    }
}
