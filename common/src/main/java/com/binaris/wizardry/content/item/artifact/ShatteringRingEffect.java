package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.content.entity.projectile.IceShardEntity;
import com.binaris.wizardry.core.IArtefactEffect;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ShatteringRingEffect implements IArtefactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (player.level().random.nextFloat() < 0.15f && event.getDamagedEntity().getHealth() < 12f
                && event.getDamagedEntity().hasEffect(EBMobEffects.FROST.get()) && !event.getSource().isIndirect()) {

            event.setAmount(12f);

            for (int i = 0; i < 8; i++) {
                double dx = event.getDamagedEntity().level().random.nextDouble() - 0.5;
                double dy = event.getDamagedEntity().level().random.nextDouble() - 0.5;
                double dz = event.getDamagedEntity().level().random.nextDouble() - 0.5;
                IceShardEntity iceshard = new IceShardEntity(event.getDamagedEntity().level());
                iceshard.setPos(event.getDamagedEntity().xo + dx + Math.signum(dx) * event.getDamagedEntity().getBbWidth(), event.getDamagedEntity().yo + event.getDamagedEntity().getBbHeight() / 2 + dy, event.getDamagedEntity().zo + dz + Math.signum(dz) * event.getDamagedEntity().getBbWidth());
                iceshard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
                iceshard.setOwner(player);
                event.getDamagedEntity().level().addFreshEntity(iceshard);
            }
        }
    }
}
