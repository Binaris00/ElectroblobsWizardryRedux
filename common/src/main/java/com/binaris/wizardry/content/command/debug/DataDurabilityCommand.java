package com.binaris.wizardry.content.command.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class DataDurabilityCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("ebw").requires(source -> source.hasPermission(2)).then(Commands.literal("data").then(Commands.literal("durability").then(Commands.argument("player", EntityArgument.player()).then(Commands.argument("durability", IntegerArgumentType.integer(0)).executes(c -> setDurability(c.getSource(), EntityArgument.getPlayer(c, "player"), IntegerArgumentType.getInteger(c, "durability"))))))));
    }

    private static int setDurability(CommandSourceStack source, ServerPlayer player, int durability) {
        ItemStack stack = player.getMainHandItem();

        if (stack.isEmpty()) {
            source.sendFailure(Component.literal(player.getName().getString() + " don't have any item in hand"));
            return 0;
        }
        if (!stack.isDamageableItem()) {
            source.sendFailure(Component.literal("You can't set durability to this item: " + stack.getHoverName().getString()));
            return 0;
        }

        int maxDurability = stack.getMaxDamage();

        if (durability > maxDurability) {
            source.sendFailure(Component.literal("The value " + durability + " is bigger than the max durability (" + maxDurability + ")."));
            return 0;
        }

        int damageValue = maxDurability - durability;
        stack.setDamageValue(damageValue);

        source.sendSuccess(() -> Component.literal("Durability set to %s/%s".formatted(durability, maxDurability)), true);

        return 1;
    }
}
