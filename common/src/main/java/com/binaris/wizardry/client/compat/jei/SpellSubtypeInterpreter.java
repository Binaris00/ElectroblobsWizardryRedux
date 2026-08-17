package com.binaris.wizardry.client.compat.jei;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @see <a href="https://github.com/mezz/JustEnoughItems/blob/1.20.1/Library/src/main/java/mezz/jei/library/plugins/vanilla/ingredients/subtypes/PotionSubtypeInterpreter.java">PotionSubtypeInterpreter</a>
 */
public class SpellSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack>  {
    public static final SpellSubtypeInterpreter INSTANCE = new SpellSubtypeInterpreter();

    private SpellSubtypeInterpreter() {}

    @Override
    public @NotNull String apply(@NotNull ItemStack itemStack, @NotNull UidContext context) {
        if (!itemStack.hasTag()) return IIngredientSubtypeInterpreter.NONE;
        String spellType = RegistryUtils.getSpell(itemStack).getDescriptionId();
        StringBuilder builder = new StringBuilder(spellType);
        for (Spell spell : RegistryUtils.getSpells(Spell::isInstantCast)) {
            builder.append(";").append(spell);
        }
        return builder.toString();
    }
}
