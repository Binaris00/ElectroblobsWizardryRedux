package com.electroblob.wizardry.common.content.spell.earth;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.entity.construct.BubbleConstruct;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Bubble extends RaySpell {

    public Bubble(){
        this.soundValues(0.5f, 1.1f, 0.2f);
    }

    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return true;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if (target instanceof LivingEntity) {
            if (!world.isClientSide) {
                target.hurt(EBMagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.SORCERY, false), 1);
                BubbleConstruct bubble = new BubbleConstruct(world);
                bubble.setPos(target.getX(), target.getY(), target.getZ());
                bubble.setCaster((Player) caster);
                bubble.lifetime = 200;
                bubble.isDarkOrb = false;
                bubble.damageMultiplier = 1;

                world.addFreshEntity(bubble);
                target.startRiding(bubble);
            }
        }

        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        world.addParticle(ParticleTypes.SPLASH, x, y, z, 0, 0, 0);
        ParticleBuilder.create(EBParticles.MAGIC_BUBBLE).pos(x, y, z).spawn(world);
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
