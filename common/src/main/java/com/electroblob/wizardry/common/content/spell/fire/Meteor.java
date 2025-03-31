package com.electroblob.wizardry.common.content.spell.fire;

import com.electroblob.wizardry.api.common.spell.internal.Caster;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.entity.EntityMeteor;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Meteor extends RaySpell {

    @Override
    protected void perform(Caster caster) {
        // TODO ARTIFACT
//        if(ArtefactItem.isArtefactActive(caster, WizardryItems.RING_METEOR.get())){
//
//            if(!world.isClientSide){
//
//                EntityMeteor meteor = new EntityMeteor(world, caster.getX(), caster.getY() + caster.getEyeHeight(), caster.getZ(),
//                        modifiers.get(WizardryItems.BLAST_UPGRADE.get()), EntityUtils.canDamageBlocks(caster, world));
//
//                Vec3 direction = caster.getLookAngle().scale(2 * modifiers.get(WizardryItems.RANGE_UPGRADE.get()));
//                meteor.setDeltaMovement(direction);
//
//                world.addFreshEntity(meteor);
//            }
//
//            this.playSound(world, caster, ticksInUse, -1, modifiers);
//            return true;
//
//        }else{
//            super.perform(caster);
//        }
        super.perform(caster);
    }

    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if(world.canSeeSky(pos.above())){
            if(!world.isClientSide){
                EntityMeteor meteor = new EntityMeteor(world, pos.getX(), pos.getY() + 50, pos.getZ(),
                        1, EntityUtil.canDamageBlocks(caster, world));
                world.addFreshEntity(meteor);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        return false;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 40F)
                .add(DefaultProperties.DAMAGE, 2F)
                .build();
    }
}
