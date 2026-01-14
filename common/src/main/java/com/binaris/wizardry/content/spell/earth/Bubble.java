package com.binaris.wizardry.content.spell.earth;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.entity.construct.BubbleConstruct;
import com.binaris.wizardry.content.entity.living.AbstractWizard;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Bubble extends RaySpell {

    public Bubble() {
        this.soundValues(0.5f, 1.1f, 0.2f);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return true;
        if (ctx.world().isClientSide) return true;

        DamageSource source = ctx.caster() != null ? MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.SORCERY) : target.damageSources().magic();
        target.hurt(source, 1);

        // Wizards have a weird bug related to armors, so, avoiding a big render problem we're having them resist this spell
        // entirely for now.
        if (target instanceof AbstractWizard) {
            ctx.caster().sendSystemMessage(Component.translatable("spell.resist", target.getName(), this.getDescriptionFormatted()));
            return true;
        }

        BubbleConstruct bubble = new BubbleConstruct(ctx.world());
        bubble.setPos(target.getX(), target.getY(), target.getZ());
        if (ctx.caster() != null) bubble.setCaster(ctx.caster());
        bubble.lifetime = ((int) (property(DefaultProperties.DURATION).floatValue() * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));
        bubble.setDarkOrb(false);
        bubble.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
        ctx.world().addFreshEntity(bubble);
        target.startRiding(bubble);

        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.SPLASH, x, y, z, 0, 0, 0);
        ParticleBuilder.create(EBParticles.MAGIC_BUBBLE).pos(x, y, z).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellType.ATTACK, SpellAction.POINT, 15, 0, 20)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.DURATION, 200)
                .build();
    }
}
