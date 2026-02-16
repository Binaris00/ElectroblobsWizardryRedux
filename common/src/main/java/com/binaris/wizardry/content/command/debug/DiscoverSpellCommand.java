package com.binaris.wizardry.content.command.debug;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.binaris.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class DiscoverSpellCommand {

    private DiscoverSpellCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("discover")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("spell", SpellArgument.spell()))
                        .executes((c) -> execute(c, EntityArgument.getPlayer(c, "player"), SpellArgument.getSpell(c, "spell")))
                        .then(Commands.argument("all", StringArgumentType.string())
                                .executes((c) -> execute(c, EntityArgument.getPlayer(c, "player")))
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, ServerPlayer player, Spell spell) {
        CommandSourceStack source = context.getSource();
        var data = Services.OBJECT_DATA.getSpellManagerData(player);

        if (data.hasSpellBeenDiscovered(spell)) {
            source.sendFailure(Component.translatable("command.ebwizardry.discover.already_discovered", player.getName(), spell.getDescriptionFormatted()));
            return 0;
        }

        data.discoverSpell(spell);
        source.sendSystemMessage(Component.translatable("command.ebwizardry.discover.success", player.getName(), spell.getDescriptionFormatted()));
        return 1;
    }

    private static int execute(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        CommandSourceStack source = context.getSource();
        var data = Services.OBJECT_DATA.getSpellManagerData(player);

        int count = 0;
        for (Spell spell : Services.REGISTRY_UTIL.getSpells()) {
            if (!data.hasSpellBeenDiscovered(spell)) {
                data.discoverSpell(spell);
                count++;
            }
        }

        source.sendSystemMessage(Component.translatable("command.ebwizardry.discover.discovered_all", player.getName(), count));
        return count;
    }
}
