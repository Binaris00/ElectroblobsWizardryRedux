package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
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

public class EBMagicDamageSource extends DamageSource {
    private static final Map<Class<? extends Entity>, Set<ResourceKey<DamageType>>> immunityMapping = new HashMap<>();
    public static final String DIRECT_MAGIC_DAMAGE = "wizardry_magic";
    public static final String INDIRECT_MAGIC_DAMAGE = "indirect_wizardry_magic";
    private final boolean isRetaliatory;

    public EBMagicDamageSource(Holder<DamageType> damageTypeHolder, @Nullable Entity directEntity, @Nullable Entity causingEntity, boolean isRetaliatory) {
        super(damageTypeHolder, directEntity, causingEntity);
        this.isRetaliatory = isRetaliatory;
//        if (type == DamageType.FIRE) this.setIsFire();
//        if (type == DamageType.BLAST) this.setExplosion();
    }


    // Immunity
    static {
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

        //setEntityImmunities(Phoenix.class, DamageType.FIRE);
        //setEntityImmunities(ZombieMinion.class, DamageType.POISON);
        //setEntityImmunities(EntitySkeletonMinion.class, DamageType.POISON);
        //setEntityImmunities(EntitySpiderMinion.class, DamageType.POISON);
        //setEntityImmunities(IceWraith.class, DamageType.FROST);
        //setEntityImmunities(EntityIceGiant.class, DamageType.FROST);
        //setEntityImmunities(EntityLightningWraith.class, DamageType.SHOCK);
        //setEntityImmunities(EntityShadowWraith.class, DamageType.WITHER);
        //setEntityImmunities(StormElemental.class, DamageType.FIRE, DamageType.SHOCK);
        //setEntityImmunities(BlazeMinion.class, DamageType.FIRE);
    }


    @SafeVarargs
    public static void setEntityImmunities(Class<? extends Entity> entityType, ResourceKey<DamageType>... immunities) {
        immunityMapping.computeIfAbsent(entityType, k -> new HashSet<>()).addAll(Arrays.asList(immunities));
    }

    public static boolean isEntityImmune(ResourceKey<DamageType> type, Entity entity) {
        if (type == EBDamageSources.FIRE && entity.fireImmune()) return true;
        Set<ResourceKey<DamageType>> immunities = immunityMapping.get(entity.getClass());

        return immunities != null && immunities.contains(type);
    }

    /**
     * A convenience method for applying magic damage to a target entity.
     * If the caster entity has an owner, the damage is indirect and the owner is considered the indirect source of the damage.
     * Otherwise, the damage is direct and the caster is considered the source of the damage.
     * (Normally {@param caster} is a projectile)
     *
     * @param caster the entity applying the damage
     * @param target the entity taking the damage
     * @param damage the amount of damage to apply
     * @param type the type of magic damage to apply
     * @param isRetaliatory whether this damage is retaliatory (i.e. the target is attacking the caster)
     * @return whether the target entity was damaged
     */
    public static boolean causeMagicDamage(Entity caster, Entity target, float damage, ResourceKey<DamageType> type, boolean isRetaliatory){
        if ((caster instanceof OwnableEntity ownableCaster && ownableCaster.getOwner() != null) || (caster instanceof TraceableEntity traceableCaster && traceableCaster.getOwner() != null)) {
            Entity owner = (caster instanceof OwnableEntity) ? ((OwnableEntity) caster).getOwner() : ((TraceableEntity) caster).getOwner();

            DamageSource source = EBMagicDamageSource.causeIndirectMagicDamage(caster, owner, type, isRetaliatory);
            return target.hurt(source, damage);
        } else {
            DamageSource source = EBMagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.SORCERY, isRetaliatory);
            return target.hurt(source, damage);
        }
    }

    // ===============================================

    public static DamageSource causeDirectMagicDamage(Entity caster, ResourceKey<DamageType> type, boolean isRetaliatory) {
        return createMagicDamage(caster, null, type, isRetaliatory);
    }

    public static DamageSource causeIndirectMagicDamage(Entity magic, Entity caster, ResourceKey<DamageType> type, boolean isRetaliatory) {
        return createMagicDamage(magic, caster, type, isRetaliatory);
    }

    public static DamageSource causeDirectMagicDamage(Entity caster, ResourceKey<DamageType> type) {
        return createMagicDamage(caster, null, type, false);
    }

    public static DamageSource causeIndirectMagicDamage(Entity magic, Entity caster, ResourceKey<DamageType> type) {
        return createMagicDamage(magic, caster, type, false);
    }

    private static DamageSource createMagicDamage(Entity source, Entity indirect, ResourceKey<DamageType> type, boolean isRetaliatory) {
        return new EBMagicDamageSource(source.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(type), source, indirect, isRetaliatory);
    }

    // Component
    public static String getIndirectDamageNameForType(DamageType type) {
        return EBConfig.damageTypePerElement ? type.msgId().toLowerCase() + "_" + INDIRECT_MAGIC_DAMAGE : INDIRECT_MAGIC_DAMAGE;
    }

    public static String getDirectDamageNameForType(DamageType type) {
        return EBConfig.damageTypePerElement ? type.msgId().toLowerCase() + "_" + DIRECT_MAGIC_DAMAGE : DIRECT_MAGIC_DAMAGE;
    }
}
