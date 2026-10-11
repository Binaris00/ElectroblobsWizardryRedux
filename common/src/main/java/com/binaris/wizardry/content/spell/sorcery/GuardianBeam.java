package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GuardianBeam extends RaySpell {
    private static final SpellProperty<Integer> AIR_DEPLETION = SpellProperty.intProperty("air_depletion");

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        if (ticksInUse % 50 == 1) super.playSound(world, x, y, z, ticksInUse, duration);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity livingEntity) {

            if (ctx.castingTicks() % 50 == 1) {
                EntityUtil.attackEntityWithoutKnockback(livingEntity,
                        MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.MAGIC),
                        property(DefaultProperties.DAMAGE) * ctx.modifiers().getFactor(SpellModifiers.POTENCY));

                if (!livingEntity.canBreatheUnderwater() && !livingEntity.hasEffect(MobEffects.WATER_BREATHING)) {
                    livingEntity.setAirSupply(Math.max(-20, livingEntity.getAirSupply() - property(AIR_DEPLETION)));
                }

                if (!ctx.world().isClientSide) {
                    this.applyElderGuardianCurse(ctx, livingEntity, origin);
                }
            }

            if (ctx.world().isClientSide) {

                float t = (ctx.castingTicks() % 50) / 50f;
                float yellowness = t * t;
                int r = 64 + (int) (yellowness * 191.0F);
                int g = 32 + (int) (yellowness * 191.0F);
                int b = 128 - (int) (yellowness * 64.0F);

                if (ctx.castingTicks() % 3 == 0) ParticleBuilder.create(EBParticles.GUARDIAN_BEAM).entity(ctx.caster())
                        .pos(ctx.caster() != null ? origin.subtract(ctx.caster().position()) : origin).target(livingEntity)
                        .color(r, g, b).spawn(ctx.world());

                Vec3 direction = livingEntity.getBoundingBox().getCenter().subtract(origin);
                Vec3 pos = origin.add(direction.scale(ctx.world().random.nextFloat()));
                ParticleBuilder.create(EBParticles.MAGIC_BUBBLE, ctx.world().random, pos.x, pos.y, pos.z, 0.15, false).spawn(ctx.world());
            }
        }

        return true;
    }

    private void applyElderGuardianCurse(CastContext ctx, LivingEntity target, Vec3 origin) {
        Vec3 source = ctx.caster() != null ? ctx.caster().position() : origin;
        Vec3 direction = target.position().subtract(source);

        if (direction.lengthSqr() > 1.0E-4) {
            direction = direction.normalize();
            float knockback = property(DefaultProperties.KNOCKBACK);

            target.setDeltaMovement(direction.x * knockback, 0.2, direction.z * knockback);
            target.hasImpulse = true;

            if (target instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetEntityMotionPacket(target));
            }
        }

        ctx.world().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 1.0F);

        target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,
                property(DefaultProperties.EFFECT_DURATION), property(DefaultProperties.EFFECT_STRENGTH)));
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellTypes.ATTACK, SpellAction.POINT, 15, 0, 50)
                .add(DefaultProperties.RANGE, 16f)
                .add(DefaultProperties.DAMAGE, 5f)
                .add(DefaultProperties.KNOCKBACK, 0.6f)
                .add(DefaultProperties.EFFECT_DURATION, 60)
                .add(DefaultProperties.EFFECT_STRENGTH, 2)
                .add(AIR_DEPLETION, 70)
                .build();
    }
}
