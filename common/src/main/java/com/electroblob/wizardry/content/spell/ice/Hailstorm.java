package com.electroblob.wizardry.content.spell.ice;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.entity.construct.HailstormConstruct;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConstructRangedSpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Hailstorm extends ConstructRangedSpell<HailstormConstruct> {
    public Hailstorm() {
        super(HailstormConstruct::new, false);
    }

    @Override
    protected boolean spawnConstruct(CastContext ctx, Vec3 vec3, @Nullable Direction side) {
        return super.spawnConstruct(ctx, vec3.add(0, 5, 0), side);
    }

    @Override
    protected void addConstructExtras(HailstormConstruct construct, Direction side, @Nullable LivingEntity caster) {
        if (caster != null) {
            construct.setYRot(caster.getYHeadRot());
        } else {
            construct.setYRot(side.toYRot());
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.ICE, SpellType.ATTACK, SpellAction.POINT, 75, 20, 300)
                .add(DefaultProperties.RANGE, 20F)
                .add(DefaultProperties.DURATION, 120)
                .add(DefaultProperties.EFFECT_RADIUS, 2)
                .build();
    }
}
