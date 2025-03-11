package com.electroblob.wizardry.common.content.spell.earth;

import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
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
        // TODO BIN SPELL SOUND
        //this.soundValues(0.8f, 0.7f, 0.2f);
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        // TODO BIN Wizardry.settings.playersMoveEachOther
        if (target instanceof Player && ((caster instanceof Player && !false))) {
            if (!world.isClientSide)
                ((Player) caster).displayClientMessage(Component.translatable("spell.resist", target.getName(), this.getLocation()), true);
            return false;
        }

        if (target instanceof LivingEntity) {
            Vec3 vec = target.getEyePosition(1).subtract(origin).normalize();
            if (!world.isClientSide) {
                float velocity = 1.5f;

                target.setDeltaMovement(vec.x * velocity, vec.y * velocity + 1, vec.z * velocity);

                if (target instanceof ServerPlayer) {
                    ((ServerPlayer) target).connection.send(new ClientboundSetEntityMotionPacket(target));
                }
            }

            if (world.isClientSide) {
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

        return false;
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
        return null;
    }
}
