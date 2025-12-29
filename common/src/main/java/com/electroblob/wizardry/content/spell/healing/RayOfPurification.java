package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.client.util.ClientUtils;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.MagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RayOfPurification extends RaySpell {
    private static final SpellProperty<Float> UNDEAD_DAMAGE_MULTIPLIER = SpellProperty.floatProperty("undead_damage_multiplier", 2);

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;

        if (MagicDamageSource.isEntityImmune(EBDamageSources.RADIANT, target)) {
            if (!ctx.world().isClientSide() && ctx.castingTicks() == 1)
                ctx.caster().sendSystemMessage(Component.translatable("spell.resist", target.getName(),
                        this.getDescriptionFormatted())
                );
            return false;
        }
        if (ctx.castingTicks() % 10 != 0) return true;
        float damage = property(UNDEAD_DAMAGE_MULTIPLIER) * ctx.modifiers().get(SpellModifiers.POTENCY);
        if (target.isInvertedHealAndHarm()) damage *= property(UNDEAD_DAMAGE_MULTIPLIER);

        EntityUtil.attackEntityWithoutKnockback(target,
                MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.RADIANT), damage);

        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get()))));

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
    protected void spawnParticleRay(CastContext ctx, Vec3 origin, Vec3 direction, double distance) {
        if (ctx.caster() != null) {
            ParticleBuilder.create(EBParticles.BEAM).entity(ctx.caster()).pos(origin.subtract(ctx.caster().position()))
                    .length(distance).color(1, 0.6f + 0.3f * ctx.world().random.nextFloat(), 0.2f)
                    .scale((float) (Math.sin(ctx.caster().tickCount * 0.2f) * 0.1f + 1.4f)).spawn(ctx.world());
        } else {
            ParticleBuilder.create(EBParticles.BEAM).pos(origin).target(origin.add(direction.scale(distance)))
                    .color(1, 0.6f + 0.3f * ctx.world().random.nextFloat(), 0.2f)
                    .scale((float) (Math.sin(ClientUtils.getPlayer().tickCount * 0.2f) * 0.1f + 1.4f)).spawn(ctx.world());
        }
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellType.ATTACK, SpellAction.POINT, 10, 10, 40)
                .add(DefaultProperties.DAMAGE, 2.0F)
                .add(DefaultProperties.EFFECT_DURATION, 100)
                .add(DefaultProperties.RANGE, 10F)
                .add(UNDEAD_DAMAGE_MULTIPLIER)
                .build();
    }
}
