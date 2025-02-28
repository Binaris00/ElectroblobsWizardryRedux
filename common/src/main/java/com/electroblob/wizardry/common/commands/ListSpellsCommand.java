package com.electroblob.wizardry.common.commands;

import com.electroblob.wizardry.api.common.spell.SpellRegistry;
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
        SpellRegistry.entrySet().forEach((k) -> {
            pSource.sendSystemMessage(Component.translatable(k.getKey().location().toString()));
        });
        return 1;
    }

    private ListSpellsCommand(){}
}
