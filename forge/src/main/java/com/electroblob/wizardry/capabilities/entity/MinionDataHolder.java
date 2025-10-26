package com.electroblob.wizardry.capabilities.entity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.data.CastCommandData;
import com.electroblob.wizardry.api.content.data.MinionData;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MinionDataHolder implements INBTSerializable<CompoundTag>, MinionData {
    private static final ResourceLocation LOCATION = WizardryMainMod.location("minion_data");
    public static final Capability<MinionDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    private final Mob provider;
    private int lifetime = -1;
    private boolean summoned = false;

    public MinionDataHolder(Mob mob) {
        this.provider = mob;
    }

    private void sync(){
        // TODO THX FORGE FOR MAKING THE CAPABILITY SYSTEM SO HARD FOR ME
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
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("lifetime", lifetime);
        tag.putBoolean("summoned", summoned);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.lifetime = tag.getInt("lifetime");
        this.summoned = tag.getBoolean("summoned");
    }


    @Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<MinionDataHolder> dataHolder;

        public Provider(Mob mob) {
            this.dataHolder = LazyOptional.of(() -> new MinionDataHolder(mob));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return MinionDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return dataHolder.orElseThrow(NullPointerException::new).serializeNBT();
        }
        @Override
        public void deserializeNBT(CompoundTag arg) {
            dataHolder.orElseThrow(NullPointerException::new).deserializeNBT(arg);
        }

        @SubscribeEvent
        public static void attachCapability(final AttachCapabilitiesEvent<Entity> event) {
            if(event.getObject() instanceof Mob mob){
                event.addCapability(LOCATION, new MinionDataHolder.Provider(mob));
            }
        }
    }
}
