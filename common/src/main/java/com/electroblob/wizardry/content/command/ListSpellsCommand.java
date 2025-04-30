package com.electroblob.wizardry.content.command;

import com.electroblob.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class ListSpellsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("listSpells")
                .requires((p) -> p.hasPermission(2))
                .executes(c -> listSpells(c.getSource()))
        );
    }

    private static int listSpells(CommandSourceStack pSource) {
        Services.REGISTRY_UTIL.getSpells().forEach((spell) -> {
            pSource.sendSystemMessage(Component.literal("Spell: " + spell));
        });
        return 1;
    }

    private ListSpellsCommand(){}
}
