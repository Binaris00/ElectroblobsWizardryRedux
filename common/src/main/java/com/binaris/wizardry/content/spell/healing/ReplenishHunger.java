package com.binaris.wizardry.content.spell.healing;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.content.spell.abstr.BuffSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ReplenishHunger extends BuffSpell {
    public static final SpellProperty<Integer> HUNGER_POINTS = SpellProperty.intProperty("hunger_points", 6);
    public static final SpellProperty<Float> SATURATION_MODIFIER = SpellProperty.floatProperty("saturation_modifier", 0.1F);

    public ReplenishHunger() {
        super(1, 0.7f, 0.4f);
    }


    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity caster) {
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.caster().getFoodData().needsFood()) {
            ctx.caster().getFoodData().eat((int) (property(HUNGER_POINTS) * ctx.modifiers().get(SpellModifiers.POTENCY)), property(SATURATION_MODIFIER));
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
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.HEALING, SpellType.BUFF, SpellAction.POINT_UP, 10, 0, 30)
                .add(HUNGER_POINTS)
                .add(SATURATION_MODIFIER)
                .build();
    }
}
