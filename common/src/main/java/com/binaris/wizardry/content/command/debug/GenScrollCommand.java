package com.binaris.wizardry.content.command.debug;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.binaris.wizardry.setup.registries.EBItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class GenScrollCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("ebw")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("generator")
                        .then(Commands.literal("scroll")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("spell", SpellArgument.spell())
                                                .executes(c -> execute(c, EntityArgument.getPlayer(c, "player"), SpellArgument.getSpell(c, "spell")))
                                        )
                                )
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> c, ServerPlayer player, Spell spell) {
        player.getInventory().add(RegistryUtils.setSpell(new ItemStack(EBItems.SCROLL.get()), spell));
        return 1;
    }

    private GenScrollCommand() {
    }
}
