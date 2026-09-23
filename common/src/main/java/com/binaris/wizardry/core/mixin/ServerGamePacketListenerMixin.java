package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSleeping()Z"))
    private boolean EBWIZARDRY$whitelistFlightFromFloatingKick(ServerPlayer player) {
        if (EntityUtil.isCasting(player, Spells.FLIGHT)) return true;
        return player.isSleeping();
    }
}
