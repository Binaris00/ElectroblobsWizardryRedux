# Breaking Changes

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