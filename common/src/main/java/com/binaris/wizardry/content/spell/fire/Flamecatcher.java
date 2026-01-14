package com.binaris.wizardry.content.spell.fire;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.content.spell.abstr.ConjureItemSpell;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;

public class Flamecatcher extends ConjureItemSpell {
    public static final SpellProperty<Integer> SHOT_COUNT = SpellProperty.intProperty("shot_count", 5);

    public Flamecatcher() {
        super(EBItems.FLAMECATCHER.get());
    }

    @Override
    protected void spawnParticles(PlayerCastContext ctx) {
        ParticleBuilder.create(EBParticles.BUFF).entity(ctx.caster()).color(0xff6d00).spawn(ctx.world());

        for (int i = 0; i < 10; i++) {
            double x = ctx.caster().getX() + ctx.world().random.nextDouble() * 2 - 1;
            double y = ctx.caster().getY() + ctx.caster().getEyeHeight() - 0.5 + ctx.world().random.nextDouble();
            double z = ctx.caster().getZ() + ctx.world().random.nextDouble() * 2 - 1;
            ctx.world().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
        }
    }
}
