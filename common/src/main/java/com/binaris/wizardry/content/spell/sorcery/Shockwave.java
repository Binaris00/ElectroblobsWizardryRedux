package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.client.ScreenShakeHandler;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.AreaEffectSpell;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Shockwave extends AreaEffectSpell {
    public static final SpellProperty<Float> MAX_REPULSION_VELOCITY = SpellProperty.floatProperty("max_repulsion_velocity", 3F);

    private static final double EPICENTRE_RADIUS = 1;
    private static final float MIN_KNOCKBACK_FACTOR = 0.5F;
    private static final float MAX_KNOCKBACK_FACTOR = 1.1F;

    public Shockwave() {
        this.soundValues(2, 0.5f, 0);
        this.alwaysSucceed(true);
    }

    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        float radius = ctx.modifiers().get(SpellModifiers.BLAST, property(DefaultProperties.EFFECT_RADIUS));

        if (target instanceof Player) {
            if (!EBServerConfig.PLAYERS_MOVE_EACH_OTHER.get()) return false;

            if (ArtifactChannel.isEquipped((Player) target, EBItems.AMULET_ANCHORING.get())) {
                if (!ctx.world().isClientSide && ctx.caster() instanceof Player caster)
                    caster.displayClientMessage(Component.translatable("spell.resist", target.getName(), this.getDescriptionFormatted()), true);
                return false;
            }
        }

        float proximity = (float) (1 - (Math.max(origin.distanceTo(target.position()) - EPICENTRE_RADIUS, 0) / (radius - EPICENTRE_RADIUS)));

        if (!ctx.world().isClientSide) {
            DamageSource source = ctx.caster() != null
                    ? MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.BLAST)
                    : ctx.world().damageSources().magic();

            target.hurt(source, property(DefaultProperties.DAMAGE) * proximity * ctx.modifiers().get(SpellModifiers.POTENCY, 1F));

            double velocityFactor = proximity * property(MAX_REPULSION_VELOCITY) * getHealthKnockbackFactor(ctx.caster(), target);
            Vec3 motion = new Vec3(target.getX() - origin.x, target.getY() + 1 - origin.y, target.getZ() - origin.z).scale(velocityFactor);

            target.setDeltaMovement(motion);
            target.hurtMarked = true;
        }

        return true;
    }

    private static double getHealthKnockbackFactor(LivingEntity caster, LivingEntity target) {
        double referenceHealth = caster != null ? caster.getMaxHealth() : 20;
        double ratio = referenceHealth / Math.max(target.getMaxHealth(), 1);
        return Mth.clamp(ratio, MIN_KNOCKBACK_FACTOR, MAX_KNOCKBACK_FACTOR);
    }

    @Override
    protected void spawnParticleEffect(CastContext ctx, Vec3 origin, double radius) {
        ScreenShakeHandler.shakeScreenNear(origin, radius, 10F);

        for (int i = 0; i < 40; i++) {
            double particleX = origin.x - 1.0 + 2 * ctx.world().random.nextDouble();
            double particleZ = origin.z - 1.0 + 2 * ctx.world().random.nextDouble();
            BlockState block = ctx.world().getBlockState(BlockPos.containing(origin.x, origin.y - 0.5, origin.z));

            if (!block.isAir()) {
                ctx.world().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block),
                        particleX, origin.y, particleZ, particleX - origin.x, 0, particleZ - origin.z);
            }
        }

        ParticleBuilder.create(EBParticles.SPHERE).pos(origin.add(0, 0.1, 0)).scale((float) radius * 0.8f)
                .color(0.8f, 0.9f, 1).spawn(ctx.world());

        ctx.world().addParticle(ParticleTypes.EXPLOSION_EMITTER, origin.x, origin.y + 0.1, origin.z, 0, 0, 0);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.SORCERY, SpellTypes.ATTACK, SpellAction.POINT_DOWN, 65, 50, 150)
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.DAMAGE, 8F)
                .add(MAX_REPULSION_VELOCITY, 3F)
                .build();
    }
}
