package com.electroblob.wizardry.content.command.debug;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.content.item.WandItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class WandSelectCommand {
    private static final SuggestionProvider<CommandSourceStack> MODE_SUGGESTIONS = (context, builder) ->
            SharedSuggestionProvider.suggest(Arrays.asList("next", "previous"), builder);

    private WandSelectCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("wandselect")
                .then(Commands.literal("mode")
                    .then(Commands.argument("option", StringArgumentType.word())
                        .suggests(MODE_SUGGESTIONS)
                        .executes(ctx -> executeMode(ctx, StringArgumentType.getString(ctx, "option")))
                    )
                )
                .then(Commands.literal("index")
                    .then(Commands.argument("slot", IntegerArgumentType.integer(0))
                        .executes(ctx -> executeIndex(ctx, IntegerArgumentType.getInteger(ctx, "slot")))
                    )
                )
        );
    }


    private static int executeMode(CommandContext<CommandSourceStack> context, String option) {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("You need to be a player to execute this!"));
            return 0;
        }
        ServerPlayer player = source.getPlayer();
        ItemStack wandStack = player.getMainHandItem();
        if (!(wandStack.getItem() instanceof WandItem)) {
            source.sendFailure(Component.literal("You must be holding a wand!"));
            return 0;
        }

        Spell newSelected;
        if (option.equalsIgnoreCase("previous")) {
            newSelected = WandHelper.getPreviousSpell(wandStack);
        } else if(option.equalsIgnoreCase("next")) {
            newSelected = WandHelper.getNextSpell(wandStack);
        } else {
            newSelected = WandHelper.getCurrentSpell(wandStack);
        }

        WandHelper.setCurrentSpell(wandStack, newSelected);
        source.sendSystemMessage(Component.literal("Spell selected: " + newSelected.getLocation()));
        return 1;
    }

    private static int executeIndex(CommandContext<CommandSourceStack> context, int slot) {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("You need to be a player to execute this!"));
            return 0;
        }
        ServerPlayer player = source.getPlayer();
        ItemStack wandStack = player.getMainHandItem();
        if (!(wandStack.getItem() instanceof WandItem wandItem)) {
            source.sendFailure(Component.literal("You must be holding a wand!"));
            return 0;
        }
        int maxSlots = wandItem.getSpellSlotCount(wandStack);
        if (slot < 0 || slot >= maxSlots) {
            source.sendFailure(Component.literal("Invalid slot number. Must be between 0 and " + (maxSlots - 1)));
            return 0;
        }

        List<Spell> spells = WandHelper.getSpells(wandStack);
        Spell newSelected = spells.get(slot);
        WandHelper.setCurrentSpell(wandStack, newSelected);
        source.sendSystemMessage(Component.literal("Spell selected: " + newSelected.getLocation() + " at " + slot));
        return 1;
    }
}
