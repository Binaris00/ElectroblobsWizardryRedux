package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.content.entity.construct.IceBarrierConstruct;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class FrostWardingAmuletEffect implements IArtifactEffect {
    @Override
    public void onTick(Player player, Level level, ItemStack artifact) {
        if (level.isClientSide || player.tickCount % 40 != 0) return;

        List<IceBarrierConstruct> barriers = level.getEntitiesOfClass(IceBarrierConstruct.class, player.getBoundingBox().inflate(1.5));

        if (!barriers.isEmpty() && barriers.stream().anyMatch(b -> b.getLookAngle().dot(b.position().subtract(player.position())) > 0)) {
            player.addEffect(new MobEffectInstance(EBMobEffects.WARD.get(), 50, 1));
        }
    }
}
