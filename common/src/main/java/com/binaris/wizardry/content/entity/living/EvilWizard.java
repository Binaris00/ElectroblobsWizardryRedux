package com.binaris.wizardry.content.entity.living;

import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.entity.monster.Monster.isDarkEnoughToSpawn;

public class EvilWizard extends AbstractWizard implements Enemy {
    public EvilWizard(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    public EvilWizard(Level level) {
        this(EBEntities.EVIL_WIZARD.get(), level);
    }

    public static boolean checkEvilWizardSpawnRules(EntityType<? extends AbstractWizard> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(level, pos, random) && checkMobSpawnRules(type, level, spawnType, pos, random);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.removeAllGoals((g) -> true); // Clear existing target goals added by AbstractWizard
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Wizard.class, true));
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return EBSounds.ENTITY_EVIL_WIZARD_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return EBSounds.ENTITY_EVIL_WIZARD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return EBSounds.ENTITY_EVIL_WIZARD_DEATH.get();
    }
}
