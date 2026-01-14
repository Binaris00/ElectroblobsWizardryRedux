package com.binaris.wizardry.content.spell.ice;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.content.spell.abstr.ConjureItemSpell;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.client.EBParticles;

public class FrostAxe extends ConjureItemSpell {
    public FrostAxe() {
        super(EBItems.FROST_AXE.get());
    }

    @Override
    protected void spawnParticles(PlayerCastContext ctx) {
        for (int i = 0; i < 10; i++) {
            double x = ctx.caster().getX() + ctx.world().random.nextDouble() * 2 - 1;
            double y = ctx.caster().getY() + ctx.caster().getEyeHeight() - 0.5 + ctx.world().random.nextDouble();
            double z = ctx.caster().getZ() + ctx.world().random.nextDouble() * 2 - 1;
            ParticleBuilder.create(EBParticles.SNOW).pos(x, y, z).spawn(ctx.world());
        }
    }
}
