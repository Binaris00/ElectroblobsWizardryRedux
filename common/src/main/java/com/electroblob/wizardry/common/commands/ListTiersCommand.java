package com.electroblob.wizardry.common.commands;

import com.electroblob.wizardry.common.core.ElementRegistry;
import com.electroblob.wizardry.common.core.TierRegistry;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class ListTiersCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tiers")
                .requires((p) -> p.hasPermission(2))
                .executes(c -> listSpells(c.getSource()))
        );
    }

    private static int listSpells(CommandSourceStack pSource) {
        TierRegistry.entrySet().forEach((k) -> {
            pSource.sendSystemMessage(k.getValue().getNameForTranslation());
        });
        return 1;
    }

    private ListTiersCommand(){}
}
