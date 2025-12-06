package com.electroblob.wizardry.network;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.client.ClientPacketHandler;
import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.networking.c2s.BlockUsePacketC2S;
import com.electroblob.wizardry.core.networking.c2s.ControlInputPacketC2S;
import com.electroblob.wizardry.core.networking.c2s.SpellAccessPacketC2S;
import com.electroblob.wizardry.core.networking.s2c.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public class EBForgeNetwork {
    private static final String version = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            WizardryMainMod.location("main_channel"), () -> version, version::equals, version::equals);

    private static int index = 0;

    public static void registerMessages() {
        register(TestParticlePacketS2C.class, TestParticlePacketS2C::new, NetworkDirection.PLAY_TO_CLIENT);
        register(BlockUsePacketC2S.class, BlockUsePacketC2S::new, NetworkDirection.PLAY_TO_SERVER);
        register(ControlInputPacketC2S.class, ControlInputPacketC2S::new, NetworkDirection.PLAY_TO_SERVER);
        register(SpellAccessPacketC2S.class, SpellAccessPacketC2S::new, NetworkDirection.PLAY_TO_SERVER);
        register(SpellGlyphPacketS2C.class, SpellGlyphPacketS2C::new, NetworkDirection.PLAY_TO_CLIENT);
        register(SpellPropertiesSyncS2C.class, SpellPropertiesSyncS2C::new, NetworkDirection.PLAY_TO_CLIENT);
        register(NPCSpellCastS2C.class, NPCSpellCastS2C::new, NetworkDirection.PLAY_TO_CLIENT);
        register(SpellCastS2C.class, SpellCastS2C::new, NetworkDirection.PLAY_TO_CLIENT);
        register(MinionSyncPacketS2C.class, MinionSyncPacketS2C::new, NetworkDirection.PLAY_TO_CLIENT);
        register(PlayerCapabilitySyncPacketS2C.class, PlayerCapabilitySyncPacketS2C::new, NetworkDirection.PLAY_TO_CLIENT);
        register(ArcaneLockSyncPacketS2C.class, ArcaneLockSyncPacketS2C::new, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <T extends Message> void register(Class<T> clazz, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
        INSTANCE.messageBuilder(clazz, index++, direction)
                .encoder(T::encode)
                .decoder(factory)
                .consumerMainThread((packet, contextSupplier) -> {
                    NetworkEvent.Context ctx = contextSupplier.get();
                    if (ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleClientPacket(packet));
                    } else {
                        packet.handleServer(ctx.getSender().server, ctx.getSender());
                    }
                    ctx.setPacketHandled(true);
                }).add();
    }
}
