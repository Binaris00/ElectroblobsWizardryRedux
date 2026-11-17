package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.item.SpectralSwordItem;
import com.binaris.wizardry.content.spell.abstr.ConjureItemSpell;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.world.item.ItemStack;

public class ConjureSword extends ConjureItemSpell {
    private static final double BASE_DAMAGE = 5.0D;

    public ConjureSword() {
        super(EBItems.SPECTRAL_SWORD.get());
    }

    @Override
    protected ItemStack addItemExtras(PlayerCastContext ctx, ItemStack stack) {
        float potency = ctx.modifiers().getFactor(SpellModifiers.POTENCY);
        double bonusDamage = BASE_DAMAGE * (potency - 1.0F);

        // Only write NBT attributes when there is an actual bonus. For a normal cast this leaves the item's
        // default damage/speed untouched; when there is a bonus, SpectralSwordItem writes the full set.
        if (bonusDamage > 0.0D && stack.getItem() instanceof SpectralSwordItem sword) {
            sword.applyConjuredAttributes(stack, bonusDamage);
        }

        return super.addItemExtras(ctx, stack);
    }
}