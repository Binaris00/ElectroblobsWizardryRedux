# Breaking Changes

## Events

### SpellCastEvent Refactor

- `SpellCastEvent` constructors and all its sub-events constructors are deprecated in favor of using the ones with the `CastContext` parameter. 
- `SpellCastEvent.Source` is now `SpellCastEvent.Sources`. `SpellCastEvent.Source` is now an interface that can be used to create custom cast sources for this event
- `getTicksCasting()` from `Tick` and `Finish` are deprecated in favor of `context.castingTicks()`
- `EventResult` and `EventResultHolder` classes have been deleted (not used in code)

### DiscoverSpellEvent Refactor

- `EBDiscoverSpellEvent` has been renamed to `DiscoverSpellEvent`
- `EBDiscoverSpellEvent.Source` (enum) is now `DiscoverSpellEvent.Sources` (enum implementing `Source` interface)
- `DiscoverSpellEvent.Source` is now an interface that can be implemented to define custom discovery sources
- The event now uses `CastContext` internally

## Spell Data Enums Refactor

### SpellType

- `SpellType` has been changed from an enum to an interface. Custom spell types can now be created by implementing the `SpellType` interface and registering them via `SpellTypeRegistry`.
- The default spell types are now defined in the `SpellTypes` enum which implements `SpellType`.
- `SpellType.fromName()` and `SpellType.fromLocation()` have been removed. Use `SpellTypeRegistry.get(ResourceLocation)` instead.
- `SpellType.getLocation()` still exists as the interface method.
- `SpellType.getName()` and `SpellType.getDisplayName()` are no longer direct methods; use `SpellType.getLocation()` and `Util.makeDescriptionId("spelltype", getLocation())` respectively.

### SpellContext

- `SpellContext` has been changed from an enum to an interface. Custom spell contexts can now be created by implementing the `SpellContext` interface.
- The built-in contexts (`BOOK`, `SCROLL`, `WANDS`, `NPCS`, `DISPENSERS`, `COMMANDS`, `TREASURE`, `TRADES`, `LOOTING`) are now defined in the `SpellContexts` enum which implements `SpellContext`.
- `SpellContext.fromKey()`, `SpellContext.getAllKeys()`, `SpellContext.isValidKey()` have been removed. Use `SpellContexts` enum methods or direct string comparison.
- `SpellContext.getKey()` is now `SpellContext.getName()`.

## Armor

### WizardArmorType

- `WizardArmorType` has been renamed to `WizardArmorTypes` (plural).

## Artifact and Event Effect System Refactor

### `IArtifactEffect`
- `IArtifactEffect` now extends `IEventEffect<ArtifactEffectContext>` instead of defining its own methods directly.
- Method Renamed: `onPlayerHurt` is now `onUserHurt`, including some changes in parameters

The trailing `ItemStack artifact` parameter has been replaced with `ArtifactEffectContext context` in all methods:
- `onTick`
- `onHurtEntity`
- `onUserHurt` (formerly `onPlayerHurt`)
- `onKillEntity`
- `onSpellPreCast`
- `onSpellPostCast`

The `ItemStack` can now be retrieved using `context.getArtifact()`.

To allow effects to be applied to non-player entities (e.g., via mob effects), the following methods now take `LivingEntity user` instead of `Player player`:
- `onTick(LivingEntity user, ...)`
- `onHurtEntity(LivingEntity user, ...)`
- `onUserHurt(LivingEntity user, ...)` (formerly `onPlayerHurt`)
- `onKillEntity(LivingEntity user, ...)`

These changes apply to "QuickArtifactEffect" utils too.

## Utils

### GeometryUtils

- `GeometryUtils` has been renamed to `VecUtils`.
- `GeometryUtils#getPitch` deleted because it was not used outside of Ice Spikes spell
- `GeometryUtils#horizontalise` renamed to `VecUtils#flattenToHorizontal`
- `GeometryUtils#getCentre` deleted and replaced for `Vec3.atCenterOf(BlockPos)`
- `GeometryUtils#component` deleted

### InventoryUtils and EntityUtils

- `InventoryUtils` has been deleted and moved all its methods to `EntityUtils`.
- `InventoryUtils#getHotBarAndOffhand` renamed to `EntityUtils#getHotBarAndHandItems`
- `InventoryUtils#getHotbar` renamed to `EntityUtils#getHotbarItems`
- `InventoryUtils#isWearingFullSet` renamed to `EntityUtils#isWearingFullMagicArmorSet`
- `InventoryUtils#doAllArmourPiecesHaveMana` renamed to `EntityUtils#doAllArmorPiecesHaveMana`
- `EntityUtils#isLiving` deleted
- `EntityUtils#applyStandardKnockback` deleted
- `EntityUtils#undoGravity` deleted
 
### DrawingUtils and ClientUtils

- `DrawingUtils#mix` deleted and replaced for `ClientUtils#mixColor`.
- `DrawingUtils#drawTexturedFlippedRectF` deleted
- `DrawingUtils#drawGlitchRect` deleted
- `DrawingUtils#drawTexturedRect` deleted
- `DrawingUtils#drawTexturedFlippedRect` deleted
- `DrawingUtils#drawScaledStringToWidth` deleted 
- The rest of methods have been moved to `ClientUtils`, `DrawingUtils` has been deleted

## Entity Immunities

In favor of creating a new mod for entity immunities, deleting not important parts of the API and cleaning the codebase entity immunities have been deleted.

## Spell Vars

### ISpellVar and IStoredSpellVar Refactor

- `ISpellVar` and `IStoredSpellVar` have been rewritten to use Codecs instead of raw NBT serialization.
- `Persistence` has been renamed to `VarPersistence`.
- The `writeToNbt`/`readFromNbt` methods have been replaced with Codec-based serialization.
- New `SpellVar` and `StoredSpellVar` classes provide the default implementations.

## Spell Modifiers

### SpellModifiers Refactor

- `SpellModifiers` has been significantly refactored. The internal storage now uses `ModifiersInstance` instead of raw float multipliers.
- New `Operation` enum with values: `ADDITION`, `MULTIPLY_BASE`, `MULTIPLY_TOTAL` (replaces old `SET`, `ADD`, `SUBTRACT`, `MULTIPLY`, `DIVIDE`).
- `SpellModifiers.combine()` has been removed.
- `SpellModifiers.operate()` has been removed.
- `SpellModifiers.fromTag()` and `SpellModifiers.toTag()` have been removed. Use `SpellModifiers.load(CompoundTag)` and `SpellModifiers.save()` instead.
- `SpellModifiers.add()`, `SpellModifiers.subtract()`, `SpellModifiers.multiply()`, `SpellModifiers.divide()` have been removed. Use `SpellModifiers.addModifier(String, Operation, float)` instead.
- `SpellModifiers.get(String)` now returns a `float` value instead of a multiplier. Use `SpellModifiers.get(String, float)` to get a modified value.
- `ModifiersInstance` is a new class that handles the calculation of modifier values with the new operation system.

## Projectiles

### MagicProjectileEntity

- `MagicProjectileEntity` now extends `ThrowableProjectile` instead of `ThrowableItemProjectile`.
- New `MagicItemProjectileEntity` class has been created for projectile entities that need item stack behavior (formerly handled by `ThrowableItemProjectile`).
- Projectiles that used to extend `MagicProjectileEntity` with item behavior should now extend `MagicItemProjectileEntity`.

### MagicArrowEntity

- `MagicArrowEntity` has been refactored with new utility methods for handling damage, sound, particles, and custom effects without code repetition.
- Subclasses should use the new utility methods instead of duplicating logic.

## Client

### Spell HUD Skins

- The `SpellHUDSkin` class has been deleted.
- The `spell_hud/` assets directory (JSON and PNG files for all HUD skins) has been removed.
- Spell HUD customization is now handled through resource packs / texture packs instead.

### ICustomHitbox

- `ICustomHitbox` interface has been deleted (was already deprecated).

## Utils

### NBTExtras

- `NBTExtras` utility class has been deleted. Its functionality has been inlined or replaced with standard NBT methods.

## Item Tags

New item tags have been added for better mod compatibility and data-driven customization:
- `ebwizardry:armor_upgrade` - Items that can be used as armor upgrades
- `ebwizardry:magic_crystal_shard` - Magic crystal shard items
- `ebwizardry:spectral_dust` - Spectral dust items
- `ebwizardry:wand_upgrade` - Items that can be used as wand upgrades

## Others

- Deleted `EBClientConstants` and moved constants to `ArcaneWorkbenchScreen`

### SpellProperty codec-based serialization

- Deleted the `IPropertyType`, `PropertyType` and `PropertyTypes` classes from `api.content.spell.properties`.
- `SpellProperty` now stores a `Codec<T>` (previously an `IPropertyType<T>`) used for serialization via `JsonOps`/`NbtOps`.
- Custom property types are now provided by passing a `Codec<T>` to `SpellProperty#createProperty(String, T, Codec)`.
- `SpellProperties#fromNbt`, `fromJson`, `toNbt` and `toJson` now serialize through the property's codec instead of the old functional `IPropertyType` handlers.
- Decoded context maps (`enabled` property) are now immutable (`ImmutableMap` produced by `Codec.unboundedMap`); code that mutates the result of `SpellProperties#get(DefaultProperties.ENABLED)` will get an `UnsupportedOperationException`.