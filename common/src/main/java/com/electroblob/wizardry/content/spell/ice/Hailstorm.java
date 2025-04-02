package com.electroblob.wizardry.content.spell.ice;

import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.entity.construct.HailstormConstruct;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConstructRangedSpell;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class Hailstorm extends ConstructRangedSpell<HailstormConstruct> {
    public Hailstorm() {
        super(HailstormConstruct::new, false);
    }

    @Override
    protected boolean spawnConstruct(Level world, double x, double y, double z, @Nullable Direction side, @Nullable LivingEntity caster) {
        double dx = caster == null ? side.step().x() : caster.getX() - x;
        double dz = caster == null ? side.step().z() : caster.getZ() - z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if(dist != 0){
            double distRatio = 3 / dist;
            x += dx * distRatio;
            z += dz * distRatio;
        }
        y += 5;

        return super.spawnConstruct(world, x, y, z, side, caster);
    }

    @Override
    protected void addConstructExtras(HailstormConstruct construct, Direction side, @Nullable LivingEntity caster) {
        if(caster != null){
            construct.setYRot(caster.getYHeadRot());
        }else{
            construct.setYRot(side.toYRot());
        }
    }


    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 20F)
                .add(DefaultProperties.DURATION, 120)
                .add(DefaultProperties.EFFECT_RADIUS, 2)
                .build();
    }
}
