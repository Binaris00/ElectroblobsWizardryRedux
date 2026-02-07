package com.binaris.wizardry.core;

import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * Utility class used for quickly add artifacts effect to simple items without having to create a whole new class. This
 * is normally used when you just want to:
 * <ul>
 *     <li>Check if the user has the artifact equipped and do a quick change</li>
 *     <li>Check world status and do something in return with the player</li>
 *     <li>Check the event and do something in return with the player</li>
 *     <li>Basically any simple-condition with a simple-implementation</li>
 * </ul>
 * <br><br>
 * It provides static methods that take a {@link BiPredicate} and a {@link BiConsumer} for each of the events in
 * {@link IArtifactEffect}, and returns an instance of {@link IArtifactEffect} with the corresponding method overridden
 * to run the consumer if the predicate returns true.
 */
public class QuickArtifactEffect implements IArtifactEffect {
    private QuickArtifactEffect() {
    }


    public static IArtifactEffect spellPreCast(BiPredicate<SpellCastEvent.Pre, ItemStack> predicate, BiConsumer<SpellCastEvent.Pre, ItemStack> consumer) {
        return new QuickArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack stack) {
                if (predicate.test(event, stack)) consumer.accept(event, stack);
            }
        };
    }

    public static IArtifactEffect hurtEntity(BiPredicate<EBLivingHurtEvent, ItemStack> predicate, BiConsumer<EBLivingHurtEvent, ItemStack> consumer) {
        return new QuickArtifactEffect() {
            @Override
            public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
                if (predicate.test(event, stack)) consumer.accept(event, stack);
            }
        };
    }

    public static IArtifactEffect death(BiPredicate<EBLivingDeathEvent, ItemStack> predicate, BiConsumer<EBLivingDeathEvent, ItemStack> consumer) {
        return new QuickArtifactEffect() {
            @Override
            public void onPlayerKill(EBLivingDeathEvent event, ItemStack stack) {
                if (predicate.test(event, stack)) consumer.accept(event, stack);
            }
        };
    }
}
