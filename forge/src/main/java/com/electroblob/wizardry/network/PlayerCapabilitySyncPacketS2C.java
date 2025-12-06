package com.electroblob.wizardry.network;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

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
    public void handleClient() {
        ClientMessageHandlerForge.playerCapabilitySync(this);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public CompoundTag getData() {
        return data;
    }

    public CapabilityType getType() {
        return type;
    }
}
