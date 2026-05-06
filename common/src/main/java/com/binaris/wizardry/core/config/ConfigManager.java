package com.binaris.wizardry.core.config;

import com.binaris.wizardry.api.content.event.EBPlayerJoinServerEvent;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.config.option.ConfigOption;
import com.binaris.wizardry.core.config.util.ConfigType;
import com.binaris.wizardry.core.networking.s2c.ConfigSyncS2C;
import com.binaris.wizardry.core.platform.Services;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ConfigManager {
    /** List of all the registered config providers, used to load, sync, and save configs */
    private static final ArrayList<ConfigProvider> CONFIG_PROVIDERS = new ArrayList<>();

    /** Path to the server config directory, set in the server init */
    private static Path serverConfigPath = null;

    /**
     * Register a config provider, by default it will load automatically it's file and properties (in case if isn't server config)
     *
     * @param provider The config provider to register
     */
    public static void register(ConfigProvider provider) {
        register(provider, true);
    }

    /**
     * Register a config provider, it will load automatically it's file and properties (in case if isn't server config) if load is true
     *
     * @param provider The config provider to register
     * @param load     Whether to load the config provider automatically
     */
    public static void register(ConfigProvider provider, boolean load) {
        CONFIG_PROVIDERS.add(provider);
        if (load) load(provider);
    }

    /**
     * Load a config provider, checking if it's registered and creating the file if it doesn't exist, loading the file otherwise
     *
     * @param provider The config provider to load
     */
    public static void load(ConfigProvider provider) {
        if (!CONFIG_PROVIDERS.contains(provider)) return;
        if (provider.getType() == ConfigType.SERVER && serverConfigPath == null) return;

        if (!Files.exists(getPath(provider))) {
            save(provider);
            return;
        }
        loadFile(provider);
    }

    /**
     * Load all server configs, setting the server config path and loading all server configs
     *
     * @param worldPath The path to the world
     */
    public static void loadServerConfigs(Path worldPath) {
        serverConfigPath = worldPath.resolve("serverconfig");
        CONFIG_PROVIDERS.stream().filter(p -> p.getType() == ConfigType.SERVER).forEach(ConfigManager::load);
    }

    /**
     * Save a config provider, checking if it's registered and saving the file
     *
     * @param configProvider The config provider to save
     */
    public static void save(ConfigProvider configProvider) {
        JsonObject obj = new JsonObject();
        configProvider.build().forEach(option -> saveOption(option, obj));

        try {
            Files.createDirectories(getPath(configProvider).getParent());
            try (Writer writer = Files.newBufferedWriter(getPath(configProvider))) {
                new GsonBuilder().setPrettyPrinting().create().toJson(obj, writer);
            }
        } catch (IOException e) {
            EBLogger.error("Failed to save config file", e);
        }
    }

    /**
     * Load a config provider from a file, checking if it's registered and loading the file
     *
     * @param provider The config provider to load
     */
    public static void loadFile(ConfigProvider provider) {
        try (Reader reader = Files.newBufferedReader(getPath(provider))) {
            JsonElement json = JsonParser.parseReader(reader);
            if (!json.isJsonObject()) return;
            JsonObject obj = json.getAsJsonObject();

            provider.build().stream().filter((configOption -> obj.has(configOption.getKey())))
                    .forEach(option -> loadOption(option, obj.get(option.getKey())));
        } catch (IOException e) {
            EBLogger.error("Failed to load config file, using defaults", e);
        }
    }

    public static void onPlayerJoin(EBPlayerJoinServerEvent event) {
        syncToPlayer(event.getPlayer());
    }

    /**
     * Syncs the config to a player, sending a ConfigSyncS2C message to the player for each config provider (common/server)
     *
     * @param player The player to sync the config to
     */
    private static void syncToPlayer(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        for (ConfigProvider provider : CONFIG_PROVIDERS) {
            if (provider.getType() == ConfigType.COMMON || provider.getType() == ConfigType.SERVER) {
                Map<String, JsonElement> syncData = new HashMap<>();
                provider.build().forEach(option -> encodeOption(option, syncData));
                Services.NETWORK_HELPER.sendTo(serverPlayer, new ConfigSyncS2C(provider.getConfigName(), syncData));
            }
        }
    }

    /**
     * Restores the local configuration values for common and server configs, used when client is disconnected (by
     * any reason) from the server and we need to reset the server configs to default.
     */
    public static void restoreLocalConfigs() {
        EBLogger.info("Restoring local configuration values...");

        for (ConfigProvider provider : CONFIG_PROVIDERS) {
            if (provider.getType() == ConfigType.COMMON) {
                loadFile(provider);
            } else if (provider.getType() == ConfigType.SERVER) {
                provider.build().forEach(ConfigManager::resetOptionToDefault);
            }
        }

        // Clean up server config path
        serverConfigPath = null;
    }

    public static ArrayList<ConfigProvider> getConfigProviders() {
        return CONFIG_PROVIDERS;
    }

    /**
     * Returns the path to the config file for the given provider, if it's a server config, it will return the assigned path
     * in the server init, otherwise it will return the path in the config directory.
     *
     * @param provider The config provider
     * @return The path to the config file
     * @throws RuntimeException if config provider is server type and the server config path isn't set
     */
    private static Path getPath(ConfigProvider provider) {
        if (provider.getType() == ConfigType.SERVER) {
            if (serverConfigPath == null) throw new RuntimeException("Server config path not set");
            return serverConfigPath.resolve(provider.getConfigName() + ".json");
        }
        return Services.PLATFORM.getConfigDirectory().resolve(provider.getConfigName() + ".json");
    }


    private static <T> void resetOptionToDefault(ConfigOption<T> option) {
        option.set(option.getDefault());
    }

    private static <T> void saveOption(ConfigOption<T> option, JsonObject obj) {
        option.getCodec().encodeStart(JsonOps.INSTANCE, option.get()).result().ifPresent(el -> obj.add(option.getKey(), el));
    }

    private static <T> void encodeOption(ConfigOption<T> option, Map<String, JsonElement> configData) {
        option.getCodec().encodeStart(JsonOps.INSTANCE, option.get()).result().ifPresent(el -> configData.put(option.getKey(), el));
    }

    private static <T> void loadOption(ConfigOption<T> option, JsonElement element) {
        option.getCodec().parse(JsonOps.INSTANCE, element).result().ifPresent(val -> {
            if (option.validate(val).isEmpty()) {
                option.set(val);
            } else {
                EBLogger.warn("Invalid value for {}: {}, settings defaults.", option.getKey(), val);
                option.set(option.getDefault());
            }
        });
    }

    private ConfigManager() {
    }
}