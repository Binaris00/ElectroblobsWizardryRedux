package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.api.content.util.RayTracer;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PhaseStep extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        // TODO ItemArtefact.isArtefactActive(player, WizardryItems.charm_mount_teleporting)
        boolean teleportMount = ctx.caster().getVehicle() != null;
        boolean hitLiquids = teleportMount && ctx.caster().getVehicle() instanceof Boat;

        double range = 8;

        HitResult rayTrace = RayTracer.standardBlockRayTrace(ctx.world(), ctx.caster(),
                range, hitLiquids, !hitLiquids, false);

        if(ctx.world().isClientSide){
            for(int i = 0; i < 10; i++){
                double dx1 = ctx.caster().xo;
                double dy1 = ctx.caster().yo + 2 * ctx.world().random.nextFloat();
                double dz1 = ctx.caster().zo;
                ctx.world().addParticle(ParticleTypes.PORTAL, dx1, dy1, dz1, ctx.world().random.nextDouble() - 0.5,
                        ctx.world().random.nextDouble() - 0.5, ctx.world().random.nextDouble() - 0.5);
            }

            // TODO BIN BLINK EFFECT
            //Wizardry.proxy.playBlinkEffect(caster);
        }

        Entity toTeleport = teleportMount ? ctx.caster().getVehicle() : ctx.caster();

        if(rayTrace instanceof BlockHitResult blockHitResult){
            BlockPos pos = blockHitResult.getBlockPos();
            int maxThickness = 1;

            if(blockHitResult.getDirection() == Direction.UP) maxThickness++;

            for(int i = 0; i <= maxThickness; i++) {
                BlockPos pos1 = BlockPos.of(BlockPos.offset(i, blockHitResult.getDirection().getOpposite()));

                // TODO  && !Wizardry.settings.teleportThroughUnbreakableBlocks
                if ((BlockUtil.isBlockUnbreakable(ctx.world(), pos1) || BlockUtil.isBlockUnbreakable(ctx.world(), pos1.relative(Direction.UP)))){
                    break;
                }

                Vec3 vec = GeometryUtil.getFaceCentre(pos1, Direction.DOWN);
                attemptTeleport(ctx.world(), toTeleport, vec, teleportMount, ctx.caster(), ctx.castingTicks());
            }

            // If no suitable position was found on the other side of the wall, works like blink instead
            pos = pos.offset(blockHitResult.getDirection().getNormal());

            Vec3 vec = GeometryUtil.getFaceCentre(pos, Direction.DOWN);
            return attemptTeleport(ctx.world(), toTeleport, vec, teleportMount, ctx.caster(), 1);

        }else{ // The ray trace missed
            Vec3 vec = ctx.caster().position().add(ctx.caster().getLookAngle().scale(range));
            return attemptTeleport(ctx.world(), toTeleport, vec, teleportMount, ctx.caster(), 1);
        }
    }

    protected boolean attemptTeleport(Level world, Entity toTeleport, Vec3 destination, boolean teleportMount, Player caster, int ticksInUse){
        destination = EntityUtil.findSpaceForTeleport(toTeleport, destination, teleportMount);

        if(destination != null){
            this.playSound(world, caster, 0, -1);

            if(!teleportMount && caster.getVehicle() != null) caster.stopRiding();
            if(!world.isClientSide) toTeleport.setPos(destination.x, destination.y, destination.z);

            this.playSound(world, caster, 0, -1);
            return true;
        }
        return false;
    }

    // TODO PROPERTIES

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellType.UTILITY, SpellAction.POINT, 35, 0, 40)
                .build();
    }
}
