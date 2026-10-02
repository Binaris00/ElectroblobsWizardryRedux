package com.binaris.wizardry.content.spell.earth;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Whirlwind extends RaySpell {
    private static final double BEAM_RADIUS = 1.3;

    public Whirlwind() {
        this.soundValues(0.8f, 0.7f, 0.2f);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        this.breakNearbyPlants(ctx);
        return super.cast(ctx);
    }

    private void breakNearbyPlants(CastContext ctx) {
        if (ctx.world().isClientSide) return;
        if (!(ctx.caster() instanceof Player player)) return;

        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        Vec3 origin = player.getEyePosition(1);
        Vec3 direction = player.getLookAngle();
        Vec3 end = origin.add(direction.scale(range));

        BlockPos min = BlockPos.containing(origin).offset((int) -BEAM_RADIUS, (int) -BEAM_RADIUS, (int) -BEAM_RADIUS);
        BlockPos max = BlockPos.containing(end).offset((int) BEAM_RADIUS, (int) BEAM_RADIUS, (int) BEAM_RADIUS);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Vec3 rel = blockCenter.subtract(origin);
            double t = rel.dot(direction);
            if (t < 0 || t > range) continue;
            if (blockCenter.distanceToSqr(origin.add(direction.scale(t))) > BEAM_RADIUS * BEAM_RADIUS) continue;

            BlockState state = ctx.world().getBlockState(pos);
            if (state.isAir() || state.getDestroySpeed(ctx.world(), pos) != 0.0F) continue;

            if (BlockUtil.canBreak(player, ctx.world(), pos, false)) ctx.world().destroyBlock(pos, true, player);
        }
    }

    private void knockbackToEntities(CastContext ctx, Vec3 origin) {
        if (!(ctx.caster() instanceof Player player)) return;

        float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
        Vec3 direction = player.getLookAngle();

        AABB aabb = new AABB(BlockPos.containing(origin), BlockPos.containing(origin.add(direction.scale(range)))).inflate(BEAM_RADIUS);
        List<LivingEntity> entities = ctx.world().getEntitiesOfClass(LivingEntity.class, aabb, e -> e != player);

        for (LivingEntity target : entities) {
            Vec3 center = target.getEyePosition(1);
            Vec3 rel = center.subtract(origin);
            double t = rel.dot(direction);
            if (t < 0 || t > range) continue;
            if (center.distanceToSqr(origin.add(direction.scale(t))) > BEAM_RADIUS * BEAM_RADIUS) continue;

            if (target instanceof Player && !EBServerConfig.PLAYERS_MOVE_EACH_OTHER.get()) {
                player.displayClientMessage(Component.translatable("spell.resist", target.getName(),
                        this.getDescriptionId()), true);
                continue;
            }

            Vec3 vec = center.subtract(origin).normalize();
            if (!ctx.world().isClientSide) {
                float velocity = ctx.modifiers().get(SpellModifiers.POTENCY, property(DefaultProperties.SPEED));

                target.setDeltaMovement(vec.x * velocity, vec.y * velocity + 0.3, vec.z * velocity);

                if (target instanceof ServerPlayer) {
                    ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
                }
            }
        }
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        this.knockbackToEntities(ctx, origin);
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        this.knockbackToEntities(ctx, origin);
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        this.knockbackToEntities(ctx, origin);
        return true;
    }

    @Override
    public boolean canCastByEntity() {
        return false;
    }

    @Override
    public boolean canCastByLocation() {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.CLOUD, x, y, z, 0, 0, 0);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellTypes.DEFENCE, SpellAction.POINT, 15, 0, 15)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.SPEED, 0.7F)
                .build();
    }
}