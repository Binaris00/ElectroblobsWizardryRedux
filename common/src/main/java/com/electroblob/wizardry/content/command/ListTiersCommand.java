package com.electroblob.wizardry.content.command;

import com.electroblob.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class ListTiersCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tiers")
                .requires((p) -> p.hasPermission(2))
                .executes(c -> listTiers(c.getSource()))
        );
    }

    private static int listTiers(CommandSourceStack pSource) {
        Services.REGISTRY_UTIL.getTiers().forEach((tier) -> {
            pSource.sendSystemMessage(Component.literal("Tier: " + tier.getDescriptionFormatted()));
        });
        return 1;
    }

    private ListTiersCommand(){}
}
