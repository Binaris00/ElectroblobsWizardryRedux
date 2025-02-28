package com.electroblob.wizardry.common.content.spell.abstr;

import com.electroblob.wizardry.api.client.util.ClientUtils;
import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.api.common.util.RayTracer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class RaySpell extends Spell {
    protected static final double Y_OFFSET = 0.25;
    protected double particleSpacing = 0.85;
    protected double particleJitter = 0.1;
    protected double particleVelocity = 0;
    protected boolean ignoreLivingEntities = false;
    protected boolean hitLiquids = false;
    protected boolean ignoreUncollidables = true;
    protected float aimAssist = 0;

    public Spell particleSpacing(double particleSpacing) {
        this.particleSpacing = particleSpacing;
        return this;
    }

    public Spell particleJitter(double particleJitter) {
        this.particleJitter = particleJitter;
        return this;
    }

    public Spell particleVelocity(double particleVelocity) {
        this.particleVelocity = particleVelocity;
        return this;
    }

    public Spell ignoreLivingEntities(boolean ignoreLivingEntities) {
        this.ignoreLivingEntities = ignoreLivingEntities;
        return this;
    }

    public Spell hitLiquids(boolean hitLiquids) {
        this.hitLiquids = hitLiquids;
        return this;
    }


    public Spell ignoreUncollidables(boolean ignoreUncollidables) {
        this.ignoreUncollidables = ignoreUncollidables;
        return this;
    }


    public Spell aimAssist(float aimAssist) {
        this.aimAssist = aimAssist;
        return this;
    }

    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        Vec3 look = player.getLookAngle();
        Vec3 origin = new Vec3(player.getX(), player.getY() + player.getEyeHeight() - Y_OFFSET, player.getZ());

        if(isInstantCast() && player.level().isClientSide && ClientUtils.isFirstPerson(player)){
            origin = origin.add(look.scale(1.2));
        }
        // TODO: ticksInUse
        if (!shootSpell(player.level(), origin, look, player, 0)) return;

        //if (casterSwingsArm()) caster.swingHand(hand);
        //this.playSound(world, caster, ticksInUse, -1, modifiers);
        //return true;
    }

    protected abstract boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse);
    protected abstract boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse);
    protected abstract boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse);

    protected boolean shootSpell(Level world, Vec3 origin, Vec3 direction, Player caster, int ticksInUse) {
        double range = 30; // TODO: Set Spell Range config
        Vec3 endpoint = origin.add(direction.scale(range));

        HitResult rayTrace = RayTracer.rayTrace(world, caster, origin, endpoint, aimAssist, hitLiquids, Entity.class, ignoreLivingEntities ? EntityUtil::isLiving : RayTracer.ignoreEntityFilter(caster));

        boolean flag = false;

        if (rayTrace != null) {
            if (rayTrace.getType() == HitResult.Type.ENTITY) {
                flag = onEntityHit(world, ((EntityHitResult) rayTrace).getEntity(), rayTrace.getLocation(), caster, origin, ticksInUse);

                if (flag) range = origin.distanceTo(rayTrace.getLocation());
            } else if (rayTrace.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) rayTrace;
                flag = onBlockHit(world, blockHitResult.getBlockPos(), blockHitResult.getDirection(), rayTrace.getLocation(), caster, origin, ticksInUse);

                range = origin.distanceTo(rayTrace.getLocation());
            }
        }

        if (!flag && !onMiss(world, caster, origin, direction, ticksInUse)) return false;


        if (world.isClientSide) {
            spawnParticleRay(world, origin, direction, range);
        }

        return true;
    }

    protected void spawnParticleRay(Level world, Vec3 origin, Vec3 direction, double distance){
        Vec3 velocity = direction.scale(particleVelocity);

        for(double d = particleSpacing; d <= distance; d += particleSpacing){
            double x = origin.x + d*direction.x + particleJitter * (world.random.nextDouble()*2 - 1);
            double y = origin.y + d*direction.y + particleJitter * (world.random.nextDouble()*2 - 1);
            double z = origin.z + d*direction.z + particleJitter * (world.random.nextDouble()*2 - 1);
            spawnParticle(world, x, y, z, velocity.x, velocity.y, velocity.z);
        }
    }

    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz){}
}
