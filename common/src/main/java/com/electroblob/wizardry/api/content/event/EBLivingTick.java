package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This applies to loaders with the Mixin
 * {@link com.electroblob.wizardry.core.mixin.LivingEntityMixin#EBWIZARDRY$tick(CallbackInfo) LivingEntityMixin#EBWIZARDRY$tick}
 * */
public final class EBLivingTick extends WizardryEvent {
    LivingEntity entity;
    Level level;

    public EBLivingTick(LivingEntity entity, Level level) {
        this.entity = entity;
        this.level = level;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Level getLevel() {
        return level;
    }
}
