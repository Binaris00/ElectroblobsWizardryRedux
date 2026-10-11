package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.core.networking.MagicEffectSync;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

    @Shadow @Final private Entity entity;

    @Inject(method = "sendPairingData", at = @At("TAIL"))
    private void EBWIZARDRY$sendMagicEffectsOnPair(ServerPlayer player,
                                                    Consumer<Packet<ClientGamePacketListener>> consumer, CallbackInfo ci) {
        if (!(entity instanceof LivingEntity living)) return;
        MagicEffectSync.sendAllTo(player, living);
    }
}