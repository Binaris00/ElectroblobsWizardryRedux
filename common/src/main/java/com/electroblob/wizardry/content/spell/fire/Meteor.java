package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.entity.MeteorEntity;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class Meteor extends RaySpell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
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
        return super.cast(ctx);
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if(ctx.world().canSeeSky(blockHit.getBlockPos().above())){
            if(!ctx.world().isClientSide){
                MeteorEntity meteor = new MeteorEntity(ctx.world(), blockHit.getBlockPos().getX(), blockHit.getBlockPos().getY() + 50, blockHit.getBlockPos().getZ(), 1, EntityUtil.canDamageBlocks(ctx.caster(), ctx.world()));
                ctx.world().addFreshEntity(meteor);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
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
