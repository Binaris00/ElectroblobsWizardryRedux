package com.binaris.wizardry.core.mixin.client;

import com.binaris.wizardry.client.renderer.entity.layers.LayerSummonAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/// Adds the summon appear/disappear overlay layer to every living entity renderer, and hides the normal model while a
/// minion is appearing or disappearing (only the overlay is shown during those ticks).
///
/// This is a common mixin loaded by both Fabric and Forge, so no per-loader registration is needed: minions are
/// arbitrary mobs, so the layer has to be present on every `LivingEntityRenderer` anyway.
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
    protected List<RenderLayer<T, M>> layers;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void EBWIZARDRY$addSummonAnimationLayer(CallbackInfo ci) {
        this.layers.add(new LayerSummonAnimation<T, M>((RenderLayerParent<T, M>) (Object) this));
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void EBWIZARDRY$hideModelDuringSummonAnimation(LivingEntity entity, boolean bodyVisible, boolean invisibleToPlayer, boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        if (LayerSummonAnimation.shouldHideModel(entity)) {
            cir.setReturnValue(null);
        }
    }
}