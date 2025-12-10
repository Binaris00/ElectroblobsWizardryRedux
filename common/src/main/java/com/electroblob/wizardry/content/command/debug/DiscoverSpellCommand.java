package com.electroblob.wizardry.content.command.debug;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.content.command.argument.SpellArgument;
import com.electroblob.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class DiscoverSpellCommand {

    private DiscoverSpellCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("discover")
                .then(Commands.argument("spell", SpellArgument.spell())
                        .executes((c) -> execute(c, SpellArgument.getSpell(c, "spell")))));
    }

    private static int execute(CommandContext<CommandSourceStack> context, Spell spell) {
        CommandSourceStack source = context.getSource();

        if (!source.isPlayer()) {
            source.sendFailure(Component.translatable("command.ebwizardry.discover.not_player"));
            return 0;
        }

        ServerPlayer player = source.getPlayer();

        if (spell == null) {
            source.sendFailure(Component.translatable("command.ebwizardry.discover.spell_not_found"));
            return 0;
        }

        var data = Services.OBJECT_DATA.getSpellManagerData(player);

        if (data.hasSpellBeenDiscovered(spell)) {
            source.sendFailure(Component.translatable("command.ebwizardry.discover.already_discovered", spell.getDescriptionFormatted()));
            return 0;
        }

        data.discoverSpell(spell);
        source.sendSystemMessage(Component.translatable("command.ebwizardry.discover.success", spell.getDescriptionFormatted()));
        return 1;
    }
}
