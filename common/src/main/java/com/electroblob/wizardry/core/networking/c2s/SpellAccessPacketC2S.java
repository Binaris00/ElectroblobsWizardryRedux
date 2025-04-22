package com.electroblob.wizardry.core.networking.c2s;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class SpellAccessPacketC2S implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("spell_quick_access");
    private final int index;

    public SpellAccessPacketC2S(int index) {
        this.index = index;
    }

    public SpellAccessPacketC2S(FriendlyByteBuf buf) {
        this.index = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
        pBuf.writeInt(index);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        if(player == null) return;

        ItemStack wand = player.getMainHandItem();

        if (!(wand.getItem() instanceof ISpellCastingItem)) {
            wand = player.getOffhandItem();
        }

        if (wand.getItem() instanceof ISpellCastingItem) {
            ((ISpellCastingItem) wand.getItem()).selectSpell(wand, index);
            player.stopUsingItem();
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public int getIndex() {
        return index;
    }
}
