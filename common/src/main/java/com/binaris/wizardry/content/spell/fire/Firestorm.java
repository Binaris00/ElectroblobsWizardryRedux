package com.binaris.wizardry.content.spell.fire;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.LocationCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.AreaEffectSpell;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Firestorm extends AreaEffectSpell {
    public Firestorm() {
        this.soundValues(2f, 1.0f, 0);
        this.alwaysSucceed(true);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        burnNearbyBlocks(ctx, ctx.caster().position());
        return super.cast(ctx);
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        burnNearbyBlocks(ctx, ctx.caster().position());
        return super.cast(ctx);
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        burnNearbyBlocks(ctx, ctx.vec3());
        return super.cast(ctx);
    }

    @Override
    public SpellAction getAction() {
        return SpellAction.POINT_DOWN;
    }

    @Override
    public int getChargeUp() {
        return 20;
    }

    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        if (!MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target))
            target.setSecondsOnFire(property(DefaultProperties.EFFECT_DURATION));
        return true;
    }

    private void burnNearbyBlocks(CastContext ctx, Vec3 origin) {
        if (ctx.world().isClientSide || !EntityUtil.canDamageBlocks(ctx.caster(), ctx.world())) return;

        double radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(EBItems.BLAST_UPGRADE.get());

        for (int i = -(int) radius; i <= (int) radius; i++) {
            for (int j = -(int) radius; j <= (int) radius; j++) {
                BlockPos pos = new BlockPos((int) origin.x(), (int) origin.y(), (int) origin.z()).offset(i, 0, j);
                Integer y = BlockUtil.getNearestSurface(ctx.world(), new BlockPos(pos), Direction.UP, (int) radius, true, BlockUtil.SurfaceCriteria.NOT_AIR_TO_AIR);

                if (y != null) {
                    pos = new BlockPos(pos.getX(), y, pos.getZ());

                    double dist = origin.distanceTo(new Vec3(origin.x + i, y, origin.z + j));

                    if (y != -1 && ctx.world().random.nextInt((int) (dist * 2) + 1) < radius && dist < radius && dist > 1.5 && BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), pos)) {
                        ctx.world().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                    }
                }
            }
        }
    }

    @Override
    protected void spawnParticleEffect(CastContext ctx, Vec3 origin, double radius) {
        for (int i = 0; i < 100; i++) {
            float r = ctx.world().random.nextFloat();
            double speed = 0.02 / r * (1 + ctx.world().random.nextDouble());
            ParticleBuilder.create(EBParticles.MAGIC_FIRE)
                    .pos(origin.x, origin.y + ctx.world().random.nextDouble() * 3, origin.z)
                    .velocity(0, 0, 0)
                    .scale(2)
                    .time(40 + ctx.world().random.nextInt(10))
                    .spin(ctx.world().random.nextDouble() * (radius - 0.5) + 0.5, speed)
                    .spawn(ctx.world());
        }

        for (int i = 0; i < 60; i++) {
            float r = ctx.world().random.nextFloat();
            double speed = 0.02 / r * (1 + ctx.world().random.nextDouble());
            ParticleBuilder.create(EBParticles.CLOUD)
                    .pos(origin.x, origin.y + ctx.world().random.nextDouble() * 2.5, origin.z)
                    .color(DrawingUtils.mix(DrawingUtils.mix(0xffbe00, 0xff3600, r / 0.6f), 0x222222, (r - 0.6f) / 0.4f))
                    .spin(r * (radius - 1) + 0.5, speed)
                    .spawn(ctx.world());
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.FIRE, SpellType.ATTACK, SpellAction.POINT_DOWN, 80, 20, 250)
                .add(DefaultProperties.EFFECT_RADIUS, 6)
                .add(DefaultProperties.EFFECT_DURATION, 15)
                .build();
    }
}
