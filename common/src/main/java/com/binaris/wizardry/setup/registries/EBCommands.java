package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.content.command.*;
import com.binaris.wizardry.content.command.debug.*;
import com.binaris.wizardry.core.mixin.CommandsMixin;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * Check {@link CommandsMixin CommandsMixin} to see the registry
 * <br>
 * We only need to use {@link EBCommands#COMMANDS_TO_REGISTER} :p
 *
 */
public final class EBCommands {
    // TODO CastCommand::register
    public static final List<Consumer<CommandDispatcher<CommandSourceStack>>> COMMANDS_TO_REGISTER = ImmutableList.of(
            ListSpellsCommand::register,
            ListElementsCommand::register,
            ListTiersCommand::register,
            AllyCommand::register,
            DiscoverSpellCommand::register
    );
    public static final List<Consumer<CommandDispatcher<CommandSourceStack>>> DEBUG_COMMANDS = ImmutableList.of(
            WandSpellCommand::register,
            WandSpellListCommand::register,
            WandSelectCommand::register,
            PacketTestCommand::register,
            CastCommand::register,
            ForfeitTestCommand::register,
            SpellContextCommand::register,
            UnDiscoverSpellCommand::register,
            WizardSpellTestCommand::register
    );

    private EBCommands() {
    }
}
