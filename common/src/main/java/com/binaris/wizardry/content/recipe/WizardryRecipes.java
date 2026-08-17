package com.binaris.wizardry.content.recipe;

import com.binaris.wizardry.core.EBLogger;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class responsible for defining and registering wizardry's non-JSON recipes (i.e. smelting recipes and dynamic
 * crafting recipes). Also handles dynamic recipe display and usage.
 *
 */
public final class WizardryRecipes {
    private WizardryRecipes() {} // No instances!

    private static final List<Item> chargeableItems = new ArrayList<>();

    private static boolean registered;

    /** Adds the given item to the list of items that can be charged using mana flasks. Dynamic charging recipes
     * will be added for these items during {@code RegistryEvent.Register<IRecipe>}. The item must implement
     * {@link com.binaris.wizardry.api.content.item.IManaItem} for the recipes to work correctly. This method should be called from the item's
     * constructor. */
    public static void addToManaFlaskCharging(Item item){

        if(registered){
            EBLogger.warn("Tried to add an item to mana flask charging after it was registered, this will do nothing!");
            return;
        }

        chargeableItems.add(item);
    }

    /** Returns an unmodifiable view of all registered items that can be charged with mana flasks. */
    public static List<Item> getChargeableItems(){
        return Collections.unmodifiableList(chargeableItems);
    }
}
