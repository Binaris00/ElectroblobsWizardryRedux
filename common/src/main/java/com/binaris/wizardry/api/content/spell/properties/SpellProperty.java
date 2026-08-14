package com.binaris.wizardry.api.content.spell.properties;

import com.binaris.wizardry.content.spell.DefaultProperties;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/// A class representing a property of a spell, such as its cost or cooldown. Each property has a type (e.g. Integer,
/// Float, Boolean), a default value, and a current value, these properties are identified by a unique string.
///
/// Properties can be created using the static factory methods provided, such as [#intProperty(String)] or
/// [#booleanProperty(String, boolean)]. Once created, the property's value can be accessed and modified using
/// the [#get()] and [#set(Object)] methods respectively.
///
/// Check [DefaultProperties] for examples of predefined properties.
///
/// @param <T> The type of the property's value.
/// @see DefaultProperties
/// @see SpellProperties#builder()
@SuppressWarnings("unused")
public class SpellProperty<T> {
    private static final Set<SpellProperty<?>> PROPERTIES = new HashSet<>();
    protected String identifier = null;
    protected T value = null;
    protected T defaultValue = null;
    protected Codec<T> codec = null;

    private SpellProperty() {
    }

    /// Creates a new byte spell property with the given identifier and a default value of `0`.
    ///
    /// @param id The unique identifier for this property.
    ///
    /// @return The newly created property.
    public static SpellProperty<Byte> byteProperty(String id) {
        return byteProperty(id, (byte) 0);
    }

    /// Creates a new short spell property with the given identifier and a default value of `0`.
    ///
    /// @param id The unique identifier for this property.
    ///
    /// @return The newly created property.
    public static SpellProperty<Short> shortProperty(String id) {
        return shortProperty(id, (short) 0);
    }

    /// Creates a new integer spell property with the given identifier and a default value of `0`.
    ///
    /// @param id The unique identifier for this property.
    ///
    /// @return The newly created property.
    public static SpellProperty<Integer> intProperty(String id) {
        return intProperty(id, 0);
    }

    /// Creates a new long spell property with the given identifier and a default value of `0L`.
    ///
    /// @param id The unique identifier for this property.
    ///
    /// @return The newly created property.
    public static SpellProperty<Long> longProperty(String id) {
        return longProperty(id, 0L);
    }

    /// Creates a new float spell property with the given identifier and a default value of `0.0F`.
    ///
    /// @param id The unique identifier for this property.
    ///
    /// @return The newly created property.
    public static SpellProperty<Float> floatProperty(String id) {
        return floatProperty(id, 0f);
    }

    /// Creates a new double spell property with the given identifier and a default value of `0.0D`.
    ///
    /// @param id The unique identifier for this property.
    ///
    /// @return The newly created property.
    public static SpellProperty<Double> doubleProperty(String id) {
        return doubleProperty(id, 0d);
    }

    /// Creates a new boolean spell property with the given identifier and a default value of `false`.
    ///
    /// @param id The unique identifier for this property.
    ///
    /// @return The newly created property.
    public static SpellProperty<Boolean> booleanProperty(String id) {
        return booleanProperty(id, false);
    }

    /// Creates a new string spell property with the given identifier and a default value of `""`.
    ///
    /// @param id The unique identifier for this property.
    ///
    /// @return The newly created property.
    public static SpellProperty<String> stringProperty(String id) {
        return stringProperty(id, "");
    }

    /// Creates a new byte spell property with the given identifier and default value.
    ///
    /// Constructs the property through [#createProperty(String, Object, Codec)] backed by the `Codec.BYTE` codec.
    ///
    /// @param id The unique identifier for this property.
    /// @param value The default value the property is initialized with.
    ///
    /// @return The newly created property.
    public static SpellProperty<Byte> byteProperty(String id, byte value) {
        return createProperty(id, value, Codec.BYTE);
    }

    /// Creates a new short spell property with the given identifier and default value.
    ///
    /// Constructs the property through [#createProperty(String, Object, Codec)] backed by the `Codec.SHORT` codec.
    ///
    /// @param id The unique identifier for this property.
    /// @param value The default value the property is initialized with.
    ///
    /// @return The newly created property.
    public static SpellProperty<Short> shortProperty(String id, short value) {
        return createProperty(id, value, Codec.SHORT);
    }

    /// Creates a new integer spell property with the given identifier and default value.
    ///
    /// Constructs the property through [#createProperty(String, Object, Codec)] backed by the `Codec.INT` codec.
    /// @param id The unique identifier for this property.
    /// @param value The default value the property is initialized with.
    ///
    /// @return The newly created property.
    public static SpellProperty<Integer> intProperty(String id, int value) {
        return createProperty(id, value, Codec.INT);
    }

    /// Creates a new long spell property with the given identifier and default value.
    ///
    /// Constructs the property through [#createProperty(String, Object, Codec)] backed by the `Codec.LONG` codec.
    ///
    /// @param id The unique identifier for this property.
    /// @param value The default value the property is initialized with.
    ///
    /// @return The newly created property.
    public static SpellProperty<Long> longProperty(String id, long value) {
        return createProperty(id, value, Codec.LONG);
    }

    /// Creates a new float spell property with the given identifier and default value.
    ///
    /// Constructs the property through [#createProperty(String, Object, Codec)] backed by the `Codec.FLOAT` codec.
    ///
    /// @param id The unique identifier for this property.
    /// @param value The default value the property is initialized with.
    ///
    /// @return The newly created property.
    public static SpellProperty<Float> floatProperty(String id, float value) {
        return createProperty(id, value, Codec.FLOAT);
    }

    /// Creates a new double spell property with the given identifier and default value.
    ///
    /// Constructs the property through [#createProperty(String, Object, Codec)] backed by the `Codec.DOUBLE` codec.
    ///
    /// @param id The unique identifier for this property.
    /// @param value The default value the property is initialized with.
    ///
    /// @return The newly created property.
    public static SpellProperty<Double> doubleProperty(String id, double value) {
        return createProperty(id, value, Codec.DOUBLE);
    }

    /// Creates a new boolean spell property with the given identifier and default value.
    ///
    /// Constructs the property through [#createProperty(String, Object, Codec)] backed by the `Codec.BOOL` codec.
    ///
    /// @param id The unique identifier for this property.
    /// @param value The default value the property is initialized with.
    ///
    /// @return The newly created property.
    public static SpellProperty<Boolean> booleanProperty(String id, boolean value) {
        return createProperty(id, value, Codec.BOOL);
    }

    /// Creates a new string spell property with the given identifier and default value.
    ///
    /// Constructs the property through [#createProperty(String, Object, Codec)] backed by the `Codec.STRING` codec
    ///
    /// @param id The unique identifier for this property.
    /// @param value The default value the property is initialized with.
    ///
    /// @return The newly created property.
    public static SpellProperty<String> stringProperty(String id, String value) {
        return createProperty(id, value, Codec.STRING);
    }

    /// Creates a spell property whose value maps a spell context name to a boolean (e.g. whether the property
    /// applies in that context).
    ///
    /// Backed by [Codec.unboundedMap] over `Codec.STRING` keys and `Codec.BOOL` values.
    ///
    /// @param id The unique identifier for this property.
    /// @param value The default context map; typically `SpellContexts.createDefaultMap()`.
    ///
    /// @return The newly created property.
    public static SpellProperty<Map<String, Boolean>> contextMapProperty(String id, Map<String, Boolean> value) {
        return createProperty(id, value, Codec.unboundedMap(Codec.STRING, Codec.BOOL));
    }

    /// Shared factory that builds, configures and registers a new spell property.
    ///
    /// Sets the identifier, codec, default value and initial value on a fresh instance, then registers it in the
    /// static property registry so [#fromID(String)] can resolve it later. All the typed factory methods (e.g.
    /// [#intProperty(String, int)]) delegate here.
    ///
    /// @param <T> The type of the property's value.
    /// @param identifier The unique identifier for this property.
    /// @param defaultValue The default value, also used as the initial current value.
    /// @param codec The codec used to (de)serialize the property's value from/to JSON and NBT.
    ///
    /// @return The newly created property.
    protected static <T> SpellProperty<T> createProperty(String identifier, T defaultValue, Codec<T> codec) {
        SpellProperty<T> property = new SpellProperty<>();
        property.identifier = identifier;
        property.codec = codec;
        property.defaultValue = defaultValue;
        property.value = defaultValue;
        PROPERTIES.add(property);
        return property;
    }

    /// Looks up a registered property by its identifier.
    ///
    /// Returns the first property in the global registry whose identifier matches, or `null` if none is
    /// registered. The (de)serialization paths in [SpellProperties] use this to resolve the property prototype
    /// behind a serialized key.
    ///
    /// @param identifier The identifier of the property to find.
    ///
    /// @return The matching property, or `null` if none is registered.
    public static @Nullable SpellProperty<?> fromID(String identifier) {
        return PROPERTIES.stream().filter(p -> p.identifier.equals(identifier)).findFirst().orElse(null);
    }

    /// Returns the current value of this property.
    ///
    /// @return The current value.
    public T get() {
        return this.value;
    }

    /// Returns the unique identifier of this property.
    ///
    /// @return The identifier string.
    public String getIdentifier() {
        return identifier;
    }

    /// Sets the current value of this property.
    ///
    /// Only the current value is updated; the default value is left untouched (see [#defaultValue(Object)]).
    ///
    /// @param value The new value to assign.
    public void set(T value) {
        this.value = value;
    }

    /// Sets both the default and the current value of this property, returning the property for chaining.
    ///
    /// Used by [SpellProperties.Builder] when overriding a property's default value, keeping `get()` and
    /// `getDefaultValue()` consistent.
    ///
    /// @param value The new default value to set.
    ///
    /// @return This property, for method chaining.
    SpellProperty<T> defaultValue(T value) {
        this.defaultValue = value;
        this.value = value;
        return this;
    }

    /// Compares two properties for equality based on identifier and codec.
    ///
    /// Two properties are considered equal when they share the same identifier and an equal codec. This is used
    /// by [SpellProperties#get(SpellProperty)] to match a stored property against a reference property.
    ///
    /// @param obj The object to compare against.
    ///
    /// @return `true` if both the identifier and the codec match.
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpellProperty<?> property) {
            return property.identifier.equals(this.identifier) && property.codec.equals(this.codec);
        }
        return false;
    }

    /// Returns the default value of this property.
    ///
    /// @return The default value.
    public T getDefaultValue() {
        return this.defaultValue;
    }

    /// Creates an independent copy of this property.
    ///
    /// The copy keeps the same identifier, codec, default and current value, but is not registered in the global
    /// registry. [SpellProperties.Builder] uses this so every spell holds its own mutable instance instead of
    /// sharing the registered prototype.
    ///
    /// @return A new property equal to this one.
    public SpellProperty<T> copyOf() {
        SpellProperty<T> cloned = new SpellProperty<>();
        cloned.identifier = this.identifier;
        cloned.codec = this.codec;
        cloned.defaultValue = this.defaultValue;
        cloned.value = this.value;
        return cloned;
    }

    /// Creates a copy of this property carrying the given value.
    ///
    /// Equivalent to [#copyOf()] followed by assigning the value; used when decoding a stored value into a fresh
    /// instance (see [#parseFrom(DynamicOps, Object, String)]).
    ///
    /// @param value The value to set on the copy.
    ///
    /// @return A new property holding the given value.
    SpellProperty<T> copyWithValue(T value) {
        SpellProperty<T> cloned = copyOf();
        cloned.value = value;
        return cloned;
    }

    /// Decodes this property's value from a dynamic ops input and returns a copy holding the decoded value.
    ///
    /// Parses `input` with this property's codec and wraps the result via [#copyWithValue(Object)]. On a decode
    /// failure it throws an [IllegalArgumentException] describing the error, which the JSON/NBT loading paths in
    /// [SpellProperties] rely on to reject malformed property data instead of silently falling back.
    ///
    /// @param <X> The dynamic ops input element type.
    /// @param ops The dynamic ops interpreting `input` (e.g. [JsonOps.INSTANCE] or [NbtOps.INSTANCE]).
    /// @param input The encoded value to decode.
    /// @param id The property identifier, used only for the error message.
    ///
    /// @return A copy of this property holding the decoded value.
    /// @throws IllegalArgumentException if `input` cannot be decoded with this property's codec.
    <X> SpellProperty<T> parseFrom(DynamicOps<X> ops, X input, String id) {
        DataResult<T> result = codec.parse(ops, input);
        return result.map(this::copyWithValue)
                .result()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Failed to decode spell property '" + id + "': " + result.error().map(DataResult.PartialResult::message).orElse("unknown error")));
    }
}
