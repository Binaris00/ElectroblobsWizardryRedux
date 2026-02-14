package com.binaris.wizardry.cca.entity;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.cca.EBComponents;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MinionDataHolder implements MinionData, ComponentV3, AutoSyncedComponent {
    Mob provider;
    private int lifetime = -1;
    private boolean summoned = false;
    private boolean shouldDeleteGoals = false;
    private UUID ownerUUID = null;
    private boolean shouldFollowOwner = false;
    private boolean restartGoals;

    public MinionDataHolder(Mob provider) {
        this.provider = provider;
    }

    public void sync() {
        EBComponents.MINION_DATA.sync(provider);
    }

    @Override
    public Mob getProvider() {
        return this.provider;
    }

    @Override
    public void tick() {
        if (!summoned) return;

        if (goalRestart()) {
            updateGoals();
            markGoalRestart(false);
        }

        if (provider.tickCount > this.getLifetime() && this.getLifetime() > 0) {
            this.provider.discard();
        }

        if (provider.level().isClientSide && provider.level().random.nextInt(8) == 0) {
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(provider.xo, provider.yo + (provider.level().random.nextDouble() * 1.5 + 1), provider.zo).color(0.1f, 0.0f, 0.0f).spawn(provider.level());
        }

        sync();
    }

    @Override
    public int getLifetime() {
        return lifetime;
    }

    @Override
    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
        sync();
    }

    @Override
    public boolean isSummoned() {
        return summoned;
    }

    @Override
    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
        sync();
    }

    @Override
    public boolean shouldDeleteGoals() {
        return shouldDeleteGoals;
    }

    @Override
    public void setShouldDeleteGoals(boolean shouldDeleteGoals) {
        this.shouldDeleteGoals = shouldDeleteGoals;
    }

    @Override
    public boolean shouldFollowOwner() {
        return shouldFollowOwner;
    }

    @Override
    public void setShouldFollowOwner(boolean shouldFollowOwner) {
        this.shouldFollowOwner = shouldFollowOwner;
    }

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        sync();
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        if (ownerUUID == null) return null;
        if (provider.level().isClientSide()) return null;

        return provider.getServer().getLevel(provider.level().dimension()).getEntity(ownerUUID) instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    public void setOwner(LivingEntity owner) {
        this.ownerUUID = owner.getUUID();
        sync();
    }

    @Override
    public void markGoalRestart(boolean shouldRestartGoals) {
        this.restartGoals = shouldRestartGoals;
    }

    @Override
    public boolean goalRestart() {
        return restartGoals;
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        this.lifetime = tag.getInt("lifetime");
        this.summoned = tag.getBoolean("summoned");
        this.shouldDeleteGoals = tag.getBoolean("shouldDeleteGoals");
        this.shouldFollowOwner = tag.getBoolean("shouldFollowOwner");
        if (tag.contains("ownerUUID")) {
            this.ownerUUID = tag.getUUID("ownerUUID");
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("lifetime", lifetime);
        tag.putBoolean("summoned", summoned);
        tag.putBoolean("shouldDeleteGoals", shouldDeleteGoals);
        tag.putBoolean("shouldFollowOwner", shouldFollowOwner);
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID);
        }
    }
}
