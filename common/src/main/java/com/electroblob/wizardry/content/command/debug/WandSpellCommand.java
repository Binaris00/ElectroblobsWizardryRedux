package com.electroblob.wizardry.content.command.debug;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class WandSpellCommand {
    private static final SuggestionProvider<CommandSourceStack> SPELL_SUGGESTIONS = (context, builder) -> SharedSuggestionProvider.suggest(
            SpellUtil.getSpellNames(), builder,
            value -> value,
            Component::literal
    );

    private WandSpellCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wand")
                .then(Commands.argument("spell", ResourceLocationArgument.id()).suggests(SPELL_SUGGESTIONS)
                        .then(Commands.argument("slot", IntegerArgumentType.integer(0))
                                .executes((c) -> execute(c, ResourceLocationArgument.getId(c, "spell"), IntegerArgumentType.getInteger(c, "slot"))))));
    }

    private static int execute(CommandContext<CommandSourceStack> context, ResourceLocation location, int slot) {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("You need to be a player to execute this!"));
            return 0;
        }
        ServerPlayer player = source.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof WandItem wandItem)) {
            context.getSource().sendFailure(Component.literal("You must be holding a wand!"));
            return 0;
        }

        Spell spell = Services.REGISTRY_UTIL.getSpell(location);
        if (spell == null) {
            context.getSource().sendFailure(Component.literal("Spell not found: " + location));
            return 0;
        }


        int maxSlots = wandItem.getSpellSlotCount(stack);
        if (slot < 0 || slot >= maxSlots) {
            context.getSource().sendFailure(Component.literal("Invalid slot number. Must be between 0 and " + (maxSlots - 1)));
            return 0;
        }

        java.util.List<Spell> spells = WandHelper.getSpells(stack);

        int currentSelectedIndex = spells.indexOf(WandHelper.getCurrentSpell(stack));
        spells.set(slot, spell);

        if (currentSelectedIndex == slot) {
            WandHelper.setCurrentSpell(stack, spell);
        }

        WandHelper.setSpells(stack, spells);
        context.getSource().sendSystemMessage(Component.literal("Spell " + location + " set in wand slot " + slot + "."));
        return 1;
    }
}
