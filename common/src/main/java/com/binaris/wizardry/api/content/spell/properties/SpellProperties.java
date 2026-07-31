package com.binaris.wizardry.api.content.spell.properties;

import com.binaris.wizardry.api.content.SpellTypeRegistry;
import com.binaris.wizardry.api.content.event.EBPlayerJoinServerEvent;
import com.binaris.wizardry.api.content.spell.*;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.networking.s2c.SpellPropertiesSyncS2C;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// A collection of a spell's properties, holding one value per registered [SpellProperty].
///
/// Instances are built through [#builder()] (usually populated with
/// [Builder#assignBaseProperties(SpellTier, Element, SpellType, SpellAction, int, int, int)]), or reconstructed from a
/// serialized form with [#fromJson(JsonObject)] / [#fromNbt(CompoundTag)]. Values are read back with
/// [#get(SpellProperty)], which falls back to the property's default when it is not present. [Spell] exposes typed
/// helpers over this class such as [getCooldown()], [getTier()] and [#isEnabledInContext(SpellContext)].
public class SpellProperties {
    private final List<SpellProperty<?>> properties;

    private SpellProperties(List<SpellProperty<?>> properties) {
        this.properties = properties;
    }

    /// Creates a spell properties instance with no properties.
    ///
    /// Used as the default for spells that do not rely on the base property system (see [Spell.getProperties]),
    /// where every lookup falls back to the corresponding property's default value.
    ///
    /// @return A new empty [SpellProperties] instance.
    public static SpellProperties empty() {
        return new SpellProperties(new ArrayList<>());
    }

    /// Starts building a new spell properties instance.
    ///
    /// Returns a [Builder] that accumulates property copies; populate it with
    /// [Builder#assignBaseProperties(SpellTier, Element, SpellType, SpellAction, int, int, int)] and
    /// [Builder#add(SpellProperty)], then finish with [Builder#build()]. All spells in the `Spells` registry are created this
    /// way.
    ///
    /// @return A new [Builder].
    public static Builder builder() {
        return new Builder();
    }

    /// Reconstructs spell properties from an NBT tag produced by [#toNbt()].
    ///
    /// Each key is resolved to its registered [SpellProperty] via [SpellProperty.fromID(String)] and decoded with
    /// the property's codec through [SpellProperty.parseFrom]. The `"base_properties"` compound holds the non-base
    /// properties and is decoded last. Throws [IllegalArgumentException] if any stored value cannot be decoded.
    ///
    /// @param tag The NBT tag to read, as written by [#toNbt()].
    ///
    /// @return A new [SpellProperties] instance with the decoded values.
    public static SpellProperties fromNbt(CompoundTag tag) {
        Builder builder = builder();
        for (String key : tag.getAllKeys()) {
            if (key.equals("base_properties")) continue;
            SpellProperty<?> temp = SpellProperty.fromID(key);
            if (temp == null || temp.codec == null) continue;
            builder.add(temp.parseFrom(NbtOps.INSTANCE, tag.get(key), key));
        }

        if (tag.contains("base_properties")) {
            CompoundTag basePropsTag = tag.getCompound("base_properties");
            for (String key : basePropsTag.getAllKeys()) {
                SpellProperty<?> temp = SpellProperty.fromID(key);
                if (temp == null || temp.codec == null) continue;
                builder.add(temp.parseFrom(NbtOps.INSTANCE, basePropsTag.get(key), key));
            }
        }
        return builder.build();
    }

    /// Reconstructs spell properties from a JSON object produced by [#toJson()].
    ///
    /// Each entry is resolved to its registered [SpellProperty] via [SpellProperty.fromID(String)] and decoded
    /// with the property's codec through [SpellProperty.parseFrom]. The `"base_properties"` object holds the
    /// non-base properties. Used by [PropertiesDataManager] to load spell overrides from data packs; throws
    /// [IllegalArgumentException] on malformed values so the whole spell's properties are rejected.
    ///
    /// @param jsonObject The JSON object to read, as written by [#toJson()].
    ///
    /// @return A new [SpellProperties] instance with the decoded values.
    public static SpellProperties fromJson(JsonObject jsonObject) {
        Builder builder = builder();
        jsonObject.entrySet().forEach(entry -> {
            String id = entry.getKey();
            if (id.equals("base_properties")) return;
            SpellProperty<?> temp = SpellProperty.fromID(id);
            if (temp == null || temp.codec == null) return;
            builder.add(temp.parseFrom(JsonOps.INSTANCE, entry.getValue(), id));
        });

        if (jsonObject.has("base_properties")) {
            JsonObject basePropsJson = jsonObject.getAsJsonObject("base_properties");
            basePropsJson.entrySet().forEach(entry -> {
                String id = entry.getKey();
                SpellProperty<?> temp = SpellProperty.fromID(id);
                if (temp == null || temp.codec == null) return;
                builder.add(temp.parseFrom(JsonOps.INSTANCE, entry.getValue(), id));
            });
        }
        return builder.build();
    }

    /// Sends the full spell properties map to a player when they join the server.
    ///
    /// Collects every registered spell's properties keyed by its [ResourceLocation] and ships them to the joining
    /// client in a [SpellPropertiesSyncS2C] packet, so clients have up-to-date (e.g. data-pack overridden)
    /// values. Does nothing when the event's player level is the client side.
    ///
    /// @param event The join event carrying the player to notify.
    public static void onPlayerJoin(EBPlayerJoinServerEvent event) {
        if (event.getPlayer().level().isClientSide) return;

        Map<ResourceLocation, SpellProperties> map = Services.REGISTRY_UTIL.getSpells().stream()
                .collect(java.util.stream.Collectors.toMap(Spell::getLocation, Spell::getProperties));

        Services.NETWORK_HELPER.sendTo((ServerPlayer) event.getPlayer(), new SpellPropertiesSyncS2C(map));
    }

    /// Returns the stored value for the given property, or its default if not present.
    ///
    /// Searches the stored properties for one that equals `property` (same identifier and codec) and returns its
    /// current value; if none matches, returns `property.getDefaultValue()`.
    ///
    /// @param <T> The property's value type.
    /// @param property The reference property to look up.
    ///
    /// @return The stored value, or the property's default value.
    @SuppressWarnings("unchecked")
    public <T> T get(SpellProperty<T> property) {
        for (SpellProperty<?> prop : properties) {
            if (prop.equals(property)) {
                return (T) prop.get();
            }
        }
        return property.getDefaultValue();
    }

    /// Returns the list of properties held by this instance.
    ///
    /// @return The internal list of properties.
    public List<SpellProperty<?>> getProperties() {
        return properties;
    }

    /// Returns the spell's cooldown in ticks.
    ///
    /// Equivalent to [#get(SpellProperty)] on [DefaultProperties.COOLDOWN].
    ///
    /// @return The cooldown in ticks.
    public int getCooldown() {
        return get(DefaultProperties.COOLDOWN);
    }

    /// Returns the spell's mana cost.
    ///
    /// Equivalent to [#get(SpellProperty)] on [DefaultProperties.COST].
    ///
    /// @return The mana cost.
    public int getCost() {
        return get(DefaultProperties.COST);
    }

    /// Returns the spell's charge-up time in ticks.
    ///
    /// Equivalent to [#get(SpellProperty)] on [DefaultProperties.CHARGEUP].
    ///
    /// @return The charge-up time in ticks.
    public int getChargeup() {
        return get(DefaultProperties.CHARGEUP);
    }

    /// Returns the spell's [SpellType], falling back to [SpellTypes.UTILITY].
    ///
    /// Reads [DefaultProperties.SPELL_TYPE] and resolves it through [SpellTypeRegistry]; returns the utility type
    /// when the stored name is unregistered or unparseable.
    ///
    /// @return The spell's type, or `SpellTypes.UTILITY`.
    public SpellType getType() {
        String name = get(DefaultProperties.SPELL_TYPE);
        SpellType type = SpellTypeRegistry.get(ResourceLocation.tryParse(name));
        return type != null ? type : SpellTypes.UTILITY;
    }

    /// Returns the spell's [SpellTier], falling back to [SpellTiers.NOVICE].
    ///
    /// Reads [DefaultProperties.TIER] and matches it against every registered tier; returns the novice tier when
    /// no tier's location matches the stored value.
    ///
    /// @return The spell's tier, or `SpellTiers.NOVICE`.
    public SpellTier getTier() {
        String s = get(DefaultProperties.TIER);
        for (SpellTier tier : Services.REGISTRY_UTIL.getTiers()) {
            if (tier.getOrCreateLocation().toString().equals(s)) return tier;
        }
        return SpellTiers.NOVICE; // Default
    }

    /// Returns the spell's [Element], falling back to [Elements.MAGIC].
    ///
    /// Reads [DefaultProperties.ELEMENT] and matches it against every registered element; returns the magic
    /// element when no element's location matches the stored value.
    ///
    /// @return The spell's element, or `Elements.MAGIC`.
    public Element getElement() {
        String s = get(DefaultProperties.ELEMENT);
        for (Element element : Services.REGISTRY_UTIL.getElements()) {
            if (element.getLocation().toString().equals(s)) {
                return element;
            }
        }
        return Elements.MAGIC; // Default
    }

    /// Returns the spell's [SpellAction], falling back to [SpellAction.NONE].
    ///
    /// Reads [DefaultProperties.SPELL_ACTION] and resolves it through [SpellAction.get]; returns `SpellAction.NONE`
    /// when the stored name is unregistered or unparseable.
    ///
    /// @return The spell's action, or `SpellAction.NONE`.
    public SpellAction getAction() {
        String action = get(DefaultProperties.SPELL_ACTION);
        SpellAction spellAction = SpellAction.get(ResourceLocation.tryParse(action));
        return spellAction != null ? spellAction : SpellAction.NONE;
    }

    /// Returns whether this spell is enabled in the given spell context.
    ///
    /// Looks up the enabled context map ([DefaultProperties.ENABLED]) and returns the stored boolean for
    /// `context.getName()`, defaulting to `true` when the context is not present in the map.
    ///
    /// @param context The context to check.
    ///
    /// @return `true` if the spell is enabled in that context.
    public boolean isEnabledInContext(SpellContext context) {
        Map<String, Boolean> enabled = get(DefaultProperties.ENABLED);
        return enabled.getOrDefault(context.getName(), true);
    }

    /// Serializes this instance to a JSON object for data packs and data generation.
    ///
    /// Base properties are written at the root and the remaining ones nested under `"base_properties"`. Each value
    /// is encoded with its property's codec using `JsonOps`. Round-trips with [#fromJson(JsonObject)].
    ///
    /// @return The JSON representation of this instance.
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonObject baseProps = new JsonObject();
        addProperty(json, DefaultProperties.ENABLED);
        addProperty(json, DefaultProperties.TIER);
        addProperty(json, DefaultProperties.ELEMENT);
        addProperty(json, DefaultProperties.SPELL_TYPE);
        addProperty(json, DefaultProperties.COST);
        addProperty(json, DefaultProperties.COOLDOWN);
        addProperty(json, DefaultProperties.CHARGEUP);
        addProperty(json, DefaultProperties.SPELL_ACTION);
        properties.stream().filter(p -> !isBaseProperty(p)).forEach(p -> addProperty(baseProps, p));
        if (baseProps.size() > 0) json.add("base_properties", baseProps);
        return json;
    }

    /// Serializes this instance to an NBT compound tag for network sync.
    ///
    /// Base properties are written at the root and the remaining ones nested under `"base_properties"`. Each value
    /// is encoded with its property's codec using `NbtOps`. Round-trips with [#fromNbt(CompoundTag)] and is used
    /// by [SpellPropertiesSyncS2C].
    ///
    /// @return The NBT representation of this instance.
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        CompoundTag baseProps = new CompoundTag();
        addProperty(tag, DefaultProperties.ENABLED);
        addProperty(tag, DefaultProperties.TIER);
        addProperty(tag, DefaultProperties.ELEMENT);
        addProperty(tag, DefaultProperties.SPELL_TYPE);
        addProperty(tag, DefaultProperties.COST);
        addProperty(tag, DefaultProperties.COOLDOWN);
        addProperty(tag, DefaultProperties.CHARGEUP);
        addProperty(tag, DefaultProperties.SPELL_ACTION);
        properties.stream().filter(p -> !isBaseProperty(p)).forEach(p -> addProperty(baseProps, p));
        if (!baseProps.isEmpty()) tag.put("base_properties", baseProps);
        return tag;
    }

    /// Encodes a matching property's current value into an NBT parent under its identifier.
    ///
    /// Finds the stored property equal to `referenceProperty`, encodes its value with `NbtOps` and writes the
    /// resulting tag; encode failures are logged as warnings and skipped.
    ///
    /// @param <T> The property's value type.
    /// @param parent The compound tag to write into.
    /// @param referenceProperty The property whose stored value should be encoded.
    private <T> void addProperty(CompoundTag parent, SpellProperty<T> referenceProperty) {
        SpellProperty<T> property = getProperties().stream().filter(p -> p.equals(referenceProperty))
                .map(p -> (SpellProperty<T>) p).findFirst().orElse(null);
        if (property == null) return;
        property.codec.encodeStart(NbtOps.INSTANCE, property.value)
                .resultOrPartial(err -> EBLogger.warn("Failed to encode spell property '" + property.identifier + "': " + err))
                .ifPresent(tag -> parent.put(property.identifier, tag));
    }

    /// Encodes a matching property's current value into a JSON parent under its identifier.
    ///
    /// Finds the stored property equal to `referenceProperty`, encodes its value with `JsonOps` and adds the
    /// resulting element; encode failures are logged as warnings and skipped.
    ///
    /// @param <T> The property's value type.
    /// @param parent The JSON object to write into.
    /// @param referenceProperty The property whose stored value should be encoded.
    private <T> void addProperty(JsonObject parent, SpellProperty<T> referenceProperty) {
        SpellProperty<T> property = getProperties().stream().filter(p -> p.equals(referenceProperty))
                .map(p -> (SpellProperty<T>) p).findFirst().orElse(null);
        if (property == null) return;
        property.codec.encodeStart(JsonOps.INSTANCE, property.value)
                .resultOrPartial(err -> EBLogger.warn("Failed to encode spell property '" + property.identifier + "': " + err))
                .ifPresent(json -> parent.add(property.identifier, json));
    }

    /// Returns whether the given property is one of the fixed base spell properties.
    ///
    /// Base properties (enabled, tier, element, type, action, cost, cooldown and chargeup) are written at the root
    /// of the serialized form instead of inside `"base_properties"`.
    ///
    /// @param prop The property to check.
    ///
    /// @return `true` if it is a base property.
    public boolean isBaseProperty(@NotNull SpellProperty<?> prop) {
        return prop.identifier.equals(DefaultProperties.ENABLED.identifier)
                || prop.identifier.equals(DefaultProperties.TIER.identifier)
                || prop.identifier.equals(DefaultProperties.ELEMENT.identifier)
                || prop.identifier.equals(DefaultProperties.SPELL_TYPE.identifier)
                || prop.identifier.equals(DefaultProperties.COST.identifier)
                || prop.identifier.equals(DefaultProperties.COOLDOWN.identifier)
                || prop.identifier.equals(DefaultProperties.CHARGEUP.identifier)
                || prop.identifier.equals(DefaultProperties.SPELL_ACTION.identifier);
    }

    /// Fluent builder that accumulates cloned spell properties and produces a [SpellProperties] snapshot.
    ///
    /// Each [#add(SpellProperty)] call stores a [SpellProperty.copyOf] clone so later mutation of the registered
    /// prototype does not affect the built instance. Use [assignBaseProperties(SpellTier, Element, SpellType,
    /// SpellAction, int, int, int)] to populate the standard base set, then finish with [#build()].
    public static class Builder {
        private final List<SpellProperty<?>> builder = new ArrayList<>();

        private Builder() {
        }

        /// Adds the standard base properties with the given values.
        ///
        /// Registers enabled, element, type, tier, action, cost, cooldown and chargeup. Element, type, tier and
        /// action are stored as their [ResourceLocation] strings so they survive serialization and can be
        /// resolved back via [#getElement()], [#getType()], [#getTier()] and [#getAction()]. The enabled map keeps
        /// its default contents.
        ///
        /// @param tier The spell's tier.
        /// @param element The spell's element.
        /// @param type The spell's type.
        /// @param action The spell's action.
        /// @param cost The mana cost.
        /// @param charge The charge-up time in ticks.
        /// @param cooldown The cooldown in ticks.
        ///
        /// @return This builder, for chaining.
        public Builder assignBaseProperties(SpellTier tier, Element element, SpellType type, SpellAction action, int cost, int charge, int cooldown) {
            add(DefaultProperties.ENABLED);
            add(DefaultProperties.ELEMENT, element.getLocation().toString());
            add(DefaultProperties.SPELL_TYPE, type.getLocation().toString());
            add(DefaultProperties.TIER, tier.getOrCreateLocation().toString());
            add(DefaultProperties.SPELL_ACTION, action.location.toString());
            add(DefaultProperties.COST, cost);
            add(DefaultProperties.COOLDOWN, cooldown);
            add(DefaultProperties.CHARGEUP, charge);
            return this;
        }

        /// Adds a clone of the given property to this builder.
        ///
        /// @param <T> The property's value type.
        /// @param property The property to add, copied via [SpellProperty.copyOf].
        ///
        /// @return This builder, for chaining.
        public <T> Builder add(SpellProperty<T> property) {
            if (property != null) {
                SpellProperty<T> cloned = property.copyOf();
                builder.add(cloned);
            }
            return this;
        }

        /// Adds a clone of the given property with an overridden default value.
        ///
        /// @param <T> The property's value type.
        /// @param property The property to add.
        /// @param defaultValue The default value to assign to the clone.
        ///
        /// @return This builder, for chaining.
        public <T> Builder add(SpellProperty<T> property, T defaultValue) {
            if (property != null) {
                SpellProperty<T> cloned = property.copyOf();
                cloned.defaultValue(defaultValue);
                builder.add(cloned);
            }
            return this;
        }

        /// Finishes building and returns a snapshot of the accumulated properties.
        ///
        /// @return A new [SpellProperties] instance.
        public SpellProperties build() {
            return new SpellProperties(new ArrayList<>(builder));
        }
    }
}
