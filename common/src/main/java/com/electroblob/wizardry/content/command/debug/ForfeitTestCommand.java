package com.electroblob.wizardry.content.command.debug;

import com.electroblob.wizardry.content.Forfeit;
import com.electroblob.wizardry.content.ForfeitRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class ForfeitTestCommand {
    private ForfeitTestCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("forfeit")
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(context -> execute(context, StringArgumentType.getString(context, "name")))
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, String s) {
        CommandSourceStack source = context.getSource();
        if (source.getPlayer() == null) {
            source.sendSystemMessage(Component.literal("This command can only be run by a player.").withStyle(ChatFormatting.RED));
            return 0;
        }

        for (Forfeit forfeit : ForfeitRegistry.getForfeits()) {
            if (forfeit.getName().getPath().equalsIgnoreCase(s)) {
                forfeit.apply(source.getLevel(), source.getPlayer());
                source.sendSystemMessage(Component.literal("Applied forfeit: " + s).withStyle(ChatFormatting.GREEN));
                return 1;
            }
        }

        source.sendSystemMessage(Component.literal("Forfeit not found: " + s).withStyle(ChatFormatting.RED));
        return 0;
    }
}
