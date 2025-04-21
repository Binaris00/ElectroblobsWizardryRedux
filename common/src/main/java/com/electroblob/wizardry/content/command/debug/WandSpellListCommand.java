package com.electroblob.wizardry.content.command.debug;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.core.registry.SpellRegistry;
import com.electroblob.wizardry.setup.registries.Spells;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class WandSpellListCommand {
    private WandSpellListCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wand_spells")
                .executes(WandSpellListCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if(!source.isPlayer()) {
            source.sendFailure(Component.literal("You need to be a player to execute this!"));
            return 0;
        }
        ServerPlayer player = source.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof WandItem)) {
            context.getSource().sendFailure(Component.literal("You must be holding a wand!"));
            return 0;
        }
        java.util.List<Spell> spells = WandHelper.getSpells(stack);

        for(Spell spell : spells) {
            if(WandHelper.getCurrentSpell(stack) == spell) {
                player.sendSystemMessage(Component.literal("Spell at %s is currently active".formatted(spells.indexOf(spell))).withStyle(ChatFormatting.YELLOW));
                continue;
            }
            player.sendSystemMessage(Component.literal("Spell at %s is %s".formatted(spells.indexOf(spell), spell.getLocation())));
        }
        return 1;
    }
}
