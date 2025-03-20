package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.common.DeferredObject;
import com.electroblob.wizardry.common.content.entity.EntityArrowRain;
import com.electroblob.wizardry.common.content.entity.EntityMeteor;
import com.electroblob.wizardry.common.content.entity.construct.*;
import com.electroblob.wizardry.common.content.entity.projectile.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.electroblob.wizardry.WizardryMainMod.location;
import static com.electroblob.wizardry.setup.registries.EBEntities.Register.entity;

public final class EBEntities {

    public static final DeferredObject<EntityType<Dart>> DART;
    public static final DeferredObject<EntityType<ConjuredArrow>> CONJURED_ARROW;
    public static final DeferredObject<EntityType<FireBolt>> FIRE_BOLT;
    public static final DeferredObject<EntityType<FireBomb>> FIRE_BOMB;
    public static final DeferredObject<EntityType<FlamecatcherArrow>> FLAME_CATCHER_ARROW;
    public static final DeferredObject<EntityType<ForceArrow>> FORCE_ARROW;
    public static final DeferredObject<EntityType<IceBall>> ICE_BALL;
    public static final DeferredObject<EntityType<IceLance>> ICE_LANCE;
    public static final DeferredObject<EntityType<IceShard>> ICE_SHARD;
    public static final DeferredObject<EntityType<LightningArrow>> LIGHTNING_ARROW;
    public static final DeferredObject<EntityType<MagicFireball>> MAGIC_FIREBALL;
    public static final DeferredObject<EntityType<MagicMissile>> MAGIC_MISSILE;
    public static final DeferredObject<EntityType<PoisonBomb>> POISON_BOMB;
    public static final DeferredObject<EntityType<SmokeBomb>> SMOKE_BOMB;
    public static final DeferredObject<EntityType<Spark>> SPARK;
    public static final DeferredObject<EntityType<SparkBomb>> SPARK_BOMB;
    public static final DeferredObject<EntityType<Thunderbolt>> THUNDERBOLT;
    public static final DeferredObject<EntityType<IceCharge>> ICE_CHARGE;
    public static final DeferredObject<EntityType<DarknessOrb>> DARKNESS_ORB;
    public static final DeferredObject<EntityType<EntityMeteor>> METEOR;
    public static final DeferredObject<EntityType<EntityArrowRain>> ARROW_RAIN;
    public static final DeferredObject<EntityType<EntityForceOrb>> FORCE_ORB;
    public static final DeferredObject<EntityType<EntityBlizzard>> BLIZZARD;
    public static final DeferredObject<EntityType<EntityFireSigil>> FIRE_SIGIL;
    public static final DeferredObject<EntityType<EntityFrostSigil>> FROST_SIGIL;
    public static final DeferredObject<EntityType<EntityLightningSigil>> LIGHTNING_SIGIL;
    public static final DeferredObject<EntityType<EntityIceSpike>> ICE_SPICKES;
    public static final DeferredObject<EntityType<EntityFireRing>> RING_OF_FIRE;
    public static final DeferredObject<EntityType<EntityHealAura>> HEAL_AURA;
    public static final DeferredObject<EntityType<EntityTornado>> TORNADO;
    public static final DeferredObject<EntityType<IceBarrierConstruct>> ICE_BARRIER;
    public static final DeferredObject<EntityType<CombustionRuneConstruct>> COMBUSTION_RUNE;
    public static final DeferredObject<EntityType<BubbleConstruct>> BUBBLE;
    public static final DeferredObject<EntityType<HailstormConstruct>> HAILSTORM;
    public static final DeferredObject<EntityType<DecayConstruct>> DECAY;

    static {
        DART = entity(
                "dart",
                EntityType.Builder.<Dart>of(Dart::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(4)
                        .updateInterval(20)
        );


        ICE_SPICKES = entity(
                "ice_spikes",
                EntityType.Builder.<EntityIceSpike>of(EntityIceSpike::new, MobCategory.MISC)
                        .sized(0.5f, 1.0f)
                        .updateInterval(1)
                        .clientTrackingRange(160)
        );

        ARROW_RAIN = entity(
                "arrow_rain",
                EntityType.Builder.<EntityArrowRain>of(EntityArrowRain::new, MobCategory.MISC)
                        .sized(3, 3)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        TORNADO = entity(
                "tornado",
                EntityType.Builder.<EntityTornado>of(EntityTornado::new, MobCategory.MISC)
                        .sized(8, 8)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        BUBBLE = entity(
                "bubble",
                EntityType.Builder.<BubbleConstruct>of(BubbleConstruct::new, MobCategory.MISC)
                        .sized(0.6f, 1.8f)
                        .updateInterval(3)
                        .clientTrackingRange(160)
        );

        BLIZZARD = entity(
                "blizzard",
                EntityType.Builder.<EntityBlizzard>of(EntityBlizzard::new, MobCategory.MISC)
                        .sized(3, 3)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        HAILSTORM = entity(
                "hailstorm",
                EntityType.Builder.<HailstormConstruct>of(HailstormConstruct::new, MobCategory.MISC)
                        .sized(5, 5)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        DECAY = entity(
                "decay",
                EntityType.Builder.<DecayConstruct>of(DecayConstruct::new, MobCategory.MISC)
                        .sized(2f, 0.2f)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        ICE_BARRIER = entity(
                "ice_barrier",
                EntityType.Builder.<IceBarrierConstruct>of(IceBarrierConstruct::new, MobCategory.MISC)
                        .sized(3, 3)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        COMBUSTION_RUNE = entity(
                "combustion_rune",
                EntityType.Builder.<CombustionRuneConstruct>of(CombustionRuneConstruct::new, MobCategory.MISC)
                        .sized(2, 0.2F)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        HEAL_AURA = entity(
                "heal_aura",
                EntityType.Builder.<EntityHealAura>of(EntityHealAura::new, MobCategory.MISC)
                        .sized(3, 3)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        RING_OF_FIRE = entity(
                "ring_of_fire",
                EntityType.Builder.<EntityFireRing>of(EntityFireRing::new, MobCategory.MISC)
                        .sized(3, 3)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        FIRE_SIGIL = entity(
                "fire_sigil",
                EntityType.Builder.<EntityFireSigil>of(EntityFireSigil::new, MobCategory.MISC)
                        .sized(0.2f, 0.2f)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        FROST_SIGIL = entity(
                "frost_sigil",
                EntityType.Builder.<EntityFrostSigil>of(EntityFrostSigil::new, MobCategory.MISC)
                        .sized(0.2f, 0.2f)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        LIGHTNING_SIGIL = entity(
                "lightning_sigil",
                EntityType.Builder.<EntityLightningSigil>of(EntityLightningSigil::new, MobCategory.MISC)
                        .sized(0.2f, 0.2f)
                        .updateInterval(10)
                        .clientTrackingRange(160)
        );

        FORCE_ORB = entity(
                "force_orb",
                EntityType.Builder.<EntityForceOrb>of(EntityForceOrb::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        DARKNESS_ORB = entity(
                "darkness_orb",
                EntityType.Builder.<DarknessOrb>of(DarknessOrb::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        METEOR = entity(
                "meteor",
                EntityType.Builder.<EntityMeteor>of(EntityMeteor::new, MobCategory.MISC)
                        .sized(0.98f, 0.98f)
                        .clientTrackingRange(160)
                        .updateInterval(3)
        );

        CONJURED_ARROW = entity(
                "conjured_arrow",
                EntityType.Builder.<ConjuredArrow>of(ConjuredArrow::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        FIRE_BOLT = entity(
                "fire_bolt",
                EntityType.Builder.<FireBolt>of(FireBolt::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        FIRE_BOMB = entity(
                "fire_bomb",
                EntityType.Builder.<FireBomb>of(FireBomb::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        FLAME_CATCHER_ARROW = entity(
                "flamecatcher_arrow",
                EntityType.Builder.<FlamecatcherArrow>of(FlamecatcherArrow::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        FORCE_ARROW = entity(
                "force_arrow",
                EntityType.Builder.<ForceArrow>of(ForceArrow::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        ICE_BALL = entity(
                "iceball",
                EntityType.Builder.<IceBall>of(IceBall::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        ICE_LANCE = entity(
                "ice_lance",
                EntityType.Builder.<IceLance>of(IceLance::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        ICE_SHARD = entity(
                "ice_shard",
                EntityType.Builder.<IceShard>of(IceShard::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        LIGHTNING_ARROW = entity(
                "lightning_arrow",
                EntityType.Builder.<LightningArrow>of(LightningArrow::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        MAGIC_FIREBALL = entity(
                "magic_fireball",
                EntityType.Builder.<MagicFireball>of(MagicFireball::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        MAGIC_MISSILE = entity(
                "magic_missile",
                EntityType.Builder.<MagicMissile>of(MagicMissile::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        POISON_BOMB = entity(
                "poison_bomb",
                EntityType.Builder.<PoisonBomb>of(PoisonBomb::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        SMOKE_BOMB = entity(
                "smoke_bomb",
                EntityType.Builder.<SmokeBomb>of(SmokeBomb::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        SPARK = entity(
                "spark",
                EntityType.Builder.<Spark>of(Spark::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        SPARK_BOMB = entity(
                "spark_bomb",
                EntityType.Builder.<SparkBomb>of(SparkBomb::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        THUNDERBOLT = entity(
                "thunderbolt",
                EntityType.Builder.<Thunderbolt>of(Thunderbolt::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );

        ICE_CHARGE = entity(
                "ice_charge",
                EntityType.Builder.<IceCharge>of(IceCharge::new, MobCategory.MISC)
                        .sized(0.25f, 0.25f)
                        .clientTrackingRange(64)
                        .updateInterval(10)
        );
    }

    static void handleRegistration(Consumer<Map<String, DeferredObject<EntityType<? extends Entity>>>> handler) {
        handler.accept(Register.ENTITY_TYPES);
    }

    static class Register {

        static Map<String, DeferredObject<EntityType<? extends Entity>>> ENTITY_TYPES = new HashMap<>();

        @SuppressWarnings("unchecked")
        static <T extends Entity> DeferredObject<EntityType<T>> entity(final String name, EntityType.Builder<T> builder) {
            DeferredObject<EntityType<T>> ret = new DeferredObject<>(() -> builder.build(location(name).toString()));
            ENTITY_TYPES.put(name, (DeferredObject<EntityType<? extends Entity>>) (Object) ret); // Suppress unchecked warning
            return ret;
        }

    }


    private EBEntities() {}
}
