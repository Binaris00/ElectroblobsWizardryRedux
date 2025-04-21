package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.event.EBEntityJoinLevelEvent;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
public abstract class PersistentEntitySectionManagerMixin {

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void EBWIZARDRY$addEntity(EntityAccess entity, boolean worldGenSpawned, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof Entity e){
            boolean result = WizardryEventBus.getInstance().fire(new EBEntityJoinLevelEvent(e, e.level()));
            if (result) cir.setReturnValue(false);
        }
    }
}
