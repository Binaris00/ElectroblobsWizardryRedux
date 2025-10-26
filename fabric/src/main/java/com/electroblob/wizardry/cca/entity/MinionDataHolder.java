package com.electroblob.wizardry.cca.entity;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.MinionData;
import com.electroblob.wizardry.cca.EBComponents;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class MinionDataHolder implements MinionData, ComponentV3, AutoSyncedComponent {
    Mob provider;
    private int lifetime = -1;
    private boolean summoned = false;

    public MinionDataHolder(Mob provider) {
        this.provider = provider;
    }

    public void sync(){
        EBComponents.MINION_DATA.sync(provider);
    }

    @Override
    public Mob getProvider() {
        return this.provider;
    }

    @Override
    public void tick() {
        if (!summoned) return;

        if (provider.tickCount > this.getLifetime() && this.getLifetime() > 0) {
            this.provider.discard();
        }

        if (provider.level().isClientSide && provider.level().random.nextInt(8) == 0) {
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(provider.xo, provider.yo + (provider.level().random.nextDouble() * 1.5 + 1), provider.zo).color(0.1f, 0.0f, 0.0f).spawn(provider.level());
        }

        setLifetime(getLifetime() - 1);
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
    public void readFromNbt(@NotNull CompoundTag tag) {
        this.lifetime = tag.getInt("lifetime");
        this.summoned = tag.getBoolean("summoned");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("lifetime", lifetime);
        tag.putBoolean("summoned", summoned);
    }
}
