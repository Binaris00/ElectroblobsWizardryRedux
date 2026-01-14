package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.content.entity.projectile.IceShardEntity;
import com.binaris.wizardry.core.IArtefactEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArcaneFrostRingEffect implements IArtefactEffect {
    @Override
    public void onDeath(EBLivingDeathEvent event, ItemStack stack) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getSource().is(EBDamageSources.FROST))) return;

        for (int i = 0; i < 8; i++) {
            double dx = player.level().random.nextDouble() - 0.5;
            double dy = player.level().random.nextDouble() - 0.5;
            double dz = player.level().random.nextDouble() - 0.5;
            IceShardEntity iceshard = new IceShardEntity(player.level());
            iceshard.setPos(player.xo + dx + Math.signum(dx) * player.getBbWidth(),
                    player.yo + player.getBbHeight() / 2 + dy,
                    player.zo + dz + Math.signum(dz) * player.getBbWidth());
            iceshard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
            iceshard.setOwner(player);
            player.level().addFreshEntity(iceshard);
        }
    }
}
