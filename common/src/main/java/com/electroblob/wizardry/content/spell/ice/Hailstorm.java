package com.electroblob.wizardry.content.spell.ice;

import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.entity.construct.HailstormConstruct;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConstructRangedSpell;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Hailstorm extends ConstructRangedSpell<HailstormConstruct> {
    public Hailstorm() {
        super(HailstormConstruct::new, false);
    }

    @Override
    protected boolean spawnConstruct(CastContext ctx, Vec3 vec3, @Nullable Direction side) {
        double dx = ctx.caster() == null ? side.step().x() : ctx.caster().getX() - vec3.x;
        double dz = ctx.caster() == null ? side.step().z() : ctx.caster().getZ() - vec3.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        double x = vec3.x;
        double y = vec3.y;
        double z = vec3.z;
        if(dist != 0){
            double distRatio = 3 / dist;
            x += dx * distRatio;
            z = dz * distRatio;
        }
        y += 5;

        Vec3 betVec3 = new Vec3(x, y, z);
        return super.spawnConstruct(ctx, betVec3, side);
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
