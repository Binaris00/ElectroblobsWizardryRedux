package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.event.EBEntityJoinLevelEvent;
import com.binaris.wizardry.core.event.WizardryEventBus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRY$addEntity(int entityId, Entity entityToSpawn, CallbackInfo ci) {
        boolean result = WizardryEventBus.getInstance().fire(new EBEntityJoinLevelEvent(entityToSpawn, (ClientLevel) (Object) this));
        if (result) ci.cancel();
    }
}
