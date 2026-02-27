package com.binaris.wizardry.client.sound;

import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MovingSoundSpellCharge extends MovingSoundEntity<LivingEntity> {
    public MovingSoundSpellCharge(LivingEntity entity, SoundEvent sound, SoundSource category, float volume, float pitch, boolean repeat) {
        super(entity, sound, category, volume, pitch, repeat);
    }

    @Override
    public void tick() {
        if (source.isUsingItem()) {
            ItemStack stack = source.getUseItem();

            if (stack.getItem() instanceof ISpellCastingItem) {
                if (source.getTicksUsingItem() < ((ISpellCastingItem) stack.getItem()).getCurrentSpell(stack).getChargeUp()) {
                    super.tick();
                    return;
                }
            }
        }

        this.stop();
    }
}
