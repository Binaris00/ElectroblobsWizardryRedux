package com.electroblob.wizardry.common.content.spell.sorcery;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.api.common.util.GeometryUtil;
import com.electroblob.wizardry.api.common.util.RayTracer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PhaseStep extends Spell {
    @Override
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        // TODO ItemArtefact.isArtefactActive(player, WizardryItems.charm_mount_teleporting)
        boolean teleportMount = player.getVehicle() != null;
        boolean hitLiquids = teleportMount && player.getVehicle() instanceof Boat;

        double range = 8;

        HitResult rayTrace = RayTracer.standardBlockRayTrace(player.level(), player,
                range, hitLiquids, !hitLiquids, false);

        if(player.level().isClientSide){
            for(int i = 0; i < 10; i++){
                double dx1 = player.xo;
                double dy1 = player.yo + 2 * player.level().random.nextFloat();
                double dz1 = player.zo;
                player.level().addParticle(ParticleTypes.PORTAL, dx1, dy1, dz1, player.level().random.nextDouble() - 0.5,
                        player.level().random.nextDouble() - 0.5, player.level().random.nextDouble() - 0.5);
            }

            // TODO BIN BLINK EFFECT
            //Wizardry.proxy.playBlinkEffect(caster);
        }

        Entity toTeleport = teleportMount ? player.getVehicle() : player;

        if(rayTrace instanceof BlockHitResult blockHitResult){
            BlockPos pos = blockHitResult.getBlockPos();
            int maxThickness = 1;

            if(blockHitResult.getDirection() == Direction.UP) maxThickness++;

            for(int i = 0; i <= maxThickness; i++) {
                BlockPos pos1 = BlockPos.of(BlockPos.offset(i, blockHitResult.getDirection().getOpposite()));

                // TODO  && !Wizardry.settings.teleportThroughUnbreakableBlocks
                if ((BlockUtil.isBlockUnbreakable(player.level(), pos1) || BlockUtil.isBlockUnbreakable(player.level(), pos1.relative(Direction.UP)))){
                    EBLogger.error(Component.literal("unbreakable block found"));
                    break;
                }

                Vec3 vec = GeometryUtil.getFaceCentre(pos1, Direction.DOWN);
                // TODO TICKSINUSE
                attemptTeleport(player.level(), toTeleport, vec, teleportMount, player, 1);
            }

            // If no suitable position was found on the other side of the wall, works like blink instead
            pos = pos.offset(blockHitResult.getDirection().getNormal());

            Vec3 vec = GeometryUtil.getFaceCentre(pos, Direction.DOWN);
            attemptTeleport(player.level(), toTeleport, vec, teleportMount, player, 1);

        }else{ // The ray trace missed
            EBLogger.error(Component.literal("Raytrace missed"));
            Vec3 vec = player.position().add(player.getLookAngle().scale(range));
            attemptTeleport(player.level(), toTeleport, vec, teleportMount, player, 1);
        }
    }

    protected boolean attemptTeleport(Level world, Entity toTeleport, Vec3 destination, boolean teleportMount, Player caster, int ticksInUse){
        destination = EntityUtil.findSpaceForTeleport(toTeleport, destination, teleportMount);

        if(destination != null){
            EBLogger.info(Component.literal("Destination: " + destination));
            // TODO SPELL SOUND
            //this.playSound(world, caster, ticksInUse, -1, modifiers);

            if(!teleportMount && caster.getVehicle() != null) caster.stopRiding();
            if(!world.isClientSide) toTeleport.setPos(destination.x, destination.y, destination.z);

            // TODO SPELL SOUND
            //this.playSound(world, caster, ticksInUse, -1, modifiers);
            return true;
        }

        EBLogger.error(Component.literal("No space found"));
        return false;
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
