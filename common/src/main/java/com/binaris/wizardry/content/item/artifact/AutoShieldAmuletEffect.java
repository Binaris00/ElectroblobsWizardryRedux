package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AutoShieldAmuletEffect implements IArtifactEffect {
    @Override
    public void onTick(LivingEntity entity, Level level, ItemStack stack) {
        // todo shield spell
//        findMatchingWandAndExecute(player, Spells.SH, wand -> {
//            if(wand.getItem() instanceof ItemScroll) return;
//
//            List<Entity> projectiles = EntityUtils.getEntitiesWithinRadius(5, player.posX, player.posY, player.posZ, world, Entity.class);
//            projectiles.removeIf(e -> !(e instanceof IProjectile));
//            Vec3d look = player.getLookVec();
//            Vec3d playerPos = player.getPositionVector().add(0, player.height/2, 0);
//
//            for(Entity projectile : projectiles){
//                Vec3d vec = playerPos.subtract(projectile.getPositionVector()).normalize();
//                double angle = Math.acos(vec.scale(-1).dotProduct(look));
//                if(angle > Math.PI * 0.4f) continue; // (Roughly) the angle the shield will protect
//                Vec3d velocity = new Vec3d(projectile.motionX, projectile.motionY, projectile.motionZ).normalize();
//                double angle1 = Math.acos(vec.dotProduct(velocity));
//                if(angle1 < Math.PI * 0.2f){
//                    SpellModifiers modifiers = new SpellModifiers();
//                    if(((ISpellCastingItem)wand.getItem()).canCast(wand, Spells.shield, player, EnumHand.MAIN_HAND, 0, modifiers)){
//                        ((ISpellCastingItem)wand.getItem()).cast(wand, Spells.shield, player, EnumHand.MAIN_HAND, 0, modifiers);
//                    }
//                    break;
//                }
//            }
//        });
    }
}
