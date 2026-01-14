package com.binaris.wizardry.content.command;

import com.binaris.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class ListElementsCommand {

    private ListElementsCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("elements")
                .requires((p) -> p.hasPermission(2))
                .executes(c -> listSpells(c.getSource()))
        );
    }

    private static int listSpells(CommandSourceStack pSource) {
        Services.REGISTRY_UTIL.getElements().forEach((element) -> {
            pSource.sendSystemMessage(Component.literal("Element: " + element.getLocation()));
        });
        return 1;
    }
}
