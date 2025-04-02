package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.RayTracer;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class ConstructRangedSpell<T extends MagicConstructEntity> extends ConstructSpell<T>{
    protected boolean hitLiquids = false;
    protected boolean ignoreUncollidables = false;

    public ConstructRangedSpell(Function<Level, T> constructFactory, boolean permanent) {
        super(constructFactory, permanent);
    }

    public Spell hitLiquids(boolean hitLiquids) {
        this.hitLiquids = hitLiquids;
        return this;
    }

    public Spell ignoreUncollidables(boolean ignoreUncollidables) {
        this.ignoreUncollidables = ignoreUncollidables;
        return this;
    }

    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        double range = this.property(DefaultProperties.RANGE);
        HitResult rayTrace = RayTracer.standardBlockRayTrace(player.level(), player, range, hitLiquids, ignoreUncollidables, false);

        if (rayTrace != null && rayTrace.getType() == HitResult.Type.BLOCK && (((BlockHitResult) rayTrace).getDirection() == Direction.UP || !requiresFloor)) {
            if (!player.level().isClientSide) {
                double x = rayTrace.getLocation().x;
                double y = rayTrace.getLocation().y;
                double z = rayTrace.getLocation().z;

                spawnConstruct(player.level(), x, y, z, ((BlockHitResult) rayTrace).getDirection(), player);
            }
        } else if (!requiresFloor) {
            if (!player.level().isClientSide) {
                Vec3 look = player.getLookAngle();

                double x = player.getX() + look.x * range;
                double y = player.getY() + player.getEyeHeight() + look.y * range;
                double z = player.getZ() + look.z * range;

                spawnConstruct(player.level(), x, y, z, null, player);
            }
        }

        // TODO spell sound ticksinuse
        this.playSound(caster.getCastLevel(), player, 0, -1);
    }
}
