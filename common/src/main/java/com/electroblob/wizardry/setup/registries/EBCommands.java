package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.content.command.*;
import com.electroblob.wizardry.content.command.debug.WandSelectCommand;
import com.electroblob.wizardry.content.command.debug.WandSpellCommand;
import com.electroblob.wizardry.content.command.debug.WandSpellListCommand;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * Check {@link com.electroblob.wizardry.core.mixin.CommandsMixin CommandsMixin} to see the registry
 * <br>
 * We only need to use {@link EBCommands#COMMANDS_TO_REGISTER} :p
 * */
public final class EBCommands {
    private EBCommands() {}

    public static final List<Consumer<CommandDispatcher<CommandSourceStack>>> COMMANDS_TO_REGISTER = ImmutableList.of(
            ListSpellsCommand::register,
            ListElementsCommand::register,
            ListTiersCommand::register,
            CastCommand::register,
            AllyCommand::register
    );

    public static final List<Consumer<CommandDispatcher<CommandSourceStack>>> DEBUG_COMMANDS = ImmutableList.of(
            WandSpellCommand::register,
            WandSpellListCommand::register,
            WandSelectCommand::register
    );
}
