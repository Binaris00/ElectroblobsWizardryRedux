package com.electroblob.wizardry.content.entity.living;

import com.electroblob.wizardry.setup.registries.EBSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvilWizard extends AbstractWizard implements Enemy {
    public EvilWizard(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Wizard.class, true));
    }

    @Override
    public @NotNull Component getDisplayName() {
        if (this.hasCustomName()) return super.getDisplayName();
        return this.getElement().getWizardName();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return EBSounds.ENTITY_EVIL_WIZARD_AMBIENT.get();
    }

    @Override protected SoundEvent getHurtSound(@NotNull DamageSource source) { return EBSounds.ENTITY_EVIL_WIZARD_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return EBSounds.ENTITY_EVIL_WIZARD_DEATH.get(); }
}
