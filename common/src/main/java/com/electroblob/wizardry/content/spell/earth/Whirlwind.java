package com.electroblob.wizardry.content.spell.earth;

import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.core.EBConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Whirlwind extends RaySpell {
    public Whirlwind(){
        this.soundValues(0.8f, 0.7f, 0.2f);
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if(!(caster instanceof Player player)) return false;
        if(!(target instanceof LivingEntity)) return false;

        if (target instanceof Player && !EBConfig.playersMoveEachOther) {
            player.displayClientMessage(Component.translatable("spell.resist", target.getName(),
                    this.getLocation()), true);
            return false;
        }

        Vec3 vec = target.getEyePosition(1).subtract(origin).normalize();
        if (!world.isClientSide) {
            float velocity = property(DefaultProperties.SPEED);

            target.setDeltaMovement(vec.x * velocity, vec.y * velocity + 1, vec.z * velocity);

            if (target instanceof ServerPlayer) {
                ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
            }
        } else {
            double distance = target.distanceToSqr(origin.x, origin.y, origin.z);

            for (int i = 0; i < 10; i++) {
                double x = origin.x + world.random.nextDouble() - 0.5 + vec.x * distance * 0.5;
                double y = origin.y + world.random.nextDouble() - 0.5 + vec.y * distance * 0.5;
                double z = origin.z + world.random.nextDouble() - 0.5 + vec.z * distance * 0.5;
                world.addParticle(ParticleTypes.CLOUD, x, y, z, vec.x, vec.y, vec.z);
            }
        }

        return true;
    }
    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        return false;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.SPEED, 1.5F)
                .build();
    }
}
