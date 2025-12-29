package com.electroblob.wizardry.content.spell.lightning;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.MagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.AllyDesignation;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LightningPulse extends Spell {
    private static final SpellProperty<Float> REPULSION_VELOCITY = SpellProperty.floatProperty("repulsion_velocity", 0.8f);

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (!ctx.caster().onGround()) return false;
        float radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(EBItems.BLAST_UPGRADE.get());

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(radius, ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world());
        targets.removeIf(t -> !AllyDesignation.isValidTarget(ctx.caster(), t));
        for (LivingEntity target : targets) {
            target.hurt(
                    MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.SHOCK),
                    property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY)
            );

            if (!ctx.world().isClientSide()) {
                double dx = target.getX() - ctx.caster().getX();
                double dz = target.getZ() - ctx.caster().getZ();
                double vectorLength = Math.sqrt(dx * dx + dz * dz);
                dx /= vectorLength;
                dz /= vectorLength;

                target.setDeltaMovement(
                        property(REPULSION_VELOCITY) * dx,
                        0,
                        property(REPULSION_VELOCITY) * dz
                );

                if (target instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(target));
                }
            }
        }

        if (ctx.world().isClientSide()) {
            ParticleBuilder.create(EBParticles.LIGHTNING_PULSE)
                    .pos(ctx.caster().getX(), ctx.caster().getY() + GeometryUtil.ANTI_Z_FIGHTING_OFFSET, ctx.caster().getZ())
                    .scale(ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()))
                    .spawn(ctx.world());
        }

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.LIGHTNING, SpellType.ATTACK, SpellAction.POINT_DOWN, 25, 0, 75)
                .add(DefaultProperties.DAMAGE, 8.0F)
                .add(DefaultProperties.EFFECT_RADIUS, 3)
                .add(REPULSION_VELOCITY)
                .build();
    }
}
