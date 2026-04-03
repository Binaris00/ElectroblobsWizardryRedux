package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.spell.*;
import com.binaris.wizardry.content.WizardryAttributeModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttributeModifier.class)
public abstract class AttributeModifierMixin {
    @Inject(method = "load", at = @At("HEAD"), cancellable = true)
    private static void EBWIZARDRY$attribute_load(CompoundTag nbt, CallbackInfoReturnable<AttributeModifier> cir) {
        try {
            SpellCondition condition = SpellCondition.load(nbt);
            if (condition == null) return;

            cir.setReturnValue(new WizardryAttributeModifier(
                    nbt.getUUID("UUID"),
                    nbt.getString("Name"),
                    nbt.getDouble("Amount"),
                    AttributeModifier.Operation.fromValue(nbt.getInt("Operation")),
                    condition
            ));
        } catch (Exception e) {
            cir.setReturnValue(null);
        }
    }
}
