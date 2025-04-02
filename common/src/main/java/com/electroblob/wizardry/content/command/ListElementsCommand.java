package com.electroblob.wizardry.content.command;

import com.electroblob.wizardry.core.registry.ElementRegistry;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class ListElementsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("elements")
                .requires((p) -> p.hasPermission(2))
                .executes(c -> listSpells(c.getSource()))
        );
    }

    private static int listSpells(CommandSourceStack pSource) {
        ElementRegistry.entrySet().forEach((k) -> {
            pSource.sendSystemMessage(k.getValue().getDisplayName());
        });
        return 1;
    }

    private ListElementsCommand(){}
}
