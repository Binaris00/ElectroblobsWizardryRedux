package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.GeometryUtil;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PhaseStep extends Spell {
    public static final SpellProperty<Integer> WALL_THICKNESS = SpellProperty.intProperty("wall_thickness", 1);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        Level world = ctx.world();

        boolean teleportMount = caster.getVehicle() != null
                && EBAccessoriesIntegration.isEquipped(caster, EBItems.CHARM_MOUNT_TELEPORTING.get());
        boolean hitLiquids = teleportMount && caster.getVehicle() instanceof Boat;
        double range = property(DefaultProperties.RANGE);

        HitResult trace = RayTracer.standardBlockRayTrace(world, caster, range, hitLiquids, !hitLiquids, false);

        if (world.isClientSide) {
            for (int i = 0; i < 10; i++) {
                double x = caster.getX();
                double y = caster.getY() + 2 * world.random.nextFloat();
                double z = caster.getZ();
                world.addParticle(ParticleTypes.PORTAL, x, y, z,
                        world.random.nextDouble() - 0.5,
                        world.random.nextDouble() - 0.5,
                        world.random.nextDouble() - 0.5);
            }
        }

        Entity subject = teleportMount ? caster.getVehicle() : caster;

        if (!(trace instanceof BlockHitResult hit)) {
            Vec3 dest = caster.position().add(caster.getLookAngle().scale(range));
            return attemptTeleport(world, subject, dest, teleportMount, caster);
        }

        BlockPos hitPos = hit.getBlockPos();
        int maxThickness = property(WALL_THICKNESS);
        if (hit.getDirection() == Direction.UP) maxThickness++;

        Direction face = hit.getDirection();
        for (int i = 1; i <= maxThickness; i++) {
            BlockPos tryPos = hitPos.relative(face, i);
            if (BlockUtil.isBlockUnbreakable(world, tryPos) ||
                    BlockUtil.isBlockUnbreakable(world, tryPos.relative(Direction.UP))) {
                break;
            }
            Vec3 dest = GeometryUtil.getFaceCentre(tryPos, Direction.UP);
            if (attemptTeleport(world, subject, dest, teleportMount, caster)) return true;
        }

        Vec3 fallback = GeometryUtil.getFaceCentre(hitPos.relative(face), Direction.UP);
        return attemptTeleport(world, subject, fallback, teleportMount, caster);
    }

    protected boolean attemptTeleport(Level world, Entity toTeleport, Vec3 destination, boolean teleportMount, Player caster) {
        Vec3 resolved = EntityUtil.findSpaceForTeleport(toTeleport, destination, teleportMount);
        if (resolved == null) return false;

        playSound(world, caster, 0, -1);

        if (!teleportMount && caster.getVehicle() != null) caster.stopRiding();

        if (!world.isClientSide) {
            if (toTeleport instanceof ServerPlayer sp) sp.teleportTo(resolved.x, resolved.y, resolved.z);
            else toTeleport.setPos(resolved.x, resolved.y, resolved.z);
        }

        playSound(world, caster, 0, -1);
        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellType.UTILITY, SpellAction.POINT, 35, 0, 40)
                .add(WALL_THICKNESS)
                .add(DefaultProperties.RANGE, 8F)
                .build();
    }
}
