package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class Satiety extends BuffSpell {
    public Satiety() {
        super(1, 0.7f, 0.3f);
        this.soundValues(0.7f, 1.2f, 0.4f);
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
    protected boolean applyEffects(CastContext ctx, LivingEntity caster) {
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.caster().getFoodData().needsFood()) {
            ctx.caster().getFoodData().eat((int) (property(ReplenishHunger.HUNGER_POINTS) * ctx.modifiers().get(SpellModifiers.POTENCY)), property(ReplenishHunger.HUNGER_POINTS));
        }
        return super.cast(ctx);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellType.DEFENCE, SpellAction.POINT_UP, 40, 15, 50)
                .add(ReplenishHunger.HUNGER_POINTS, 16)
                .add(ReplenishHunger.SATURATION_MODIFIER, 0.1F)
                .build();
    }
}
