package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.core.IArtefactEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GlideAmuletEffect implements IArtefactEffect {
    @Override
    public void onTick(LivingEntity entity, Level level, ItemStack stack) {
        // TODO SPELL GLIDE
//        if(player.fallDistance > 3f && player.fallDistance < 3.5f && player.world.rand.nextFloat() < 0.5f){
//            if(!WizardData.get(player).isCasting()) WizardData.get(player).startCastingContinuousSpell(Spells.GLIDE, new SpellModifiers(), 600);
//        }
//        else if(player.onGround){
//            WizardData data = WizardData.get(player);
//            if(data.currentlyCasting() == Spells.glide) data.stopCastingContinuousSpell();
//        }
    }
}
