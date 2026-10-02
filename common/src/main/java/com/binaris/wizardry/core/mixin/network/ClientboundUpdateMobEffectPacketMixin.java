package com.binaris.wizardry.core.mixin.network;

import com.binaris.wizardry.api.content.effect.MagicMobEffectInstance;
import com.binaris.wizardry.api.network.MagicEffectPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundUpdateMobEffectPacket.class)
public abstract class ClientboundUpdateMobEffectPacketMixin implements MagicEffectPacket {
    @Unique
    private boolean ebwizardry$magic;

    @Inject(method = "<init>(ILnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
    private void EBWIZARDRY$captureMagic(int entityId, MobEffectInstance instance, CallbackInfo ci) {
        this.ebwizardry$magic = instance instanceof MagicMobEffectInstance;
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void EBWIZARDRY$writeMagic(FriendlyByteBuf buf, CallbackInfo ci) {
        buf.writeBoolean(this.ebwizardry$magic);
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void EBWIZARDRY$readMagic(FriendlyByteBuf buf, CallbackInfo ci) {
        this.ebwizardry$magic = buf.readBoolean();
    }

    @Override
    public boolean ebwizardry$isMagic() {
        return this.ebwizardry$magic;
    }
}