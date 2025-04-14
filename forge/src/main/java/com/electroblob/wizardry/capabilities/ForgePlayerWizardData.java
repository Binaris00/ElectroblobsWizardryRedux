package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.PlayerWizardData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;


/** Check {@link PlayerWizardData}, this class is just the implementation to load-save the player's wizard data */
@Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgePlayerWizardData implements INBTSerializable<CompoundTag> {
    private static final Capability<ForgePlayerWizardData> WIZARD_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    PlayerWizardData wizardData;

    public ForgePlayerWizardData(){
        this.wizardData = new PlayerWizardData();
    }

    public static void attachCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof Player player) {
            e.addCapability(new ResourceLocation(WizardryMainMod.MOD_ID, "wizard_data"), new ForgePlayerWizardData.Provider(player));
        }
    }

    public PlayerWizardData getWizardData() {
        return wizardData;
    }

    public static ForgePlayerWizardData get(Player player) {
        return player.getCapability(WIZARD_DATA_CAPABILITY).orElse(new ForgePlayerWizardData());
    }

    @Override public CompoundTag serializeNBT() {
        return wizardData.serializeNBT(new CompoundTag());
    }
    @Override public void deserializeNBT(CompoundTag tag) {
        this.wizardData = wizardData.deserializeNBT(tag);
    }

    // TODO: Forge events to handle capabilities
    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        ForgePlayerWizardData newData = ForgePlayerWizardData.get(event.getEntity());
        ForgePlayerWizardData oldData = ForgePlayerWizardData.get(event.getOriginal());

//        newData.copyFrom(oldData, event.isWasDeath());
//        newData.sync();
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
//        if (!event.getEntity().level.isClientSide && event.getEntity() instanceof ServerPlayer) {
//            WizardData data = WizardData.get((Player) event.getEntity());
//            if(data != null) {
//
//                data.sync();
//            }
//        }
    }

    @SubscribeEvent
    public static void onLivingUpdateEvent(LivingEvent.LivingTickEvent event) {
//        if (event.getEntity() instanceof Player) {
//            Player player = (Player) event.getEntity();
//
//            if(WizardData.get(player) != null) {
//
//                WizardData.get(player).update();
//            }
//        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final LazyOptional<ForgePlayerWizardData> data;

        // TODO
        public Provider(Player player) {
            data = LazyOptional.of(ForgePlayerWizardData::new);
        }


        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
            return WIZARD_DATA_CAPABILITY.orEmpty(capability, data.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return data.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
        }
    }
}
