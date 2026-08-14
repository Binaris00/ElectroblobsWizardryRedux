package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.MobEffectContext;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;

public class FontOfManaMobEffect extends MagicMobEffect {
    public FontOfManaMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x66ccff);
    }

    // Event handler to reduce cooldowns when caster has the buff
    @Override
    public void onSpellPreCast(SpellCastEvent.Pre event, MobEffectContext context) {
        if (event.getCaster() != null) {
            MobEffectInstance inst = event.getCaster().getEffect(EBMobEffects.FONT_OF_MANA.get());
            if (inst != null) event.getModifiers().multiply(SpellModifiers.COOLDOWN, 1.0f / (2 + inst.getAmplifier()));
        }
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
    }
}
