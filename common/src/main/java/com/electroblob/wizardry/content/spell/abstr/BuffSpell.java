package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.*;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class BuffSpell extends Spell {

    protected final Supplier<MobEffect>[] effects;
    protected final float r, g, b;
    protected Set<MobEffect> potionSet = new java.util.HashSet<>();
    protected float particleCount = 10;

    @SafeVarargs
    public BuffSpell(float r, float g, float b, Supplier<MobEffect>... effects) {
        this.effects = effects;
        this.r = r;
        this.g = g;
        this.b = b;
        for (Supplier<MobEffect> effect : effects) {
            potionSet.add(effect.get());
        }
    }

    public static int getStandardBonusAmplifier(float potencyModifier) {
        return (int) ((potencyModifier - 1) / 0.4);
    }

    public static SpellProperty<Integer> getEffectDurationProperty(MobEffect effect) {
        return SpellProperty.intProperty(effect.getDescriptionId() + "_duration");
    }

    public static SpellProperty<Integer> getEffectStrengthProperty(MobEffect effect) {
        return SpellProperty.intProperty(effect.getDescriptionId() + "_strength");
    }

    public BuffSpell particleCount(int particleCount) {
        this.particleCount = particleCount;
        return this;
    }

    public Set<MobEffect> getPotionSet() {
        return Collections.unmodifiableSet(potionSet);
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
        if (!this.applyEffects(ctx, ctx.caster()) && !ctx.world().isClientSide) return false;
        if (ctx.world().isClientSide) this.spawnParticles(ctx.world(), ctx.caster());
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (!potionSet.isEmpty() && ctx.caster().getActiveEffectsMap().keySet().containsAll(potionSet)) return false;
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

    protected boolean applyEffects(CastContext ctx, LivingEntity caster) {
        int bonusAmplifier = getBonusAmplifier(ctx.modifiers().get(SpellModifiers.POTENCY));

        for (MobEffect effect : potionSet) {

            caster.addEffect(new MobEffectInstance(effect, effect.isInstantenous() ? 1 :
                    (int) (this.property(getEffectDurationProperty(effect)) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())),
                    this.property(getEffectStrengthProperty(effect)) + bonusAmplifier,
                    false, true));
        }

        return true;
    }

    protected void spawnParticles(Level world, LivingEntity caster) {
        for (int i = 0; i < particleCount; i++) {
            double x = caster.xo + world.random.nextDouble() * 2 - 1;
            double y = caster.yo + caster.getEyeHeight() - 0.5 + world.random.nextDouble();
            double z = caster.zo + world.random.nextDouble() * 2 - 1;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.1, 0).color(r, g, b).spawn(world);
        }
        ParticleBuilder.create(EBParticles.BUFF).entity(caster).color(r, g, b).spawn(world);
    }

    protected int getBonusAmplifier(float potencyModifier) {
        return getStandardBonusAmplifier(potencyModifier);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
