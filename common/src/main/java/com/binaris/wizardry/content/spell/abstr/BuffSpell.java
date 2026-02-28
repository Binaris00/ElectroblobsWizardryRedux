package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.*;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.content.spell.healing.Heal;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Spell that applies mob effects to the caster. Can be cast by mobs, entities and if cast by location, it will
 * apply the effects to the nearest entity. If the caster/selected entity already has all the effects, the spell will
 * not be cast.
 * <p>
 * With this you can handle easy property handling for mob effect properties, as well as standard bonus amplifier
 * calculation based on potency modifier. You can also easily add particles and sounds to the spell by overriding the
 * respective methods.
 * <p>
 * Check {@link Spells#AGILITY} and {@link Heal} for some examples of this spell.
 * <p>
 * You must override the {@link #properties()} to return an actual instance of {@link SpellProperties} for this spell or
 * use {@link Spell#assignProperties(SpellProperties)}, otherwise the spell will have no properties and may not function
 * as intended.
 */
public class BuffSpell extends Spell {
    /** color of the particles spawned when the spell is cast. */
    protected final float r, g, b;
    /** set of mob effects applied by this spell. */
    protected Set<MobEffect> mobEffects = new java.util.HashSet<>();
    /** amount of particles spawned when the spell is cast. */
    protected float particleCount = 10;

    @SafeVarargs
    public BuffSpell(float r, float g, float b, Supplier<MobEffect>... effects) {
        this.r = r;
        this.g = g;
        this.b = b;
        Arrays.stream(effects).forEach(effect -> mobEffects.add(effect.get()));
    }

    /**
     * Sets the amount of particles spawned when the spell is cast. Default is 10.
     *
     * @param particleCount the amount of particles spawned when the spell is cast
     * @return this spell, for chaining
     */
    public BuffSpell particleCount(int particleCount) {
        this.particleCount = particleCount;
        return this;
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    @Override
    public boolean canCastByLocation() {
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!this.applyEffects(ctx, ctx.caster())) return false;
        if (ctx.world().isClientSide) this.spawnParticles(ctx.world(), ctx.caster());
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (!mobEffects.isEmpty() && ctx.caster().getActiveEffectsMap().keySet().containsAll(getMobEffects()))
            return false;
        if (!this.applyEffects(ctx, ctx.caster()) && !ctx.world().isClientSide) return false;
        if (ctx.world().isClientSide) this.spawnParticles(ctx.world(), ctx.caster());
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        AABB boundingBox = new AABB(ctx.pos());
        List<LivingEntity> entities = ctx.world().getEntitiesOfClass(LivingEntity.class, boundingBox);

        float distance = -1;
        LivingEntity nearestEntity = null;
        for (LivingEntity entity : entities) {
            float newDistance = (float) entity.distanceToSqr(ctx.x(), ctx.y(), ctx.z());
            if (distance == -1 || newDistance < distance) {
                distance = newDistance;
                nearestEntity = entity;
            }
        }

        if (nearestEntity == null) return false;
        if (!this.applyEffects(ctx, nearestEntity) && !ctx.world().isClientSide) return false;
        if (ctx.world().isClientSide) this.spawnParticles(ctx.world(), nearestEntity);

        this.playSound(ctx.world(), ctx.x() - ctx.direction().getStepX(),
                ctx.y() - ctx.direction().getStepY(), ctx.z() - ctx.direction().getStepZ(),
                ctx.castingTicks(), ctx.duration());

        return true;
    }

    /**
     * Applies the mob effects to the target. The duration and amplifier of the effects are calculated based on the
     * spell properties and the potency and duration modifiers of the cast context.
     *
     * @param ctx    the cast context of the spell, used to get the modifiers for duration and potency, as well as the world
     *               for applying the effects.
     * @param target the target of the spell, the entity that is going to receive the mob effects. Normally the caster,
     *               but if the spell is cast by location, it will be the nearest entity to the selected location.
     * @return true if the effects were applied, false if the target already had all the effects or if there was an error
     * applying the effects.
     */
    protected boolean applyEffects(CastContext ctx, LivingEntity target) {
        int bonusAmplifier = getStandardBonusAmplifier(ctx.modifiers().get(SpellModifiers.POTENCY));

        for (MobEffect effect : mobEffects) {
            if (ctx.world().isClientSide) continue;
            target.addEffect(new MobEffectInstance(effect, effect.isInstantenous() ? 1 :
                    (int) (this.property(getEffectDurationProperty(effect)) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())),
                    this.property(getEffectStrengthProperty(effect)) + bonusAmplifier,
                    false, true));
        }

        return true;
    }

    /**
     * Spawns particles when the spell is cast. By default, it spawns some sparkle particles around the target and a buff
     * particle on the target. You can override this method to spawn different particles or to change the way the particles
     * are spawned. The color of the particles is determined by the r,g,b fields of the spell, which are set in the
     * constructor. The amount of particles spawned is determined by the particleCount field, which can be set using the
     * particleCount() method.
     * <p>
     * Note that this method is only called on the client side.
     *
     * @param world  the world where the particles are going to be spawned, used to spawn the particles.
     * @param target the entity that is going to be the center of the particles, normally the caster of the spell, but
     *               if the spell is cast by location, it will be the nearest entity to the selected location.
     */
    protected void spawnParticles(Level world, LivingEntity target) {
        for (int i = 0; i < particleCount; i++) {
            double x = target.xo + world.random.nextDouble() * 2 - 1;
            double y = target.yo + target.getEyeHeight() - 0.5 + world.random.nextDouble();
            double z = target.zo + world.random.nextDouble() * 2 - 1;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.1, 0).color(r, g, b).spawn(world);
        }

        ParticleBuilder.create(EBParticles.BUFF).entity(target).color(r, g, b).spawn(world);
    }

    /**
     * Calculates the standard bonus amplifier based on the potency modifier. The formula is (potencyModifier - 1) / 0.4,
     * which means that for every 0.4 increase in potency modifier, the amplifier increases by 1. For example, if the
     * potency modifier is 1.4, the bonus amplifier will be 1, if the potency modifier is 1.8, the bonus amplifier will
     * be 2, and so on.
     *
     * @param potencyModifier the potency modifier of the cast context, used to calculate the bonus amplifier.
     * @return the bonus amplifier calculated based on the potency modifier.
     */
    public static int getStandardBonusAmplifier(float potencyModifier) {
        return (int) ((potencyModifier - 1) / 0.4);
    }

    /**
     * Utility method to get the spell property for the duration of a mob effect. The property name is the description
     * id of the effect followed by "_duration". For example, if the effect is "minecraft:speed", the property name will
     * be 'effect.minecraft.speed_duration'.
     * <p>
     * Used to easily get the properties for the duration of the mob effects applied by this spell. You
     * must use this method to get the properties for the duration of the mob effects.
     *
     * @param effect the mob effect for which to get the duration property, used to generate the property name.
     * @return the spell property for the duration of the given mob effect.
     */
    public static SpellProperty<Integer> getEffectDurationProperty(MobEffect effect) {
        return SpellProperty.intProperty(effect.getDescriptionId() + "_duration");
    }

    /**
     * Utility method to get the spell property for the strength of a mob effect. The property name is the description
     * id of the effect followed by "_strength". For example, if the effect is "minecraft:speed", the property name will
     * be 'effect.minecraft.speed_strength'.
     * <p>
     * Used to easily get the properties for the strength of the mob effects applied by this spell. You
     * must use this method to get the properties for the strength of the mob effects.
     *
     * @param effect the mob effect for which to get the strength property, used to generate the property name.
     * @return the spell property for the strength of the given mob effect.
     */
    public static SpellProperty<Integer> getEffectStrengthProperty(MobEffect effect) {
        return SpellProperty.intProperty(effect.getDescriptionId() + "_strength");
    }

    /**
     * Gets the set of mob effects applied by this spell. Used to check if the target already has all the effects before
     * applying them.
     *
     * @return the set of mob effects applied by this spell.
     */
    public Set<MobEffect> getMobEffects() {
        return mobEffects;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
