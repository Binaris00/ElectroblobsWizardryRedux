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
import net.minecraft.world.entity.monster.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// Damage source factory for the mod's magic damage types. Including immunity saving and lookup.
///
/// This class extends [DamageSource] to provide convenience factory methods
/// for creating magic damage sources (both direct and indirect).
///
/// Typical usage:
///
///   - [#causeMagicDamage(Entity, Entity, float, ResourceKey)] - applies damage using an appropriate direct/indirect source and making all the needed checks for you.
///   - [#causeDirectMagicDamage(Entity, ResourceKey)] - build a direct damage source.
///   - [#causeIndirectMagicDamage(Entity, Entity, ResourceKey)] - build an indirect damage source (projectile caused by an owner).
public class MagicDamageSource extends DamageSource {
    /// Construct a new magic damage source wrapper.
    ///
    /// @param damageTypeHolder Holder for the [DamageType] describing this damage.
    /// @param directEntity     The direct entity that dealt the damage (could be a projectile or other entity), or `null`.
    /// @param causingEntity    The indirect/owner entity that ultimately caused the damage (could be `null`).
    public MagicDamageSource(Holder<DamageType> damageTypeHolder, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        super(damageTypeHolder, directEntity, causingEntity);
    }

    /// A convenience method for applying magic damage to a target entity.
    ///
    /// If the caster entity has an owner (either implements [OwnableEntity] or [TraceableEntity]
    /// and returns a non-null owner), the damage is treated as indirect: the magic entity is the direct
    /// source and the owner is the indirect/causing entity. Otherwise, damage is treated as direct and the
    /// caster is used as the direct source with no indirect entity.
    ///
    /// @param caster the entity applying the damage (normally a projectile or summoned entity)
    /// @param target the entity taking the damage
    /// @param damage the amount of damage to apply
    /// @param type   the type of magic damage to apply (a [ResourceKey] from [DamageType])
    /// @return whether the target entity was damaged (result of [Entity#hurt(DamageSource, float)])
    public static boolean causeMagicDamage(Entity caster, Entity target, float damage, ResourceKey<DamageType> type) {
        Entity owner = getOwnerIfPresent(caster);
        DamageSource source = (owner != null)
                ? causeIndirectMagicDamage(caster, owner, type)
                : causeDirectMagicDamage(caster, type);
        return target.hurt(source, damage);
    }

    /// Returns the owner of `entity` if it is an [OwnableEntity] or [TraceableEntity]
    /// with a non-null owner. Otherwise, returns `null`.
    ///
    /// This helper centralizes the instanceof checks used when deciding whether damage should be
    /// considered indirect (attributed to an owner) or direct (attributed to the entity itself).
    ///
    /// @param entity Entity to inspect for an owner.
    /// @return the owner [Entity] if present, or `null` otherwise.
    @Nullable
    private static Entity getOwnerIfPresent(Entity entity) {
        if (entity instanceof OwnableEntity ownable && ownable.getOwner() != null) return ownable.getOwner();
        if (entity instanceof TraceableEntity traceable && traceable.getOwner() != null) return traceable.getOwner();
        return null;
    }

    /// Create a direct magic damage [DamageSource] where `caster` is the direct source
    /// and there is no indirect/causing entity.
    ///
    /// @param caster The entity directly dealing the damage (often the shooter or caster).
    /// @param type   The damage type resource key (from [EBDamageSources]).
    /// @return A configured [DamageSource] instance representing direct magic damage.
    public static DamageSource causeDirectMagicDamage(Entity caster, ResourceKey<DamageType> type) {
        return createMagicDamage(caster, null, type);
    }

    /// Create an indirect magic damage [DamageSource] where `magic` is the direct entity
    /// (e.g. a projectile) and `caster` is the indirect/owner entity who should be credited.
    ///
    /// @param magic  The direct magic entity (projectile/minion).
    /// @param caster The owner/indirect entity to credit for the damage.
    /// @param type   The damage type resource key.
    /// @return A configured [DamageSource] instance representing indirect magic damage.
    public static DamageSource causeIndirectMagicDamage(Entity magic, Entity caster, ResourceKey<DamageType> type) {
        return createMagicDamage(magic, caster, type);
    }

    /// Internal helper to build an [MagicDamageSource]-compatible [DamageSource].
    ///
    /// Looks up the [DamageType] holder in the level's registry and constructs a new
    /// [MagicDamageSource] (which delegates to [DamageSource] superclass).
    ///
    /// @param source   The direct source entity (maybe a projectile).
    /// @param indirect The indirect/causing entity (owner), or `null` if none.
    /// @param type     The damage type resource key.
    /// @return A new [DamageSource] describing this magic damage.
    private static DamageSource createMagicDamage(Entity source, Entity indirect, ResourceKey<DamageType> type) {
        Holder<DamageType> holder = source.level().registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(type);
        return new MagicDamageSource(holder, source, indirect);
    }
}
