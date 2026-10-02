package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.content.item.SpectralPickaxeItem;
import com.binaris.wizardry.content.spell.abstr.ConjureItemSpell;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public class ConjurePickaxe extends ConjureItemSpell {
    private static final int MASTER_LEVEL = 3;

    public ConjurePickaxe() {
        super(EBItems.SPECTRAL_PICKAXE.get());
    }

    @Override
    protected ItemStack addItemExtras(PlayerCastContext ctx, ItemStack stack) {
        int level = Math.min(MASTER_LEVEL, getPotencyLevel(ctx));
        stack.getOrCreateTag().putInt(SpectralPickaxeItem.TIER_LEVEL_TAG, level);

        if (level >= 1) {
            stack.enchant(Enchantments.BLOCK_EFFICIENCY, level * 2 - 1);
        }
        return super.addItemExtras(ctx, stack);
    }
}