package com.electroblob.wizardry.content.spell.ice;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.LocationCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.content.entity.construct.IceBarrierConstruct;
import com.electroblob.wizardry.setup.registries.EBEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FrostBarrier extends Spell {
    private static final double BARRIER_DISTANCE = 2;
    private static final double BARRIER_ARC_RADIUS = 10;
    private static final double BARRIER_SPACING = 1.4;

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.caster().onGround()) {
            if (!createBarriers(ctx.world(), ctx.caster().position(), ctx.caster().getLookAngle(), ctx.caster()))
                return false;
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return true;
        }

        return false;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (ctx.caster().onGround()) {
            if (!createBarriers(ctx.world(), ctx.caster().position(), ctx.target().position().subtract(ctx.caster().position()), ctx.caster()))
                return false;
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return true;
        }

        return false;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        if (!createBarriers(ctx.world(), ctx.vec3(), new Vec3(ctx.direction().step()), null)) return false;

        this.playSound(ctx.world(), ctx.x() - ctx.direction().getStepX(),
                ctx.y() - ctx.direction().getStepY(), ctx.z() - ctx.direction().getStepZ(),
                ctx.castingTicks(), ctx.duration());
        return true;
    }

    private boolean createBarriers(Level world, Vec3 origin, Vec3 direction, @Nullable LivingEntity caster) {
        if (!world.isClientSide) {
            direction = GeometryUtil.horizontalise(direction);
            Vec3 centre = origin.add(direction.scale(BARRIER_DISTANCE - BARRIER_ARC_RADIUS));

            List<IceBarrierConstruct> barriers = new ArrayList<>();

            int barrierCount = 3;

            for (int i = 0; i < barrierCount; i++) {
                IceBarrierConstruct barrier = createBarrier(world, centre, direction.yRot((float) (BARRIER_SPACING / BARRIER_ARC_RADIUS) * i), caster, barrierCount, i);
                if (barrier != null) barriers.add(barrier);

                if (i == 0) continue;
                barrier = createBarrier(world, centre, direction.yRot(-(float) (BARRIER_SPACING / BARRIER_ARC_RADIUS) * i), caster, barrierCount, i);
                if (barrier != null) barriers.add(barrier);
            }

            if (barriers.isEmpty()) return false;

            barriers.forEach(world::addFreshEntity);
        }

        return true;
    }


    private IceBarrierConstruct createBarrier(Level world, Vec3 centre, Vec3 direction, @Nullable LivingEntity caster, int barrierCount, int index) {
        Vec3 position = centre.add(direction.scale(BARRIER_ARC_RADIUS));
        Integer floor = BlockUtil.getNearestFloor(world, BlockPos.containing(position), 3);
        if (floor == null) return null;
        position = GeometryUtil.replaceComponent(position, Axis.Y, floor);

        float scale = 1.5f - (float) index / barrierCount * 0.5f;
        double yOffset = 1.5 * scale;

        IceBarrierConstruct barrier = new IceBarrierConstruct(EBEntities.ICE_BARRIER.get(), world);
        barrier.setPos(position.x, position.y - yOffset, position.z);
        barrier.setCaster((Player) caster);
        barrier.lifetime = 400;
        barrier.damageMultiplier = 1;
        barrier.setRot((float) Math.toDegrees(Mth.atan2(-direction.x, direction.z)), barrier.getXRot());
        barrier.setSizeMultiplier(scale);
        barrier.setDelay(1 + 3 * index);

        if (!world.getEntitiesOfClass(barrier.getClass(), barrier.getBoundingBox().move(0, yOffset, 0)).isEmpty())
            return null;

        return barrier;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
