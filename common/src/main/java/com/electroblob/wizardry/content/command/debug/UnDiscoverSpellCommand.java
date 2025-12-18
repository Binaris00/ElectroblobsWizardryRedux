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

public final class UnDiscoverSpellCommand {

    private UnDiscoverSpellCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("undiscover")
                .executes(UnDiscoverSpellCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!source.isPlayer()) {
            return 0;
        }

        ServerPlayer player = source.getPlayer();

        var data = Services.OBJECT_DATA.getSpellManagerData(player);
        for (Spell spell : Services.REGISTRY_UTIL.getSpells()) {
            if (data.hasSpellBeenDiscovered(spell)) {
                data.undiscoverSpell(spell);
                source.sendSystemMessage(Component.literal("Undiscovered spell: " + spell.getDescriptionFormatted().getString()));
            }
        }

        return 1;
    }
}
