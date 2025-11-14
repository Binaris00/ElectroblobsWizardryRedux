package com.electroblob.wizardry.content.entity.living;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.core.mixin.accessor.BlazeAccessor;
import com.electroblob.wizardry.core.mixin.accessor.MobGoalsAccessor;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import java.util.EnumSet;

public class StormElemental extends Blaze {

    public StormElemental(EntityType<? extends Blaze> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;
        ((MobGoalsAccessor) this).getPathfindingMalus().remove(BlockPathTypes.WATER);
        ((MobGoalsAccessor) this).getPathfindingMalus().remove(BlockPathTypes.LAVA);
        ((MobGoalsAccessor) this).getPathfindingMalus().remove(BlockPathTypes.DANGER_FIRE);
        ((MobGoalsAccessor) this).getPathfindingMalus().remove(BlockPathTypes.DAMAGE_FIRE);
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

        if (this.tickCount % 120 == 1) {
            this.playSound(EBSounds.ENTITY_STORM_ELEMENTAL_WIND.get(), 1.0f, 1.0f);
        }

        // Ambient burn/crackle sound
        if (this.random.nextInt(24) == 0) {
            this.playSound(EBSounds.ENTITY_STORM_ELEMENTAL_BURN.get(), 1.0F + this.random.nextFloat(),
                    this.random.nextFloat() * 0.7F + 0.3F);
        }

        // Slow fall effect
        if (!this.onGround() && this.getDeltaMovement().y < 0.0D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }

        // Particle effects
        if (level().isClientSide) {
            // Large smoke particles
            for (int i = 0; i < 2; ++i) {
                level().addParticle(ParticleTypes.LARGE_SMOKE,
                        this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * (double) this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        0.0D, 0.0D, 0.0D);

                // Spark particles
                ParticleBuilder.create(EBParticles.SPARK, this).spawn(level());
            }

            // Spinning cyan sparkles around the body
            for (int i = 0; i < 10; i++) {
                float brightness = random.nextFloat() * 0.2f;
                double dy = this.random.nextDouble() * (double) this.getBbHeight();

                ParticleBuilder.create(EBParticles.SPARKLE)
                        .pos(this.getX(), this.getY() + dy, this.getZ())
                        .time(20 + random.nextInt(10))
                        .color(0, brightness, brightness)
                        .spin(0.2 + 0.5 * dy, 0.1 + 0.05 * random.nextDouble())
                        .spawn(level());
            }
        }
    }

    private void spawnParticles() {
        if (!level().isClientSide) return;

        for (int i = 0; i < 15; i++) {
            float brightness = random.nextFloat() * 0.3f;
            ParticleBuilder.create(EBParticles.SPARKLE, this)
                    .velocity(0, 0.05, 0)
                    .time(20 + random.nextInt(10))
                    .color(brightness, brightness + 0.2f, 1.0f)
                    .spawn(level());
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
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
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void thunderHit(net.minecraft.server.level.ServerLevel level, LightningBolt lightning) {
        // Immune to lightning strikes - do nothing
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals((goal) -> true);
        this.targetSelector.removeAllGoals((goal) -> true);

        this.goalSelector.addGoal(4, new StormElementalAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0F, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    static class StormElementalAttackGoal extends Goal {
        private final StormElemental elemental;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        public StormElementalAttackGoal(StormElemental elemental) {
            this.elemental = elemental;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.elemental.getTarget();
            return livingentity != null && livingentity.isAlive() && this.elemental.canAttack(livingentity);
        }

        public void start() {
            this.attackStep = 0;
        }

        public void stop() {
            ((BlazeAccessor) elemental).callSetCharged(false);
            this.lastSeen = 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            --this.attackTime;
            LivingEntity livingentity = this.elemental.getTarget();
            if (livingentity != null) {
                boolean flag = this.elemental.getSensing().hasLineOfSight(livingentity);
                if (flag) {
                    this.lastSeen = 0;
                } else {
                    ++this.lastSeen;
                }

                double d0 = this.elemental.distanceToSqr(livingentity);
                if (d0 < (double) 4.0F) {
                    if (!flag) {
                        return;
                    }

                    if (this.attackTime <= 0) {
                        this.attackTime = 20;
                        this.elemental.doHurtTarget(livingentity);
                    }

                    this.elemental.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), (double) 1.0F);
                } else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {
                    if (this.attackTime <= 0) {
                        ++this.attackStep;
                        if (this.attackStep == 1) {
                            this.attackTime = 60;
                            ((BlazeAccessor) elemental).callSetCharged(true);
                        } else if (this.attackStep <= 4) {
                            this.attackTime = 6;
                        } else {
                            this.attackTime = 100;
                            this.attackStep = 0;
                            ((BlazeAccessor) elemental).callSetCharged(false);
                        }

                        if (this.attackStep > 1) {
                            // TODO Lightning Disc Spell Casting
                            //Spells.LIGHTNING_DISC.cast(new EntityCastContext(elemental.level(), elemental, InteractionHand.MAIN_HAND, 0, livingentity, new SpellModifiers()));
                        }
                    }

                    this.elemental.getLookControl().setLookAt(livingentity, 10.0F, 10.0F);
                } else if (this.lastSeen < 5) {
                    this.elemental.getMoveControl().setWantedPosition(livingentity.getX(), livingentity.getY(), livingentity.getZ(), (double) 1.0F);
                }

                super.tick();
            }
        }

        private double getFollowDistance() {
            return this.elemental.getAttributeValue(Attributes.FOLLOW_RANGE);
        }
    }
}