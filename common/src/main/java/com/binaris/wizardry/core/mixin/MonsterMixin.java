package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Monster.class)
public abstract class MonsterMixin {

    @Unique
    Monster monster = (Monster) (Object) this;

    @Inject(method = "shouldDropLoot", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$dropLoot(CallbackInfoReturnable<Boolean> cir) {
        if (Services.OBJECT_DATA.isMinion(monster)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldDropExperience", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$dropExperience(CallbackInfoReturnable<Boolean> cir) {
        if (Services.OBJECT_DATA.isMinion(monster)) {
            cir.setReturnValue(false);
        }
    }
}
