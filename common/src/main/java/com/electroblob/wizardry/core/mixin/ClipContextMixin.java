package com.electroblob.wizardry.core.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO This is probably a temp mixin, I just want to make sure if I can't use a raycast without an entity like before
@Mixin(ClipContext.class)
public abstract class ClipContextMixin {
    @Mutable
    @Shadow
    @Final
    private CollisionContext collisionContext;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void EBWIZARDRY$clipInit(Vec3 from, Vec3 _to, ClipContext.Block block, ClipContext.Fluid fluid, Entity entity, CallbackInfo ci) {
        collisionContext = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
    }
}
