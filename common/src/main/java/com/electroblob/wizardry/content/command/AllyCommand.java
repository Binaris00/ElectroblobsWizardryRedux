package com.electroblob.wizardry.content.command;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.WizardData;
import com.electroblob.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class AllyCommand {
    private AllyCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ally")
                .then(Commands.literal("add")
                        // Remote version where you need to specify an origin and an ally
                        .then(Commands.argument("origin", EntityArgument.players())
                                .then(Commands.argument("ally", EntityArgument.players())
                                        .requires(src -> src.hasPermission(2))
                                        .executes(ctx -> processAllyChange(ctx.getSource(), EntityArgument.getPlayer(ctx, "origin"),
                                                EntityArgument.getPlayer(ctx, "ally"), true, "add"))
                                )
                                .executes(ctx -> executeAddAlly(ctx, EntityArgument.getPlayer(ctx, "origin")))
                        )
                )

                .then(Commands.literal("remove")
                        // Remote version where you need to specify an origin and an ally
                        .then(Commands.argument("origin", EntityArgument.players())
                                .then(Commands.argument("ally", EntityArgument.players())
                                        .requires(src -> src.hasPermission(2))
                                        .executes(ctx -> processAllyChange(ctx.getSource(), EntityArgument.getPlayer(ctx, "origin"),
                                                EntityArgument.getPlayer(ctx, "ally"), false, "remove"))
                                )
                                .executes(ctx -> executeRemoveAlly(ctx, EntityArgument.getPlayer(ctx, "origin")))
                        )
                )
        );
    }

    private static int processAllyChange(CommandSourceStack source, Player origin, Player ally, boolean addOperation, String opKey) {
        WizardData data = Services.OBJECT_DATA.getWizardData(origin);
        boolean result = data.toggleAlly(ally);
        // If the result is the same as the operation, the change was successful
        boolean success = (addOperation == result);
        String key = "commands." + WizardryMainMod.MOD_ID + ":ally." + opKey + (success ? ".success" : ".failure");
        source.sendSystemMessage(Component.translatable(key, origin.getDisplayName(), ally.getDisplayName()));
        return success ? 1 : 0;
    }

    private static int executeAddAlly(CommandContext<CommandSourceStack> context, Player ally) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        // You need to be a player to execute this!!
        if (!source.isPlayer()) {
            source.sendFailure(Component.translatable("commands." + WizardryMainMod.MOD_ID + ":ally.add.failure", ally.getDisplayName()));
            return 0;
        }
        Player origin = source.getPlayerOrException();
        return processAllyChange(source, origin, ally, true, "add");
    }


    private static int executeRemoveAlly(CommandContext<CommandSourceStack> context, Player ally) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        // You need to be a player to execute this!!
        if (!source.isPlayer()) {
            source.sendFailure(Component.translatable("commands." + WizardryMainMod.MOD_ID + ":ally.remove.failure", ally.getDisplayName()));
            return 0;
        }
        Player origin = source.getPlayerOrException();
        return processAllyChange(source, origin, ally, false, "remove");
    }
}
