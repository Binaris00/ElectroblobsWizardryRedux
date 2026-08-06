# Electroblob's Wizardry Redux

Minecraft 1.20.1 magic mod with 180+ spells, 8 elements. Multi-loader: Fabric + Forge.

## Project structure

- `common/` — shared mod code (most logic lives here)
- `fabric/` — Fabric loader entrypoint + platform-specific code
- `forge/` — Forge loader entrypoint + platform-specific code
- `buildSrc/` — Gradle convention plugins (`multiloader-common`, `multiloader-loader`)

The `fabric/` and `forge/` subprojects depend on `:common` via Gradle configuration (capabilities-based, not a simple `implementation`). Changes to `common/` automatically apply to both.

## Commands

```bash
# Build everything
./gradlew build

# Run Fabric client
./gradlew fabric:runClient

# Run Forge client
./gradlew forge:runClient

# Run Fabric server
./gradlew fabric:runServer

# Run Forge server
./gradlew forge:runServer

# Forge data generation (outputs to common/src/generated/resources)
./gradlew forge:runData

# Run Fabric game tests
./gradlew fabric:runGameTest

# Run Forge game tests
./gradlew forge:runGameTestServer

# Publish to CurseForge & Modrinth (requires CURSEFORGE_TOKEN and MODRINTH_TOKEN env vars)
./gradlew fabric:publishMods forge:publishMods
```

**Key:** `org.gradle.daemon=false` is set in `gradle.properties` — each Gradle invocation starts fresh. No typecheck or lint commands exist.

## Entry points

| Module | Class | Role |
|---|---|---|
| common | `WizardryMainMod` | `init()` called by both loaders; registers config, events, registry stubs |
| fabric | `WizardryFabricMod` (implements `ModInitializer`) | Fabric init; uses Fabric API for registries, events, networking |
| forge | `WizardryForgeMod` (`@Mod(ebwizardry)`) | Forge init; uses Forge event bus and registries |
| common client | `WizardryClientMod` | Client-side setup (config screen) |
| fabric client | `WizardryFabricClient` | Fabric client entrypoint |
| forge client | `WizardryForgeClient` | Forge client setup via `modBus` listener |

## Architecture

- **Platform abstraction:** `Services.PLATFORM`, `Services.OBJECT_DATA`, `Services.NETWORK_HELPER`, `Services.REGISTRY_UTIL` loaded via Java `ServiceLoader`. Each has Fabric and Forge implementations under `fabric/src/.../platform/` and `forge/src/.../platform/`.
- **Custom registries:** Spells, elements, and spell tiers use custom `Registry` objects registered via loader-specific code (`EBRegistriesFabric`, `EBRegistriesForge`). Minecraft registries (blocks, items, entities, etc.) use vanilla `Registry.register()` on Fabric and `DeferredRegister` on Forge.
- **Networking:** Fabric uses Fabric API (`EBFabricServerNetwork`); Forge uses `EBForgeNetwork` with its own packet system.
- **Mixin configs:** Two files on Forge: `ebwizardry.mixins.json` (common) + `ebwizardry.forge.mixins.json` (forge-specific). Fabric uses only `ebwizardry.mixins.json` + access widener (`ebwizardry.accesswidener`).
- **Access transformers:** Forge uses `META-INF/accesstransformer.cfg` from `common/`. Fabric uses `ebwizardry.accesswidener` from `common/`.
- **Data generation:** Forge `runData` task outputs to `common/src/generated/resources/`. Both loaders include this directory in resources (excluding `.cache` and `docs/spells/ebwizardry/**`).

## Dependencies & mod compat

- **Fabric deps (required):** Fabric API 0.92.2+, Fabric Loader 0.16.9+
- **Forge deps (required):** Forge 47.1.25+
- **Cardinal Components API** (Fabric only): embedded via `include()` — bundled in the jar
- **Trinkets** (Fabric optional), **Curios** (Forge optional), **Accessories** (both, optional)
- **JEI** (optional, both loaders)
- **Cloth Config** (optional, both loaders)
- **Mixinextras** (Forge: `jarJar` included, common: `compileOnly`)
- **MarkdownGenerator** (runtime, used for spell doc generation)

## Conventions

- Root package: `com.binaris.wizardry`
- Mod ID: `ebwizardry`
- `ResourceLocation` helper: `WizardryMainMod.location(path)` or `WizardryMainMod.location(namespace, path)`
- Wand upgrade registration: `WandUpgrades.initUpgrades()` called per-loader
- Bookshelf items: `BookshelfMenu.initBookItems()` called per-loader
- `NotImplementedItems.init()` called per-loader
- API events use `WizardryEventBus.fireEvent()` (custom event bus, not Forge/Fabric native)
- Breaking changes tracked in `BREAKING_CHANGES.md` — check before refactoring API classes in `api/`
- `GeometryUtils` → renamed to `VecUtils`; `InventoryUtils` → merged into `EntityUtils`; `DrawingUtils` → moved to `ClientUtils`

## Testing

- Only Forge has jUnit test config (`test { useJUnitPlatform() }` in `forge/build.gradle`)
- Fabric game tests run via `fabric:runGameTest` (uses `-Dfabric-api.gametest`)
- Forge game tests run via `forge:runGameTestServer` (uses `forge.enabledGameTestNamespaces=ebwizardry`)
- No unit tests exist in `common/` or `fabric/` beyond game tests
