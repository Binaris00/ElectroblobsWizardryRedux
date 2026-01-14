package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.content.entity.living.*;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Damage source factory for the mod's magic damage types. Including immunity saving and lookup.
 * <p>
 * This class extends {@link DamageSource} to provide convenience factory methods
 * for creating magic damage sources (both direct and indirect). It also keeps
 * a simple static mapping of entity classes to {@link DamageType} resource keys
 * that represent immunities; this mapping is consulted by {@link #isEntityImmune}.
 * <p>
 * Typical usage:
 * <ul>
 *     <li>{@link #causeMagicDamage(Entity, Entity, float, ResourceKey)} - applies damage using an appropriate direct/indirect source and making all the needed checks for you.</li>
 *     <li>{@link #causeDirectMagicDamage(Entity, ResourceKey)} - build a direct damage source.</li>
 *     <li>{@link #causeIndirectMagicDamage(Entity, Entity, ResourceKey)} - build an indirect damage source (projectile caused by an owner).</li>
 * </ul>
 */
public class MagicDamageSource extends DamageSource {

    /**
     * Mapping from entity implementation classes to the set of damage types
     * (resource keys) that those entities are immune to within the mod's logic.
     */
    private static final Map<Class<? extends Entity>, Set<ResourceKey<DamageType>>> IMMUNITY_MAPPING = new HashMap<>();

    // Immunity setup
    static {
        // Populate the known/implied immunities for vanilla and mod entities.
        // These entries are consulted by isEntityImmune(...)
        setEntityImmunities(Blaze.class, EBDamageSources.FIRE);
        setEntityImmunities(ZombifiedPiglin.class, EBDamageSources.FIRE, EBDamageSources.POISON);
        setEntityImmunities(MagmaCube.class, EBDamageSources.FIRE);
        setEntityImmunities(Ghast.class, EBDamageSources.FIRE);
        setEntityImmunities(EnderDragon.class, EBDamageSources.FIRE);
        setEntityImmunities(WitherBoss.class, EBDamageSources.FIRE, EBDamageSources.WITHER);
        setEntityImmunities(SnowGolem.class, EBDamageSources.FROST);
        setEntityImmunities(PolarBear.class, EBDamageSources.FROST);
        setEntityImmunities(WitherSkeleton.class, EBDamageSources.WITHER);
        setEntityImmunities(Spider.class, EBDamageSources.POISON);
        setEntityImmunities(CaveSpider.class, EBDamageSources.POISON);
        setEntityImmunities(Zombie.class, EBDamageSources.POISON);
        setEntityImmunities(Skeleton.class, EBDamageSources.POISON);
        // setEntityImmunities(Phoenix.class, DamageType.FIRE);
        setEntityImmunities(IceWraith.class, EBDamageSources.FROST);
        setEntityImmunities(IceGiant.class, EBDamageSources.FROST);
        setEntityImmunities(LightningWraith.class, EBDamageSources.SHOCK);
        setEntityImmunities(ShadowWraith.class, EBDamageSources.WITHER);
        setEntityImmunities(StormElemental.class, EBDamageSources.FIRE, EBDamageSources.SHOCK);
    }

    /**
     * Construct a new magic damage source wrapper.
     *
     * @param damageTypeHolder Holder for the {@link DamageType} describing this damage.
     * @param directEntity     The direct entity that dealt the damage (could be a projectile or other entity), or {@code null}.
     * @param causingEntity    The indirect/owner entity that ultimately caused the damage (could be {@code null}).
     */
    public MagicDamageSource(Holder<DamageType> damageTypeHolder, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        super(damageTypeHolder, directEntity, causingEntity);
    }

    /**
     * Sets the immunities for the specified entity type.
     *
     * @param entityType The class of the entity type.
     * @param immunities The damage types to which the entity type is immune.
     */
    @SafeVarargs
    public static void setEntityImmunities(Class<? extends Entity> entityType, ResourceKey<DamageType>... immunities) {
        IMMUNITY_MAPPING.computeIfAbsent(entityType, k -> new HashSet<>()).addAll(Arrays.asList(immunities));
    }

    /**
     * Checks if the given entity is immune to the specified damage type.
     *
     * @param type   The damage type to check immunity against (a resource key from {@link EBDamageSources}).
     * @param entity The entity to check for immunity.
     * @return true if the entity is immune to the specified damage type, false otherwise.
     */
    public static boolean isEntityImmune(ResourceKey<DamageType> type, Entity entity) {
        if (type == EBDamageSources.FIRE && entity.fireImmune()) return true;
        Set<ResourceKey<DamageType>> immunities = IMMUNITY_MAPPING.get(entity.getClass());
        return immunities != null && immunities.contains(type);
    }

    /**
     * A convenience method for applying magic damage to a target entity.
     * <p>
     * If the caster entity has an owner (either implements {@link OwnableEntity} or {@link TraceableEntity}
     * and returns a non-null owner), the damage is treated as indirect: the magic entity is the direct
     * source and the owner is the indirect/causing entity. Otherwise damage is treated as direct and the
     * caster is used as the direct source with no indirect entity.
     * </p>
     *
     * @param caster the entity applying the damage (normally a projectile or summoned entity)
     * @param target the entity taking the damage
     * @param damage the amount of damage to apply
     * @param type   the type of magic damage to apply (a {@link ResourceKey} from {@link DamageType})
     * @return whether the target entity was damaged (result of {@link Entity#hurt(DamageSource, float)})
     */
    public static boolean causeMagicDamage(Entity caster, Entity target, float damage, ResourceKey<DamageType> type) {
        Entity owner = getOwnerIfPresent(caster);
        DamageSource source = (owner != null)
                ? causeIndirectMagicDamage(caster, owner, type)
                : causeDirectMagicDamage(caster, type);
        return target.hurt(source, damage);
    }

    /**
     * Returns the owner of {@code entity} if it is an {@link OwnableEntity} or {@link TraceableEntity}
     * with a non-null owner. Otherwise, returns {@code null}.
     *
     * <p>
     * This helper centralises the instanceof checks used when deciding whether damage should be
     * considered indirect (attributed to an owner) or direct (attributed to the entity itself).
     * </p>
     *
     * @param entity Entity to inspect for an owner.
     * @return the owner {@link Entity} if present, or {@code null} otherwise.
     */
    @Nullable
    private static Entity getOwnerIfPresent(Entity entity) {
        if (entity instanceof OwnableEntity ownable && ownable.getOwner() != null) return ownable.getOwner();
        if (entity instanceof TraceableEntity traceable && traceable.getOwner() != null) return traceable.getOwner();
        return null;
    }

    /**
     * Create a direct magic damage {@link DamageSource} where {@code caster} is the direct source
     * and there is no indirect/causing entity.
     *
     * @param caster The entity directly dealing the damage (often the shooter or caster).
     * @param type   The damage type resource key (from {@link EBDamageSources}).
     * @return A configured {@link DamageSource} instance representing direct magic damage.
     */
    public static DamageSource causeDirectMagicDamage(Entity caster, ResourceKey<DamageType> type) {
        return createMagicDamage(caster, null, type);
    }

    /**
     * Create an indirect magic damage {@link DamageSource} where {@code magic} is the direct entity
     * (e.g. a projectile) and {@code caster} is the indirect/owner entity who should be credited.
     *
     * @param magic  The direct magic entity (projectile/minion).
     * @param caster The owner/indirect entity to credit for the damage.
     * @param type   The damage type resource key.
     * @return A configured {@link DamageSource} instance representing indirect magic damage.
     */
    public static DamageSource causeIndirectMagicDamage(Entity magic, Entity caster, ResourceKey<DamageType> type) {
        return createMagicDamage(magic, caster, type);
    }

    /**
     * Internal helper to build an {@link MagicDamageSource}-compatible {@link DamageSource}.
     *
     * <p>
     * Looks up the {@link DamageType} holder in the level's registry and constructs a new
     * {@link MagicDamageSource} (which delegates to {@link DamageSource} superclass).
     * </p>
     *
     * @param source   The direct source entity (may be a projectile).
     * @param indirect The indirect/causing entity (owner), or {@code null} if none.
     * @param type     The damage type resource key.
     * @return A new {@link DamageSource} describing this magic damage.
     */
    private static DamageSource createMagicDamage(Entity source, Entity indirect, ResourceKey<DamageType> type) {
        Holder<DamageType> holder = source.level().registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(type);
        return new MagicDamageSource(holder, source, indirect);
    }
}
