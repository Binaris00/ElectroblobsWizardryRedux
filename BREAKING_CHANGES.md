# Breaking Changes

## Events

### SpellCastEvent Refactor

- `SpellCastEvent` constructors and all its sub-events constructors are deprecated in favor of using the ones with the `CastContext` parameter. 
- `SpellCastEvent.Source` is now `SpellCastEvent.Sources`. `SpellCastEvent.Source` is now an interface that can be used to create custom cast sources for this event
- `getTicksCasting()` from `Tick` and `Finish` are deprecated in favor of `context.castingTicks()`

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


## Others

- Deleted `EBClientConstants` and moved constants to `ArcaneWorkbenchScreen`

### SpellProperty codec-based serialization

- Deleted the `IPropertyType`, `PropertyType` and `PropertyTypes` classes from `api.content.spell.properties`.
- `SpellProperty` now stores a `Codec<T>` (previously an `IPropertyType<T>`) used for serialization via `JsonOps`/`NbtOps`.
- Custom property types are now provided by passing a `Codec<T>` to `SpellProperty#createProperty(String, T, Codec)`.
- `SpellProperties#fromNbt`, `fromJson`, `toNbt` and `toJson` now serialize through the property's codec instead of the old functional `IPropertyType` handlers.
- Decoded context maps (`enabled` property) are now immutable (`ImmutableMap` produced by `Codec.unboundedMap`); code that mutates the result of `SpellProperties#get(DefaultProperties.ENABLED)` will get an `UnsupportedOperationException`.