package com.binaris.wizardry.mixin;

import com.binaris.wizardry.event.AdvancementEvents;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @see net.fabricmc.fabric.mixin.event.interaction.PlayerAdvancementTrackerMixin PlayerAdvancementTrackerMixin
 */
@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementMixin {
    @Shadow
    private ServerPlayer player;
    @Shadow
    public abstract AdvancementProgress getOrStartProgress(Advancement advancement);

    @Inject(method = "award", at = @At("RETURN"))
    private void onGrantCriteria(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> ci) {
        AdvancementProgress progress = this.getOrStartProgress(advancement);
        if (progress.isDone())
            AdvancementEvents.GRANT.invoker().onAdvancementGrant(player, advancement, progress);

        AdvancementEvents.PROGRESS_UPDATE.invoker().onProgressUpdate(player, advancement, progress);
    }

    @Inject(method = "revoke", at = @At("RETURN"))
    private void onRevokeCriteria(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> ci) {
        AdvancementProgress progress = this.getOrStartProgress(advancement);

        AdvancementEvents.REVOKE.invoker().onAdvancementRevoke(player, advancement, progress);
        AdvancementEvents.PROGRESS_UPDATE.invoker().onProgressUpdate(player, advancement, progress);
    }
}
