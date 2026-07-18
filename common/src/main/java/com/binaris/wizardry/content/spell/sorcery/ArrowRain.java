package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.entity.ArrowRainConstruct;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ConstructSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArrowRain extends ConstructSpell<ArrowRainConstruct> {
    public ArrowRain() {
        super(ArrowRainConstruct::new, false);
        this.floor(true);
    }

    @Override
    protected boolean spawnConstruct(CastContext ctx, Vec3 vec3, @Nullable Direction side) {
        return super.spawnConstruct(ctx, vec3.add(0, 5, 0), side);
    }

    @Override
    protected void addConstructExtras(CastContext ctx, ArrowRainConstruct construct, Direction side) {
        // Makes the arrows shoot in the direction the caster was looking when they cast the spell.
        if (ctx.caster() != null) {
            construct.setYRot(ctx.caster().getYHeadRot());
        } else {
            construct.setYRot(side.toYRot());
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.SORCERY, SpellTypes.ATTACK, SpellAction.POINT, 75, 20, 300)
                .add(DefaultProperties.RANGE, 20F)
                .add(DefaultProperties.DURATION, 120)
                .add(DefaultProperties.EFFECT_RADIUS, 2)
                .build();
    }
}
