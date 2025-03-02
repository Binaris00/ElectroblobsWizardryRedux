package com.electroblob.wizardry.common.content.spell.sorcery;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.RayTracer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class Blink extends Spell {
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;
        // TODO Bin: This could teleport the mount by a charm
        boolean teleportMount = false;
        boolean hitLiquids = teleportMount && player.getVehicle() instanceof Boat; // Boats teleport to the surface

        double range = 25;
        HitResult rayTrace = RayTracer.standardBlockRayTrace(player.level(),
                player, range, hitLiquids, !hitLiquids, false);

        if(player.level().isClientSide){
            for(int i = 0; i < 10; i++){
                double dx = player.xo;
                double dy = player.yo + 2 * player.level().random.nextFloat();
                double dz = player.zo;
                // For portal particles, velocity is not velocity but the offset where they start, then drift to
                // the actual position given.
                player.level().addParticle(ParticleTypes.PORTAL, dx, dy, dz, player.level().random.nextDouble() - 0.5,
                        player.level().random.nextDouble() - 0.5, player.level().random.nextDouble() - 0.5);
            }

            // TODO Bin: Missing better blind effect
            //Wizardry.proxy.playBlinkEffect(caster);
        }

        if(rayTrace != null && rayTrace instanceof BlockHitResult blockHitResult){

            BlockPos pos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());

            // TODO Bin: Add better blind effect
//            Vec3 vec = EntityUtil.findSpaceForTeleport(toTeleport, GeometryUtils.getFaceCentre(pos, EnumFacing.DOWN), teleportMount);
//
//            if(vec != null){
//                // Plays before and after so it is heard from both positions
//                this.playSound(world, caster, ticksInUse, -1, modifiers);
//
//                if(!teleportMount && caster.isRiding()) caster.dismountRidingEntity();
//                if(!world.isRemote) toTeleport.setPositionAndUpdate(vec.x, vec.y, vec.z);
//
//                this.playSound(world, caster, ticksInUse, -1, modifiers);
//                return true;
//            }
        }
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
