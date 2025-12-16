package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.entity.ArrowRainConstruct;
import com.electroblob.wizardry.content.entity.MeteorEntity;
import com.electroblob.wizardry.content.entity.construct.*;
import com.electroblob.wizardry.content.entity.living.*;
import com.electroblob.wizardry.content.entity.projectile.*;
import com.electroblob.wizardry.core.mixin.invoker.SpawnPlacementsInvoker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.electroblob.wizardry.WizardryMainMod.location;

public final class EBEntities {
    static Map<String, DeferredObject<EntityType<? extends Entity>>> ENTITY_TYPES = new HashMap<>();
    public static final DeferredObject<EntityType<DartEntity>> DART = entity(DartEntity::new, "dart", MobCategory.MISC, 0.5F, 0.5F, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<ConjuredArrowEntity>> CONJURED_ARROW = entity(ConjuredArrowEntity::new, "conjured_arrow", MobCategory.MISC, 0.5F, 0.5F, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<FireBoltEntity>> FIRE_BOLT = entity(FireBoltEntity::new, "firebolt", MobCategory.MISC, 0.25f, 0.25f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<FireBombEntity>> FIRE_BOMB = entity(FireBombEntity::new, "firebomb", MobCategory.MISC, 0.25f, 0.25f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<FlamecatcherArrow>> FLAME_CATCHER_ARROW = entity(FlamecatcherArrow::new, "flamecatcher_arrow", MobCategory.MISC, 0.5f, 0.5f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<ForceArrow>> FORCE_ARROW = entity(ForceArrow::new, "force_arrow", MobCategory.MISC, 0.5f, 0.5f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<IceBall>> ICE_BALL = entity(IceBall::new, "iceball", MobCategory.MISC, 0.5F, 0.5F, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<IceLanceEntity>> ICE_LANCE = entity(IceLanceEntity::new, "ice_lance", MobCategory.MISC, 0.5f, 0.5f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<IceShardEntity>> ICE_SHARD = entity(IceShardEntity::new, "ice_shard", MobCategory.MISC, 0.5f, 0.5f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<LightningArrow>> LIGHTNING_ARROW = entity(LightningArrow::new, "lightning_arrow", MobCategory.MISC, 0.5f, 0.5f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<MagicFireballEntity>> MAGIC_FIREBALL = entity(MagicFireballEntity::new, "magic_fireball", MobCategory.MISC, 0.5f, 0.5f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<MagicMissileEntity>> MAGIC_MISSILE = entity(MagicMissileEntity::new, "magic_missile", MobCategory.MISC, 0.5f, 0.5f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<PoisonBombEntity>> POISON_BOMB = entity(PoisonBombEntity::new, "poison_bomb", MobCategory.MISC, 0.25f, 0.25f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<SmokeBombEntity>> SMOKE_BOMB = entity(SmokeBombEntity::new, "smoke_bomb", MobCategory.MISC, 0.25f, 0.25f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<SparkEntity>> SPARK = entity(SparkEntity::new, "spark", MobCategory.MISC, 0.25f, 0.25f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<SparkBombEntity>> SPARK_BOMB = entity(SparkBombEntity::new, "spark_bomb", MobCategory.MISC, 0.25f, 0.25f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<ThunderboltEntity>> THUNDERBOLT = entity(ThunderboltEntity::new, "thunderbolt", MobCategory.MISC, 0.25f, 0.25f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<IceChargeEntity>> ICE_CHARGE = entity(IceChargeEntity::new, "ice_charge", MobCategory.MISC, 0.25f, 0.25f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<DarknessOrbEntity>> DARKNESS_ORB = entity(DarknessOrbEntity::new, "darkness_orb", MobCategory.MISC, 0.5f, 0.5f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<MeteorEntity>> METEOR = entity(MeteorEntity::new, "meteor", MobCategory.MISC, 0.98f, 0.98f, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<ArrowRainConstruct>> ARROW_RAIN = entity(ArrowRainConstruct::new, "arrow_rain", MobCategory.MISC, 3, 3, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<ForceOrbEntity>> FORCE_ORB = entity(ForceOrbEntity::new, "force_orb", MobCategory.MISC, 0.25f, 0.25f, MagicType.PROJECTILE);
    public static final DeferredObject<EntityType<BlizzardConstruct>> BLIZZARD = entity(BlizzardConstruct::new, "blizzard", MobCategory.MISC, 3, 3, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<FireSigilConstruct>> FIRE_SIGIL = entity(FireSigilConstruct::new, "fire_sigil", MobCategory.MISC, 0.2f, 0.2f, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<FrostSigilConstruct>> FROST_SIGIL = entity(FrostSigilConstruct::new, "frost_sigil", MobCategory.MISC, 0.2f, 0.2f, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<LightningSigilConstruct>> LIGHTNING_SIGIL = entity(LightningSigilConstruct::new, "lightning_sigil", MobCategory.MISC, 0.2f, 0.2f, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<IceSpikeConstruct>> ICE_SPICKES = entity("ice_spikes", EntityType.Builder.<IceSpikeConstruct>of(IceSpikeConstruct::new, MobCategory.MISC).sized(0.5f, 1.0f).updateInterval(1).clientTrackingRange(160));
    public static final DeferredObject<EntityType<FireRingConstruct>> RING_OF_FIRE = entity(FireRingConstruct::new, "ring_of_fire", MobCategory.MISC, 3, 3, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<HealAuraConstruct>> HEAL_AURA = entity(HealAuraConstruct::new, "heal_aura", MobCategory.MISC, 3, 3, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<TornadoConstruct>> TORNADO = entity(TornadoConstruct::new, "tornado", MobCategory.MISC, 8, 8, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<IceBarrierConstruct>> ICE_BARRIER = entity(IceBarrierConstruct::new, "ice_barrier", MobCategory.MISC, 3, 3, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<CombustionRuneConstruct>> COMBUSTION_RUNE = entity(CombustionRuneConstruct::new, "combustion_rune", MobCategory.MISC, 2, 0.2F, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<BubbleConstruct>> BUBBLE = entity("bubble", EntityType.Builder.<BubbleConstruct>of(BubbleConstruct::new, MobCategory.MISC).sized(0.6f, 1.8f).updateInterval(3).clientTrackingRange(160));
    public static final DeferredObject<EntityType<HailstormConstruct>> HAILSTORM = entity(HailstormConstruct::new, "hailstorm", MobCategory.MISC, 5, 5, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<DecayConstruct>> DECAY = entity(DecayConstruct::new, "decay", MobCategory.MISC, 2f, 0.2f, MagicType.CONSTRUCT);
    public static final DeferredObject<EntityType<IceGiant>> ICE_GIANT = entity(IceGiant::new, "ice_giant", MobCategory.CREATURE, 1.4f, 2.9f, MagicType.LIVING);
    public static final DeferredObject<EntityType<LightningWraith>> LIGHTNING_WRAITH = entity(LightningWraith::new, "lightning_wraith", MobCategory.MONSTER, 0.6F, 1.8F, MagicType.LIVING);
    public static final DeferredObject<EntityType<IceWraith>> ICE_WRAITH = entity(IceWraith::new, "ice_wraith", MobCategory.MONSTER, 0.6F, 1.8F, MagicType.LIVING);
    public static final DeferredObject<EntityType<StormElemental>> STORM_ELEMENTAL = entity(StormElemental::new, "storm_elemental", MobCategory.MONSTER, 0.6F, 1.8F, MagicType.LIVING);
    public static final DeferredObject<EntityType<ShadowWraith>> SHADOW_WRAITH = entity(ShadowWraith::new, "shadow_wraith", MobCategory.MONSTER, 0.6F, 1.8F, MagicType.LIVING);
    public static final DeferredObject<EntityType<MagicSlime>> MAGIC_SLIME = entity(MagicSlime::new, "magic_slime", MobCategory.CREATURE, 2.04F, 2.04F, MagicType.LIVING);
    public static final DeferredObject<EntityType<Remnant>> REMNANT = entity(Remnant::new, "remnant", MobCategory.CREATURE, 0.8f, 0.8f, MagicType.LIVING);
    public static final DeferredObject<EntityType<Wizard>> WIZARD = entity(Wizard::new, "wizard", MobCategory.CREATURE, 0.6f, 1.8f, MagicType.LIVING);
    public static final DeferredObject<EntityType<EvilWizard>> EVIL_WIZARD = entity(EvilWizard::new, "evil_wizard", MobCategory.CREATURE, 0.6f, 1.8f, MagicType.LIVING);
    private EBEntities() {
    }

    // ======= Registry =======
    public static void register(RegisterFunction<EntityType<?>> function) {
        ENTITY_TYPES.forEach(((id, entityType) ->
                function.register(BuiltInRegistries.ENTITY_TYPE, WizardryMainMod.location(id), entityType.get())));
        EBEntities.registerSpawns();
    }

    public static void registerAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> consumer) {
        consumer.accept(REMNANT.get(), Remnant.createMobAttributes().build());
        consumer.accept(WIZARD.get(), AbstractWizard.createAttributes().build());
        consumer.accept(EVIL_WIZARD.get(), AbstractWizard.createAttributes().build());
        consumer.accept(MAGIC_SLIME.get(), Monster.createMonsterAttributes().build());
        consumer.accept(LIGHTNING_WRAITH.get(), Blaze.createAttributes().build());
        consumer.accept(ICE_WRAITH.get(), Blaze.createAttributes().build());
        consumer.accept(STORM_ELEMENTAL.get(), Blaze.createAttributes().build());
        consumer.accept(SHADOW_WRAITH.get(), Blaze.createAttributes().build());
        consumer.accept(ICE_GIANT.get(), IceGiant.createAttributes().build());
    }

    public static void registerSpawns() {
        SpawnPlacementsInvoker.callRegister(EBEntities.EVIL_WIZARD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EvilWizard::checkEvilWizardSpawnRules);
    }

    // ======= Helpers =======
    static <T extends Entity> DeferredObject<EntityType<T>> entity(EntityType.EntityFactory<T> factory, String name, MobCategory category, float width, float height, MagicType type) {
        return entity(name, EntityType.Builder.<T>of(factory, category).sized(width, height).clientTrackingRange(type.range).updateInterval(type.interval));
    }

    static <T extends Entity> DeferredObject<EntityType<T>> entityFireImmune(EntityType.EntityFactory<T> factory, String name, MobCategory category, float width, float height, MagicType type) {
        return entity(name, EntityType.Builder.<T>of(factory, category).fireImmune().sized(width, height).clientTrackingRange(type.range).updateInterval(type.interval));
    }

    @SuppressWarnings("unchecked")
    static <T extends Entity> DeferredObject<EntityType<T>> entity(String name, EntityType.Builder<T> builder) {
        DeferredObject<EntityType<T>> ret = new DeferredObject<>(() -> builder.build(location(name).toString()));
        ENTITY_TYPES.put(name, (DeferredObject<EntityType<? extends Entity>>) (Object) ret);
        return ret;
    }

    enum MagicType {
        LIVING(80, 3),
        PROJECTILE(64, 10),
        CONSTRUCT(160, 10);

        final int range;
        final int interval;

        MagicType(int range, int interval) {
            this.range = range;
            this.interval = interval;
        }
    }
}
