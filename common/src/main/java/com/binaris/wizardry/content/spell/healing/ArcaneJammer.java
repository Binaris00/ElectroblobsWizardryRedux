package com.binaris.wizardry.content.spell.healing;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ArcaneJammer extends RaySpell {
    /** Random number generator used to coordinate whether spellcasting works or not. */
    private static final Random random = new Random();
    /** The number of ticks between updates of whether spellcasting works or not. */
    private static final int UPDATE_INTERVAL = 15;

    public ArcaneJammer() {
        this.soundValues(0.7f, 1, 0.4f);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target) {
            if (!ctx.world().isClientSide) {
                target.addEffect(new MobEffectInstance(EBMobEffects.ARCANE_JAMMER.get(),
                        (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION)),
                        (int) (property(DefaultProperties.EFFECT_STRENGTH) * ctx.modifiers().get(SpellModifiers.POTENCY) - 1)));
            }
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8))
                .color(0.9f, 0.3f, 0.7f)
                .spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellType.ALTERATION, SpellAction.POINT, 30, 5, 50)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.EFFECT_DURATION, 300)
                .add(DefaultProperties.EFFECT_STRENGTH, 0)
                .build();
    }

    public static void onSpellCastPreEvent(SpellCastEvent.Pre event){
        if (event.getCaster() == null || !event.getCaster().hasEffect(EBMobEffects.ARCANE_JAMMER.get())) {
            return;
        }
        random.setSeed(event.getLevel().getGameTime() / UPDATE_INTERVAL);
        random.nextInt(2);

        if(random.nextInt(event.getCaster().getEffect(EBMobEffects.ARCANE_JAMMER.get()).getAmplifier() + 2) > 0){
            event.setCanceled(true);

            event.getLevel().playSound(event.getCaster(), event.getCaster().blockPosition(), EBSounds.MISC_SPELL_FAIL.get(), SoundSource.MASTER, 1.0F, 1.0F);

            if(!event.getLevel().isClientSide){
                for(int i = 0; i < 5; i++){
                    double x = event.getCaster().xo + 0.5f * (event.getLevel().random.nextFloat() - 0.5f);
                    double y = event.getCaster().yo + event.getCaster().getBbHeight() / 2 + 0.5f * (event.getLevel().random.nextFloat() - 0.5f);
                    double z = event.getCaster().zo + 0.5f * (event.getLevel().random.nextFloat() - 0.5f);
                    ((ServerLevel) event.getLevel()).sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 1, 0.5, 0.5, 0.5, 0);
                }
            }
        }
    }
}
