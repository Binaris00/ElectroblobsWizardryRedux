package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.entity.construct.BubbleConstruct;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Entrapment extends RaySpell {
    public static final SpellProperty<Integer> DAMAGE_INTERVAL = SpellProperty.intProperty("damage_interval", 30);

    public Entrapment() {
        this.soundValues(1, 0.85f, 0.3f);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (ctx.world().isClientSide) return true;

        if (entityHit.getEntity() instanceof LivingEntity target) {
            DamageSource source = ctx.caster() != null ? EBMagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.SORCERY)
                    : target.damageSources().magic();
            target.hurt(source, 1);

            BubbleConstruct bubble = new BubbleConstruct(ctx.world());
            bubble.setPos(target.getX(), target.getY(), target.getZ());
            bubble.setCaster(ctx.caster());
            bubble.lifetime = ((int) (property(DefaultProperties.EFFECT_DURATION).floatValue() * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));
            bubble.isDarkOrb = true;
            bubble.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);

            ctx.world().addFreshEntity(bubble);
            target.startRiding(bubble);
        }

        return true;
    }


    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.PORTAL, x, y - 0.5, z, 0, 0, 0);
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.NECROMANCY, SpellType.ATTACK, SpellAction.POINT, 35, 10, 75)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.EFFECT_DURATION, 120)
                .add(DAMAGE_INTERVAL)
                .build();
    }
}
