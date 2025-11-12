package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.core.particles.ParticleTypes;

public class FlamingAxe extends ConjureItemSpell {
    public FlamingAxe() {
        super(EBItems.FLAMING_AXE.get());
    }

    @Override
    protected void spawnParticles(PlayerCastContext ctx) {
        for (int i = 0; i < 10; i++) {
            double x = ctx.caster().getX() + ctx.world().random.nextDouble() * 2 - 1;
            double y = ctx.caster().getY() + ctx.caster().getEyeHeight() - 0.5 + ctx.world().random.nextDouble();
            double z = ctx.caster().getZ() + ctx.world().random.nextDouble() * 2 - 1;
            ctx.world().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
        }
    }
}
