package com.binaris.wizardry.content.spell.ice;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.*;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.AreaEffectSpell;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class IceAge extends AreaEffectSpell {
    private static final SpellProperty<Integer> FREEZE_DURATION = SpellProperty.intProperty("freeze_duration", 1200);

    public IceAge() {
        this.soundValues(1.5f, 1.0f, 0);
        this.alwaysSucceed(true);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        freezeNearbyBlocks(ctx.world(), ctx.caster().position(), ctx.caster(), ctx.modifiers());

        return super.cast(ctx);
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        freezeNearbyBlocks(ctx.world(), ctx.vec3(), null, ctx.modifiers());
        return super.cast(ctx);
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        freezeNearbyBlocks(ctx.world(), ctx.caster().position(), ctx.caster(), ctx.modifiers());
        return super.cast(ctx);
    }

    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        target.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())),
                property(DefaultProperties.EFFECT_STRENGTH)));

        target.playSound(EBSounds.MISC_FREEZE.get(), 1.0F, ctx.world().random.nextFloat() * 0.4F + 0.8F);

        // TODO ICE STATUE (for just living)
//            if(((BlockStatue)WizardryBlocks.ice_statue).convertToStatue((EntityLiving)target,
//                    caster, (int)(getProperty(FREEZE_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)))){
//                target.playSound(WizardrySounds.MISC_FREEZE, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
//            }

        return true;
    }

    @Override
    protected void spawnParticleEffect(CastContext ctx, Vec3 origin, double radius) {
        RandomSource random = ctx.world().random;

        for (int i = 0; i < 100; i++) {
            float r = random.nextFloat();
            double speed = 0.02 / r * (1 + random.nextDouble());
            ParticleBuilder.create(EBParticles.SNOW)
                    .pos(origin.x, origin.y + random.nextDouble() * 3, origin.z)
                    .velocity(0, 0, 0)
                    .spin(random.nextDouble() * (radius - 0.5) + 0.5, speed)
                    .shaded(true).scale(2)
                    .spawn(ctx.world());
        }

        for (int i = 0; i < 60; i++) {
            float r = random.nextFloat();
            double speed = 0.02 / r * (1 + random.nextDouble());
            ParticleBuilder.create(EBParticles.CLOUD)
                    .pos(origin.x, origin.y + random.nextDouble() * 2.5, origin.z)
                    .spin(random.nextDouble() * (radius - 1) + 0.5, speed)
                    .color(0xffffff).shaded(true)
                    .spawn(ctx.world());
        }
    }

    private void freezeNearbyBlocks(Level world, Vec3 origin, @Nullable LivingEntity caster, SpellModifiers modifiers) {
        if (world.isClientSide() || !EntityUtil.canDamageBlocks(caster, world)) return;

        double radius = property(DefaultProperties.EFFECT_RADIUS).floatValue() * modifiers.get(EBItems.BLAST_UPGRADE.get());

        for (int i = -(int) radius; i <= (int) radius; i++) {
            for (int j = -(int) radius; j <= (int) radius; j++) {
                BlockPos pos = BlockPos.containing(origin).offset(i, 0, j);
                Integer y = BlockUtil.getNearestSurface(world, new BlockPos(pos), Direction.UP, (int) radius, true, BlockUtil.SurfaceCriteria.SOLID_LIQUID_TO_AIR);
                if (y == null) continue;

                pos = new BlockPos(pos.getX(), y, pos.getZ());
                double dist = origin.distanceTo(new Vec3(origin.x + i, y, origin.z + j));

                if (y != -1 && world.random.nextInt((int) (dist * 2) + 1) < radius && dist < radius && BlockUtil.canPlaceBlock(caster, world, pos)) {
                    BlockUtil.freeze(world, pos.below(), true);
                }
            }
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.ICE, SpellType.ATTACK, SpellAction.POINT_DOWN, 70, 20, 250)
                .add(DefaultProperties.EFFECT_RADIUS, 7)
                .add(FREEZE_DURATION)
                .add(DefaultProperties.EFFECT_DURATION, 400)
                .add(DefaultProperties.EFFECT_STRENGTH, 1)
                .build();
    }
}
