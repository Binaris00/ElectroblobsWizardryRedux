package com.binaris.wizardry.core.mixin.client;

import com.binaris.wizardry.api.content.effect.MagicMobEffectInstance;
import com.binaris.wizardry.api.network.MagicEffectPacket;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Redirect(method = "handleUpdateMobEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;forceAddEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)V"))
    private void EBWIZARDRY$magicAddEffect(LivingEntity target, MobEffectInstance instance, Entity source, @Local ClientboundUpdateMobEffectPacket packet) {
        if (packet instanceof MagicEffectPacket magicPacket && magicPacket.ebwizardry$isMagic()) {
            target.forceAddEffect(new MagicMobEffectInstance(instance), source);
        } else {
            target.forceAddEffect(instance, source);
        }
    }
}