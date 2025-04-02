package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ReplenishHunger extends BuffSpell {
    public static final SpellProperty<Integer> HUNGER_POINTS = SpellProperty.intProperty("hunger_points", 6);
    public static final SpellProperty<Float> SATURATION_MODIFIER = SpellProperty.floatProperty("saturation_modifier", 0.1F);

    public ReplenishHunger() {
        super(1, 0.7f, 0.4f);
    }

    @Override
    protected boolean applyEffects(LivingEntity caster) {
        return true;
    }

    @Override
    protected void perform(Caster caster) {
        if(caster instanceof Player player && player.getFoodData().needsFood()){
            player.getFoodData().eat(property(HUNGER_POINTS), property(SATURATION_MODIFIER));
        }
        super.perform(caster);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(HUNGER_POINTS)
                .add(SATURATION_MODIFIER)
                .build();
    }
}
