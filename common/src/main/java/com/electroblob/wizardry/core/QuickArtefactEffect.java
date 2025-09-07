package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.content.event.EBLivingDeathEvent;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * Utility class used for quickly add artefacts effect to simple items without having to create a whole new class. This
 * is normally used when you just want to:
 * <ul>
 *     <li>Check if the user has the artefact equipped and do a quick change</li>
 *     <li>Check world status and do something in return with the player</li>
 *     <li>Check the event and do something in return with the player</li>
 *     <li>Basically any simple-condition with a simple-implementation</li>
 * </ul>
 * <br><br>
 * It provides static methods that take a {@link BiPredicate} and a {@link BiConsumer} for each of the events in
 * {@link IArtefactEffect}, and returns an instance of {@link IArtefactEffect} with the corresponding method overridden
 * to run the consumer if the predicate returns true.
 */
public class QuickArtefactEffect implements IArtefactEffect {
    private QuickArtefactEffect() {
    }


    public static IArtefactEffect spellPreCast(BiPredicate<SpellCastEvent.Pre, ItemStack> predicate, BiConsumer<SpellCastEvent.Pre, ItemStack> consumer) {
        return new QuickArtefactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack stack) {
                if (predicate.test(event, stack)) consumer.accept(event, stack);
            }
        };
    }

    public static IArtefactEffect hurtEntity(BiPredicate<EBLivingHurtEvent, ItemStack> predicate, BiConsumer<EBLivingHurtEvent, ItemStack> consumer) {
        return new QuickArtefactEffect() {
            @Override
            public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
                if (predicate.test(event, stack)) consumer.accept(event, stack);
            }
        };
    }

    public static IArtefactEffect death(BiPredicate<EBLivingDeathEvent, ItemStack> predicate, BiConsumer<EBLivingDeathEvent, ItemStack> consumer) {
        return new QuickArtefactEffect() {
            @Override
            public void onDeath(EBLivingDeathEvent event, ItemStack stack) {
                if (predicate.test(event, stack)) consumer.accept(event, stack);
            }
        };
    }
}
