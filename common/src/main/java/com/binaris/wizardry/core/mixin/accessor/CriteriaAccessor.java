package com.binaris.wizardry.core.mixin.accessor;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// thx fabric, this is why I love you
@Mixin({CriteriaTriggers.class})
public interface CriteriaAccessor {
    @SuppressWarnings("all")
    @Invoker
    static <T extends CriterionTrigger<?>> T callRegister(T object) {
        //throw new AssertionError("Mixin dummy");
        return object;
    }
}
