package com.electroblob.wizardry.network;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.capabilities.CastCommandDataHolder;
import com.electroblob.wizardry.capabilities.SpellManagerDataHolder;
import com.electroblob.wizardry.capabilities.WizardDataHolder;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class PlayerCapabilitySyncPacketS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("player_capability_sync");

    private final CapabilityType type;
    private final CompoundTag data;

    public enum CapabilityType {
        CAST_COMMAND,
        SPELL_MANAGER,
        WIZARD_DATA
    }

    public PlayerCapabilitySyncPacketS2C(CapabilityType type, CompoundTag data) {
        this.type = type;
        this.data = data;
    }

    public PlayerCapabilitySyncPacketS2C(FriendlyByteBuf buf) {
        this.type = buf.readEnum(CapabilityType.class);
        this.data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeNbt(data);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if (minecraft.level == null) return;

        switch (type) {
            case CAST_COMMAND -> player.getCapability(CastCommandDataHolder.INSTANCE)
                    .ifPresent(d -> d.deserializeNBT(data));
            case SPELL_MANAGER -> player.getCapability(SpellManagerDataHolder.INSTANCE)
                    .ifPresent(d -> d.deserializeNBT(data));
            case WIZARD_DATA -> player.getCapability(WizardDataHolder.INSTANCE)
                    .ifPresent(d -> d.deserializeNBT(data));
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
