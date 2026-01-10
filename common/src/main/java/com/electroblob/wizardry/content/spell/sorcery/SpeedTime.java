package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpeedTime extends Spell {

    public static final SpellProperty<Float> TIME_INCREMENT = SpellProperty.floatProperty("time_increment", 30f);
    public static final SpellProperty<Integer> EXTRA_TICKS = SpellProperty.intProperty("extra_ticks", 1);

    public SpeedTime() {
        super();
        this.soundValues(0.4f, 1, 0);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Level world = ctx.world();
        Player caster = ctx.caster();
        boolean didAnything = false;

        float blastMod = ctx.modifiers().get(EBItems.BLAST_UPGRADE.get());
        // Advance world time on server
        if (!world.isClientSide) {
            long advance = (long) (property(TIME_INCREMENT) * blastMod);
            ((ServerLevel) world).setDayTime(world.getDayTime() + advance);
            didAnything = true;
        }

        double radius = property(DefaultProperties.EFFECT_RADIUS) * blastMod;
        int potencyLevel = (int) (((ctx.modifiers().get(SpellModifiers.POTENCY) - 1f) * 2f + 1f)
                * property(EXTRA_TICKS));

        // Advance non-player entities' ticks within radius
        List<Entity> entities = EntityUtil.getEntitiesWithinRadius(radius, caster.getX(), caster.getY(),
                caster.getZ(), world, Entity.class);
        if (!entities.isEmpty()) {
            entities.removeIf(e -> e instanceof Player);
            if (!entities.isEmpty()) {
                for (int i = 0; i < potencyLevel; i++) {
                    for (Entity e : entities) e.tick();
                }
                didAnything = true;
            }
        }

        // Advance random-ticking blocks in the sphere
        if (!world.isClientSide) {
            List<BlockPos> sphere = BlockUtil.getBlockSphere(caster.blockPosition(), radius);
            for (BlockPos pos : sphere) {
                BlockState state = world.getBlockState(pos);
                if (state.isRandomlyTicking()) {
                    for (int i = 0; i < potencyLevel; i++) {
                        state.randomTick((ServerLevel) world, pos, world.random);
                    }
                }
            }
        }

        // Client-side particles
        if (world.isClientSide) {
            double x = caster.getX() + 2;
            double y = caster.getY() + caster.getBbHeight() / 2;
            double z = caster.getZ();
            for (int i = 0; i < 2; i++) {
                ParticleBuilder.create(EBParticles.SPARKLE, world.random, x, y, z, 2, false)
                        .velocity(-0.25, 0, 0).time(16).color(1f, 1f, 1f).spawn(world);

                ParticleBuilder.create(EBParticles.FLASH, world.random, x, y, z, 2, false)
                        .velocity(-0.25, 0, 0).time(16).scale(0.5f)
                        .color(0.6f + world.random.nextFloat() * 0.4f,
                                0.6f + world.random.nextFloat() * 0.4f,
                                0.6f + world.random.nextFloat() * 0.4f).spawn(world);
            }
        }

        this.playSound(world, caster, ctx.castingTicks(), -1);
        return didAnything;
    }

    @Override
    protected void playSound(Level world, LivingEntity entity, int castTicks, int duration) {
        this.playSoundLoop(world, entity, castTicks);
    }

    @Override
    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration) {
        this.playSoundLoop(world, x, y, z, ticksInUse, duration);
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.SORCERY,
                        SpellType.UTILITY, SpellAction.POINT_UP, 15, 0, 0)
                .add(DefaultProperties.EFFECT_RADIUS)
                .add(TIME_INCREMENT)
                .add(EXTRA_TICKS)
                .build();
    }
}
