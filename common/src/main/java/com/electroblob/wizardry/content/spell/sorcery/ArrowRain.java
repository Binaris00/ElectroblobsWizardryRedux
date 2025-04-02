package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.entity.ArrowRainConstruct;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ConstructRangedSpell;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ArrowRain extends ConstructRangedSpell<ArrowRainConstruct> {
    public ArrowRain() {
        super(ArrowRainConstruct::new, false);
        this.floor(true);
    }

    @Override
    protected boolean spawnConstruct(Level world, double x, double y, double z, @Nullable Direction side, @Nullable LivingEntity caster) {
        double dx = caster == null ? side.getStepX() : caster.xo - x;
        double dz = caster == null ? side.getStepZ() : caster.zo - z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if(dist != 0){
            double distRatio = 3 / dist;
            x += dx * distRatio;
            z += dz * distRatio;
        }
        // Moves the entity up 5 blocks so that it is above mobs' heads.
        y += 5;

        return super.spawnConstruct(world, x, y, z, side, caster);
    }


    @Override
    protected void addConstructExtras(ArrowRainConstruct construct, Direction side, @Nullable LivingEntity caster) {
        // Makes the arrows shoot in the direction the caster was looking when they cast the spell.
        // TODO FIX YHEAD
        if(caster != null){
            EBLogger.error(Component.literal("Player is using yhead for arrow rain"));
            //construct.setXRot(caster.getXRot());
            construct.setYHeadRot(caster.getYHeadRot());
        }else{
            //construct.setXRot(0);
            construct.setYHeadRot(side.toYRot());
            EBLogger.error(Component.literal("Side is using yhead for arrow rain"));
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
