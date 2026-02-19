package com.binaris.wizardry.core.config;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.EBLogger;
import com.binaris.wizardry.api.content.event.EBPlayerJoinServerEvent;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class EBConfigManager {
    private final String modId;
    private final Path configPath;
    private final IConfigProvider configProvider;
    private final boolean shouldSync;

    public EBConfigManager(String modId, Path configPath, IConfigProvider configProvider, boolean shouldSync) {
        this.modId = modId;
        this.configPath = configPath;
        this.configProvider = configProvider;
        this.shouldSync = shouldSync;
    }

    public void load() {
        if (!Files.exists(configPath)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonElement json = JsonParser.parseReader(reader);
            if (!json.isJsonObject()) return;
            JsonObject obj = json.getAsJsonObject();

            for (ConfigOption<?> option : configProvider.getOptions()) {
                if (obj.has(option.getKey())) {
                    loadOption(option, obj.get(option.getKey()));
                }
            }
        } catch (IOException e) {
            EBLogger.error("Failed to load config file, using defaults", e);
        }
    }

    public void reload() {
        load();
    }

    private <T> void loadOption(ConfigOption<T> option, JsonElement element) {
        option.getCodec()
                .parse(JsonOps.INSTANCE, element)
                .result()
                .ifPresent(option::set);
    }

    public void save() {
        JsonObject obj = new JsonObject();

        for (ConfigOption<?> option : configProvider.getOptions()) {
            saveOption(option, obj);
        }

        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(obj, writer);
            }
        } catch (IOException e) {
            EBLogger.error("Failed to save config file", e);
        }
    }

    private <T> void saveOption(ConfigOption<T> option, JsonObject obj) {
        option.getCodec()
                .encodeStart(JsonOps.INSTANCE, option.get())
                .result()
                .ifPresent(el -> obj.add(option.getKey(), el));
    }

    public String getModId() {
        return modId;
    }

    public IConfigProvider getConfigProvider() {
        return configProvider;
    }

    public boolean shouldSync() {
        return shouldSync;
    }

    public static void onPlayerJoin(EBPlayerJoinServerEvent event) {
        for (EBConfigManager manager : WizardryMainMod.getConfigManagers()) {
            if (manager.shouldSync) {
                manager.syncToPlayer(event.getPlayer());
            }
        }
    }

    public void syncToPlayer(net.minecraft.world.entity.player.Player player) {
        if (!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) return;

        java.util.Map<String, com.google.gson.JsonElement> configData = new java.util.HashMap<>();
        for (ConfigOption<?> option : configProvider.getOptions()) {
            encodeOption(option, configData);
        }

        com.binaris.wizardry.core.platform.Services.NETWORK_HELPER.sendTo(
                serverPlayer,
                new com.binaris.wizardry.core.networking.s2c.ConfigSyncS2C(modId, configData)
        );
    }

    private <T> void encodeOption(ConfigOption<T> option, java.util.Map<String, com.google.gson.JsonElement> configData) {
        option.getCodec()
                .encodeStart(JsonOps.INSTANCE, option.get())
                .result()
                .ifPresent(el -> configData.put(option.getKey(), el));
    }
}
