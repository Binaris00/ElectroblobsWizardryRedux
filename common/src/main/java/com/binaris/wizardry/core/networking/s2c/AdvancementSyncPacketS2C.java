package com.binaris.wizardry.core.networking.s2c;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.networking.ClientMessageHandler;
import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/// <b>\[Server -> Client\]</b> This packet is fired on login and on advancement gain to update the handbook progress.
public class AdvancementSyncPacketS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("advancement_sync");
    public final boolean showToasts;
    public final List<ResourceLocation> completedAdvancements;

    public AdvancementSyncPacketS2C(boolean showToasts, List<ResourceLocation> completedAdvancements) {
        this.showToasts = showToasts;
        this.completedAdvancements = completedAdvancements;
    }

    public AdvancementSyncPacketS2C(FriendlyByteBuf buf) {
        this.showToasts = buf.readBoolean();
        List<ResourceLocation> advancements = new ArrayList<>();
        while(buf.isReadable())
            advancements.add(buf.readResourceLocation());

        this.completedAdvancements = advancements;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(showToasts);
        for (ResourceLocation advancement : completedAdvancements) {
            buf.writeResourceLocation(advancement);
        }
    }

    @Override
    public void handleClient() {
        ClientMessageHandler.advancementSync(this);
    }

    @Override
    public String toString() {
        return ID.getPath();
    }
}
