package com.binaris.wizardry.content.spell.earth;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.config.EBConfig;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Whirlwind extends RaySpell {
    public Whirlwind() {
        this.soundValues(0.8f, 0.7f, 0.2f);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(ctx instanceof PlayerCastContext playerCtx)) return false;
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;

        if (target instanceof Player && !EBConfig.PLAYERS_MOVE_EACH_OTHER.get()) {
            playerCtx.caster().displayClientMessage(Component.translatable("spell.resist", target.getName(),
                    this.getDescriptionId()), true);
            return false;
        }

        Vec3 vec = target.getEyePosition(1).subtract(origin).normalize();
        if (!ctx.world().isClientSide) {
            float velocity = property(DefaultProperties.SPEED) * ctx.modifiers().get(SpellModifiers.POTENCY);

            target.setDeltaMovement(vec.x * velocity, vec.y * velocity + 1, vec.z * velocity);

            if (target instanceof ServerPlayer) {
                ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
            }
        } else {
            double distance = Math.sqrt(target.distanceToSqr(origin.x, origin.y, origin.z));

            for (int i = 0; i < 10; i++) {
                double t = ctx.world().random.nextDouble();
                double x = origin.x + vec.x * distance * t + (ctx.world().random.nextDouble() - 0.5) * 0.3;
                double y = origin.y + vec.y * distance * t + (ctx.world().random.nextDouble() - 0.5) * 0.3;
                double z = origin.z + vec.z * distance * t + (ctx.world().random.nextDouble() - 0.5) * 0.3;
                ctx.world().addParticle(ParticleTypes.CLOUD, x, y, z, vec.x * 0.3, vec.y * 0.3, vec.z * 0.3);
            }
        }

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
    public boolean canCastByEntity() {
        return false;
    }

    @Override
    public boolean canCastByLocation() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.EARTH, SpellType.DEFENCE, SpellAction.POINT, 10, 0, 15)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.SPEED, 1.5F)
                .build();
    }
}
