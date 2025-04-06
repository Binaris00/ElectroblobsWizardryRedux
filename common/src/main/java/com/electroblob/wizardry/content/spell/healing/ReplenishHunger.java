package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import net.minecraft.world.entity.LivingEntity;

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
    public boolean cast(PlayerCastContext ctx) {
        if(ctx.caster().getFoodData().needsFood()){
            ctx.caster().getFoodData().eat(property(HUNGER_POINTS), property(SATURATION_MODIFIER));
        }
        return super.cast(ctx);
    }

    @Override
    public boolean canCastByEntity() {
        return false;
    }

    @Override
    public boolean canCastByLocation() {
        return false;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(HUNGER_POINTS)
                .add(SATURATION_MODIFIER)
                .build();
    }
}
