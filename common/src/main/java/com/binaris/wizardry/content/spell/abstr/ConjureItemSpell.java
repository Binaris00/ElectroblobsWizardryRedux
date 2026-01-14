package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.data.ConjureData;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ConjureItemSpell extends Spell {
    public static Set<Item> SUPPORTED_ITEMS = new HashSet<>();
    private final Item item;

    public ConjureItemSpell(Item item) {
        this.item = item;
        registerSupportedItem(item);
    }

    /**
     * Checks if the given item stack is currently summoned (i.e. conjured and not expired). The item must also be part of
     * the supported conjure items inside the mod. For a check of whether an item is part of the supported conjure items
     * (not checking if it is summoned), use {@link #isSummonableItem(ItemStack)}.
     */
    public static boolean isSummoned(ItemStack stack) {
        if (!isSummonableItem(stack)) return false; // It should be part of the supported items
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        return data != null && data.isSummoned();
    }

    /**
     * Checks if the given item is part of the supported conjure items inside the mod. For a better check of whether an item
     * is actually summoned, use {@link #isSummoned(ItemStack)}.
     */
    public static boolean isSummonableItem(Item item) {
        return SUPPORTED_ITEMS.contains(item);
    }

    /**
     * Checks if the given item stack is part of the supported conjure items inside the mod. For a better check of whether an
     * item is actually summoned, use {@link #isSummoned(ItemStack)}.
     */
    public static boolean isSummonableItem(ItemStack stack) {
        return isSummonableItem(stack.getItem());
    }

    public static void registerSupportedItem(Item item) {
        SUPPORTED_ITEMS.add(item);
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

    protected void spawnParticles(PlayerCastContext ctx) {
        for (int i = 0; i < 10; i++) {
            double x = ctx.caster().xo + ctx.world().random.nextDouble() * 2 - 1;
            double y = ctx.caster().yo + ctx.caster().getEyeHeight() - 0.5 + ctx.world().random.nextDouble();
            double z = ctx.caster().zo + ctx.world().random.nextDouble() * 2 - 1;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.1, 0)
                    .color(0.7f, 0.9f, 1).spawn(ctx.world());
        }
    }

    protected boolean conjureItem(PlayerCastContext ctx) {
        ItemStack stack = new ItemStack(item);
        stack = addItemExtras(ctx, stack);

        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        int duration = property(DefaultProperties.ITEM_LIFETIME);
        long currentGameTime = ctx.world().getGameTime();

        data.setExpireTime(currentGameTime + duration);
        data.setDuration(duration);
        data.setSummoned(true);

        if (!ctx.caster().addItem(stack)) {
            ctx.caster().sendSystemMessage(Component.translatable("spell.ebwizardry:conjure_item.no_space"));
            return false;
        }
        return true;
    }


    protected ItemStack addItemExtras(PlayerCastContext ctx, ItemStack stack) {
        return stack;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
