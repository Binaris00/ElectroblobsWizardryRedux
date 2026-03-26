package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.data.ConjureData;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for spells that conjure items. This class provides the core functionality for conjuring an item and managing
 * its lifetime through the use of ConjureData. It also includes a static set of supported items that can be conjured, and
 * utility methods for checking if an item is summonable or currently summoned (you normally won't use this directly when
 * creating a spell). This can only be used by player casts.
 * <p>
 * Adding an item to conjure by this spell makes it to always contain a {@code ConjureData}.
 * <p>
 * Check {@link Spells#CONJURE_SWORD} and {@link Spells#FLAMECATCHER} for examples of how to create conjure item spells.
 * <p>
 * You must override the {@link #properties()} to return an actual instance of {@link SpellProperties} for this spell or
 * use {@link Spell#assignProperties(SpellProperties)}, otherwise the spell will have no properties and may not function
 * as intended.
 *
 * @see ConjureData
 */
public class ConjureItemSpell extends Spell {
    public static Set<Item> SUPPORTED_ITEMS = new HashSet<>();
    private final Item item;

    public ConjureItemSpell(Item item) {
        this.item = item;
        registerSupportedItem(item);
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (conjureItem(ctx)) {
            if (ctx.world().isClientSide) spawnParticles(ctx);
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return true;
        }

        return false;
    }

    /**
     * Conjures the item for the caster. This method creates an ItemStack of the specified item, gets the conjure data
     * for it and establishes the duration and expiration time based on the spell properties and modifiers. The conjure
     * data is then associated with the item stack, and the item is added to the caster's inventory. If the caster's
     * inventory is full, a message is sent to the player and the method returns false.
     *
     * @param ctx the context of the spell cast, containing information about the caster, the world, and the spell modifiers
     * @return true if the item was successfully conjured and added to the caster's inventory, false otherwise
     * @see ConjureData
     */
    protected boolean conjureItem(PlayerCastContext ctx) {
        ItemStack stack = new ItemStack(item);
        stack = addItemExtras(ctx, stack);

        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        int duration = property(DefaultProperties.ITEM_LIFETIME);
        float durationMultiplier = ctx.modifiers().get(SpellModifiers.DURATION);
        long currentGameTime = ctx.world().getGameTime();

        data.setExpireTime((long) (currentGameTime + (duration * durationMultiplier)));
        data.setDuration((int) (duration * durationMultiplier));
        data.setSummoned(true);

        if (!ctx.caster().addItem(stack)) {
            ctx.caster().sendSystemMessage(Component.translatable("spell.ebwizardry.conjure_item.no_space"));
            return false;
        }
        return true;
    }


    /**
     * Checks if the given item stack is currently summoned (i.e. conjured and not expired). The item must also be part of
     * the supported conjure items inside the mod. For a check of whether an item is part of the supported conjure items
     * (not checking if it is summoned), use {@link #isSummonableItem(ItemStack)}.
     *
     * @param stack the item stack to check
     * @return true if the item stack is currently summoned, false otherwise
     */
    public static boolean isSummoned(ItemStack stack) {
        if (!isSummonableItem(stack)) return false; // It should be part of the supported items
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        return data != null && data.isSummoned();
    }

    /**
     * Checks if the given item is part of the supported conjure items inside the mod. For a better check of whether an item
     * is actually summoned, use {@link #isSummoned(ItemStack)}.
     *
     * @param item the item to check
     * @return true if the item is part of the supported conjure items, false otherwise
     */
    public static boolean isSummonableItem(Item item) {
        return SUPPORTED_ITEMS.contains(item);
    }

    /**
     * Checks if the given item stack is part of the supported conjure items inside the mod. For a better check of whether an
     * item is actually summoned, use {@link #isSummoned(ItemStack)}.
     *
     * @param stack the item stack to check
     * @return true if the item stack is part of the supported conjure items, false otherwise
     */
    public static boolean isSummonableItem(ItemStack stack) {
        return isSummonableItem(stack.getItem());
    }

    /**
     * Registers an item as a supported conjure item. This should be called in the constructor of any spell that conjures
     * an item or in your mod's common setup. If an item is not registered as a supported conjure item, it will not be
     * recognized as a valid and won't have the conjure data associated with it.
     *
     * @param item the item to register as a supported conjure item
     */
    public static void registerSupportedItem(Item item) {
        SUPPORTED_ITEMS.add(item);
    }

    /**
     * Spawns spark particles around the caster. This is a client-side visual effect that is called when the spell is cast.
     * The particles are spawned in a random pattern around the caster's head and have a light blue color.
     * <p>
     * You could override this to change the particle effect.
     *
     * @param ctx the context of the spell cast, containing information about the caster and the world
     */
    protected void spawnParticles(PlayerCastContext ctx) {
        for (int i = 0; i < 10; i++) {
            double x = ctx.caster().xo + ctx.world().random.nextDouble() * 2 - 1;
            double y = ctx.caster().yo + ctx.caster().getEyeHeight() - 0.5 + ctx.world().random.nextDouble();
            double z = ctx.caster().zo + ctx.world().random.nextDouble() * 2 - 1;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.1, 0)
                    .color(0.7f, 0.9f, 1).spawn(ctx.world());
        }
    }

    /**
     * Adds extra properties or NBT data to the conjured item stack before it is given to the caster. This method is called
     * during the conjuration process and allows you to customize the item stack based on the spell context. By default,
     * this method returns the original item stack without any modifications.
     *
     * @param ctx   the context of the spell cast, containing information about the caster, the world, and the spell modifiers
     * @param stack the original item stack that is about to be conjured
     * @return the modified item stack with any additional properties or NBT data added
     */
    protected ItemStack addItemExtras(PlayerCastContext ctx, ItemStack stack) {
        return stack;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
