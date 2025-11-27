package com.electroblob.wizardry.content.entity.living;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.mixin.accessor.BlazeAccessor;
import com.electroblob.wizardry.core.mixin.accessor.MobGoalsAccessor;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class ShadowWraith extends Blaze {

    public ShadowWraith(EntityType<? extends Blaze> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;
        ((MobGoalsAccessor) this).getPathfindingMalus().remove(BlockPathTypes.WATER);
        ((MobGoalsAccessor) this).getPathfindingMalus().remove(BlockPathTypes.LAVA);
        ((MobGoalsAccessor) this).getPathfindingMalus().remove(BlockPathTypes.DANGER_FIRE);
        ((MobGoalsAccessor) this).getPathfindingMalus().remove(BlockPathTypes.DAMAGE_FIRE);
    }

    public ShadowWraith(Level level) {
        this(EBEntities.SHADOW_WRAITH.get(), level);
    }

    @Override
    public void spawnAnim() {
        super.spawnAnim();
        spawnParticles();
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (this.deathTime == 1) this.spawnParticles();
    }

    @Override
    public void tick() {
        super.tick();

        // Slow fall effect
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }

        // Ambient sound
        if (this.random.nextInt(24) == 0) {
             this.playSound(EBSounds.ENTITY_SHADOW_WRAITH_NOISE.get(), 1.0F + this.random.nextFloat(),
                           this.random.nextFloat() * 0.7F + 0.3F);
        }

        // Particle effects
        if (level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                // Portal particles
                level().addParticle(ParticleTypes.PORTAL,
                        this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        0.0D, 0.0D, 0.0D);

                // Large smoke particles
                level().addParticle(ParticleTypes.LARGE_SMOKE,
                        this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        0.0D, 0.0D, 0.0D);

                // Purple sparkles
                float brightness = random.nextFloat() * 0.2f;
                ParticleBuilder.create(EBParticles.SPARKLE, this)
                        .velocity(0, 0.05, 0)
                        .time(20 + random.nextInt(10))
                        .color(brightness, 0.0f, brightness)
                        .spawn(level());

                // Dark magic particles
                ParticleBuilder.create(EBParticles.DARK_MAGIC, this)
                        .color(0.1f, 0.0f, 0.0f)
                        .spawn(level());
            }
        }
    }

    private void spawnParticles() {
        if (!level().isClientSide) return;

        for (int i = 0; i < 15; i++) {
            float brightness = random.nextFloat() * 0.4f;
            ParticleBuilder.create(EBParticles.SPARKLE, this)
                    .velocity(0, 0.05, 0)
                    .time(20 + random.nextInt(10))
                    .color(brightness, 0.0f, brightness)
                    .spawn(level());
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        // Immune to wither
        if (effect.getEffect() == MobEffects.WITHER) {
            return false;
        }
        return super.canBeAffected(effect);
    }

//    @Override
//    public int getSkyLightLevel() {
//        return 15; // Maximum brightness
//    }
//
//    @Override
//    public float getBrightness() {
//        return 1.0F;
//    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals((goal) -> true);
        this.targetSelector.removeAllGoals((goal) -> true);

        this.goalSelector.addGoal(4, new ShadowWraithAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0F, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    static class ShadowWraithAttackGoal extends Goal {
        private final ShadowWraith wraith;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public ShadowWraithAttackGoal(ShadowWraith wraith) {
            this.wraith = wraith;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.wraith.getTarget();
            return livingentity != null && livingentity.isAlive() && this.wraith.canAttack(livingentity);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
            ((BlazeAccessor) wraith).callSetCharged(false);
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            --this.attackTime;
            LivingEntity livingentity = this.wraith.getTarget();
            if (livingentity != null) {
                boolean flag = this.wraith.getSensing().hasLineOfSight(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.wraith.distanceToSqr(livingentity);
                if (d0 < (double) 4.0F) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.wraith.doHurtTarget(livingentity);
                    }

                    this.wraith.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), (double) 1.0F);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                            ((BlazeAccessor) wraith).callSetCharged(true);
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                            ((BlazeAccessor) wraith).callSetCharged(false);
                        }

                        if (this.attackStep > 1) {
                            Spells.DARKNESS_ORB.cast(new EntityCastContext(wraith.level(), wraith, InteractionHand.MAIN_HAND, 0, livingentity, new SpellModifiers()));
                        }
                    }

                    this.wraith.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.wraith.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), (double) 1.0F);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.wraith.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}