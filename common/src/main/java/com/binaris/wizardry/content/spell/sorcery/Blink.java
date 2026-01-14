package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import org.jetbrains.annotations.NotNull;

// TODO BLINK EFFECT
public class Blink extends Spell {
//    @Override
//    protected void perform(Caster caster) {
//        if(!(caster instanceof Player player)) return;
//        // TODO Bin: This could teleport the mount by a charm
//        boolean teleportMount = false;
//        boolean hitLiquids = teleportMount && player.getVehicle() instanceof Boat; // Boats teleport to the surface
//
//        double range = 25;
//        HitResult rayTrace = RayTracer.standardBlockRayTrace(player.level(),
//                player, range, hitLiquids, !hitLiquids, false);
//
//        if(player.level().isClientSide){
//            for(int i = 0; i < 10; i++){
//                double dx = player.xo;
//                double dy = player.yo + 2 * player.level().random.nextFloat();
//                double dz = player.zo;
//                // For portal particles, velocity is not velocity but the offset where they start, then drift to
//                // the actual position given.
//                player.level().addParticle(ParticleTypes.PORTAL, dx, dy, dz, player.level().random.nextDouble() - 0.5,
//                        player.level().random.nextDouble() - 0.5, player.level().random.nextDouble() - 0.5);
//            }
//
//            // TODO Bin: Missing better blind effect
//            //Wizardry.proxy.playBlinkEffect(caster);
//        }
//
//        if(rayTrace != null && rayTrace instanceof BlockHitResult blockHitResult){
//
//            BlockPos pos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
//
//            // TODO Bin: Add better blind effect

    /// /            Vec3 vec = EntityUtil.findSpaceForTeleport(toTeleport, GeometryUtils.getFaceCentre(pos, EnumFacing.DOWN), teleportMount);
    /// /
    /// /            if(vec != null){
    /// /                // Plays before and after so it is heard from both positions
    /// /                this.playSound(world, caster, ticksInUse, -1, modifiers);
    /// /
    /// /                if(!teleportMount && caster.isRiding()) caster.dismountRidingEntity();
    /// /                if(!world.isRemote) toTeleport.setPositionAndUpdate(vec.x, vec.y, vec.z);
    /// /
    /// /                this.playSound(world, caster, ticksInUse, -1, modifiers);
    /// /                return true;
    /// /            }
//        }
//    }
    @Override
    public boolean cast(PlayerCastContext ctx) {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
