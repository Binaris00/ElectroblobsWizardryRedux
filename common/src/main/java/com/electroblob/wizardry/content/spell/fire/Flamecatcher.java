package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.content.item.FlameCatcherItem;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;


public class Flamecatcher extends ConjureItemSpell {
    public static final SpellProperty<Integer> SHOT_COUNT = SpellProperty.intProperty("shot_count", 5);

    public Flamecatcher() {
        super(EBItems.FLAMECATCHER.get());
    }

    @Override
    protected ItemStack addItemExtras(PlayerCastContext ctx, ItemStack stack) {
        stack.getOrCreateTag().putInt(FlameCatcherItem.SHOTS_REMAINING_NBT_KEY, (int) (property(SHOT_COUNT) * ctx.modifiers().get(SpellModifiers.POTENCY)));
        return stack;
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
