package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.hell.BinWizardDataInternal;
import com.electroblob.wizardry.registry.SpellRegistryForge;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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

import javax.annotation.Nonnull;
import java.util.HashSet;

@Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeWizardData implements INBTSerializable<CompoundTag> {
    private static final Capability<ForgeWizardData> WIZARD_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    BinWizardDataInternal wizardData;

    public ForgeWizardData(){
        this.wizardData = new BinWizardDataInternal();
    }

    public static void attachCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof Player player) {
            e.addCapability(new ResourceLocation(WizardryMainMod.MOD_ID, "wizard_data"), new ForgeWizardData.Provider((Player) e.getObject()));
        }
    }

    public BinWizardDataInternal getWizardData() {
        return wizardData;
    }

    public static ForgeWizardData get(Player player) {
        return player.getCapability(WIZARD_DATA_CAPABILITY).orElse(new ForgeWizardData());
    }

    @Override
    public CompoundTag serializeNBT() {
        return wizardData.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        BinWizardDataInternal wizardData = new BinWizardDataInternal();
        wizardData.spellsDiscovered = new HashSet<>();

        if(tag.contains("spellsDiscovered", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("spellsDiscovered", Tag.TAG_STRING);
            for (Tag element : listTag) {
                ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
                if(location != null) {
                    wizardData.spellsDiscovered.add(SpellRegistryForge.get().getValue(location));
                }
            }
        }
        this.wizardData = wizardData;
    }

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        ForgeWizardData newData = ForgeWizardData.get(event.getEntity());
        ForgeWizardData oldData = ForgeWizardData.get(event.getOriginal());

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
        private final LazyOptional<ForgeWizardData> data;

        // TODO
        public Provider(Player player) {
            data = LazyOptional.of(() -> {
                ForgeWizardData i = new ForgeWizardData();
                return i;
            });
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
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
