package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Tiers;
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
    public Whirlwind(){
        this.soundValues(0.8f, 0.7f, 0.2f);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if(!(ctx instanceof PlayerCastContext playerCtx)) return false;
        if(!(entityHit.getEntity() instanceof LivingEntity target)) return false;

        if (target instanceof Player && !EBConfig.playersMoveEachOther) {
            playerCtx.caster().displayClientMessage(Component.translatable("spell.resist", target.getName(),
                    this.getDescriptionId()), true);
            return false;
        }

        Vec3 vec = target.getEyePosition(1).subtract(origin).normalize();
        if (!ctx.world().isClientSide) {
            float velocity = property(DefaultProperties.SPEED);

            target.setDeltaMovement(vec.x * velocity, vec.y * velocity + 1, vec.z * velocity);

            if (target instanceof ServerPlayer) {
                ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
            }
        } else {
            double distance = target.distanceToSqr(origin.x, origin.y, origin.z);

            for (int i = 0; i < 10; i++) {
                double x = origin.x + ctx.world().random.nextDouble() - 0.5 + vec.x * distance * 0.5;
                double y = origin.y + ctx.world().random.nextDouble() - 0.5 + vec.y * distance * 0.5;
                double z = origin.z + ctx.world().random.nextDouble() - 0.5 + vec.z * distance * 0.5;
                ctx.world().addParticle(ParticleTypes.CLOUD, x, y, z, vec.x, vec.y, vec.z);
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
                .assignBaseProperties(Tiers.APPRENTICE, Elements.EARTH, SpellType.DEFENCE, SpellAction.POINT, 10, 0, 15)
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.SPEED, 1.5F)
                .build();
    }
}
