package com.electroblob.wizardry.common.content.spell.fire;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FireBreath extends RaySpell {
    public FireBreath(){
        this.particleVelocity(1);
        this.particleJitter(0.3);
        this.particleSpacing(0.25);
        this.soundValues(3f, 1, 0);
    }

    // TODO SOUND LOOP

//    @Override
//    protected void playSound(Level world, LivingEntity entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
//        this.playSoundLoop(world, entity, ticksInUse);
//    }
//
//    @Override
//    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
//        this.playSoundLoop(world, x, y, z, ticksInUse, duration);
//    }

    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return true;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if (!EntityUtil.canDamageBlocks(caster, world)) return false;

        pos = pos.relative(side);

        if (world.isEmptyBlock(pos)) {
            if (!world.isClientSide && BlockUtil.canPlaceBlock(caster, world, pos))
                world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
            return true;
        }

        return false;
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if (target instanceof LivingEntity livingEntity) {
            // TODO Bin: Custom magic damage
//            if (MagicDamage.isEntityImmune(DamageType.FIRE, target)) {
//                if (!world.isClientSide && ticksInUse == 1 && caster instanceof Player)
//                    ((Player) caster).displayClientMessage(Component.translatable("spell.resist", target.getName(), this.getNameForTranslationFormatted()), true);
//
//            } else if (ticksInUse % ((LivingEntity) target).invulnerableDuration == 1) {
//                target.setSecondsOnFire((int) (getProperty(BURN_DURATION).floatValue() * modifiers.get(WizardryItems.DURATION_UPGRADE.get())));
//                EntityUtils.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, DamageType.FIRE), getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
//            }

            if (ticksInUse % ((LivingEntity) target).invulnerableDuration == 1) {
                target.setSecondsOnFire(10);
                //EntityUtil.attackEntityWithoutKnockback(target, MagicDamage.causeDirectMagicDamage(caster, DamageType.FIRE), getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
                EntityUtil.attackEntityWithoutKnockback(livingEntity, livingEntity.damageSources().indirectMagic(caster, livingEntity), 5);
            }
        }

        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).scale(2 + world.random.nextFloat()).collide(true).spawn(world);
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).scale(2 + world.random.nextFloat()).collide(true).spawn(world);
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
