package com.electroblob.wizardry.core.mixin.client;

import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ConjureBarMixin {
    @Unique
    ItemStack stack = (ItemStack) (Object) this;

    // Overrides the durability bar width to show remaining lifetime based on current game time vs expire time.
    @Inject(method = "getBarWidth", at = @At("RETURN"), cancellable = true)
    public void EBWIZARDRY$conjureGetBarWidth(CallbackInfoReturnable<Integer> cir) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if (data == null || !data.isSummoned()) return;

        long currentGameTime = EBWIZARDRY$getGameTime();
        int remaining = data.getRemainingLifetime(currentGameTime);
        int duration = data.getDuration();

        if (duration <= 0) return;
        cir.setReturnValue(Math.round(13.0f * remaining / (float) duration));
    }

    @Unique
    private long EBWIZARDRY$getGameTime() {
        // I'm not proud of this
        return Minecraft.getInstance().level.getGameTime();
    }
}
