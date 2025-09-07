package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.content.entity.construct.IceBarrierConstruct;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class FrostWardingAmuletEffect implements IArtefactEffect {
    @Override
    public void onTick(LivingEntity entity, Level level, ItemStack stack) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide || player.tickCount % 40 != 0) return;

        List<IceBarrierConstruct> barriers = level.getEntitiesOfClass(IceBarrierConstruct.class, player.getBoundingBox().inflate(1.5));

        if (!barriers.isEmpty() && barriers.stream().anyMatch(b -> b.getLookAngle().dot(b.position().subtract(player.position())) > 0)) {
            player.addEffect(new MobEffectInstance(EBMobEffects.WARD.get(), 50, 1));
        }
    }
}
