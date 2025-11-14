package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.event.EBEntityJoinLevelEvent;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRY$addEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        boolean result = WizardryEventBus.getInstance().fire(new EBEntityJoinLevelEvent(entity, (ServerLevel) (Object) this));
        if (result) cir.setReturnValue(false);
    }
}
