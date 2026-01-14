package com.binaris.wizardry.content.spell.necromancy;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.AreaEffectSpell;
import com.binaris.wizardry.content.spell.abstr.BuffSpell;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PlagueOfDarkness extends AreaEffectSpell {

    public PlagueOfDarkness() {
        this.alwaysSucceed(true);
        this.soundValues(1, 1.1f, 0.2f);
    }

    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        if (!MagicDamageSource.isEntityImmune(EBDamageSources.WITHER, target)) {
            if (ctx.world().isClientSide) return true;
            target.hurt(target.damageSources().wither(), property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
            target.addEffect(new MobEffectInstance(MobEffects.WITHER,
                    (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())),
                    property(DefaultProperties.EFFECT_STRENGTH) + BuffSpell.getStandardBonusAmplifier(ctx.modifiers().get(SpellModifiers.POTENCY))));
        }

        return true;
    }

    @Override
    protected void spawnParticleEffect(CastContext ctx, Vec3 origin, double radius) {
        double particleX, particleZ;

        for (int i = 0; i < 40; i++) {
            particleX = origin.x - 1.0d + 2 * ctx.world().random.nextDouble();
            particleZ = origin.z - 1.0d + 2 * ctx.world().random.nextDouble();
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(particleX, origin.y, particleZ)
                    .velocity(particleX - origin.x, 0, particleZ - origin.z).color(0.1f, 0, 0).spawn(ctx.world());

            particleX = origin.x - 1.0d + 2 * ctx.world().random.nextDouble();
            particleZ = origin.z - 1.0d + 2 * ctx.world().random.nextDouble();
            ParticleBuilder.create(EBParticles.SPARKLE).pos(particleX, origin.y, particleZ)
                    .velocity(particleX - origin.x, 0, particleZ - origin.z).time(30).color(0.1f, 0, 0.05f).spawn(ctx.world());

            particleX = origin.x - 1.0d + 2 * ctx.world().random.nextDouble();
            particleZ = origin.z - 1.0d + 2 * ctx.world().random.nextDouble();

            BlockState state = ctx.world().getBlockState(new BlockPos((int) origin.x, (int) (origin.y - 0.5), (int) origin.z));

            if (state.getRenderShape() != RenderShape.INVISIBLE) {
                ctx.world().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), particleX, origin.y, particleZ, particleX - origin.x, 0, particleZ - origin.z);
            }
        }

        ParticleBuilder.create(EBParticles.SPHERE).pos(origin.add(0, 0.1, 0)).scale((float) radius * 0.8f).color(0.8f, 0, 0.05f).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.NECROMANCY, SpellType.ATTACK, SpellAction.POINT_DOWN, 75, 15, 200)
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.DAMAGE, 8F)
                .add(DefaultProperties.EFFECT_DURATION, 140)
                .add(DefaultProperties.EFFECT_STRENGTH, 2)
                .build();
    }
}
