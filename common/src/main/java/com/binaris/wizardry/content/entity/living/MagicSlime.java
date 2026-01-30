package com.binaris.wizardry.content.entity.living;

import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagicSlime extends Slime {
    public MagicSlime(EntityType<? extends Slime> entityType, Level level) {
        super(entityType, level);
        this.setSize(2, false);
    }

    public MagicSlime(Level world, LivingEntity target) {
        super(EBEntities.MAGIC_SLIME.get(), world);
        this.setPos(target.xo, target.yo, target.zo);
        this.startRiding(target);
        this.setSize(2, false);
    }

    @Override
    protected void registerGoals() {
        // No goals
    }

    @Override
    public void tick() {
        super.tick();

        // Damages and slows the slime's victim or makes the slime explode if the victim is dead.
        if (this.getVehicle() != null && this.getVehicle() instanceof LivingEntity livingEntity && livingEntity.getHealth() > 0) {
            if (this.tickCount % 16 == 1) {
                this.getVehicle().hurt(damageSources().magic(), 1);
                if (this.getVehicle() != null) { // Some mobs force-dismount when attacked (normally when dying)
                    if (!level().isClientSide)
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2));
                }
                this.playSound(EBSounds.ENTITY_MAGIC_SLIME_ATTACK.get(), 1.0f, 1.0f);
            }
        } else {
            this.kill();
        }
    }

    @Override
    public void kill() {
        this.setHealth(0);
        this.setSize(1, false); // avoid duplication!!
        // Bursting effect
        for (int i = 0; i < 5; i++) {
            double x = this.xo - 0.5 + random.nextDouble();
            double y = this.yo - 0.5 + random.nextDouble();
            double z = this.zo - 0.5 + random.nextDouble();
            this.level().addParticle(ParticleTypes.ITEM_SLIME, x, y, z, (x - this.xo) * 2, (y - this.yo) * 2,
                    (z - this.zo) * 2);
        }
        this.playSound(EBSounds.ENTITY_MAGIC_SLIME_SPLAT.get(), 0.5f, 0.6f);
        this.playSound(EBSounds.ENTITY_MAGIC_SLIME_EXPLODE.get(), 0.5f, 0.5f);

        super.kill();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.setSize(2, false);
        return data;
    }

}
