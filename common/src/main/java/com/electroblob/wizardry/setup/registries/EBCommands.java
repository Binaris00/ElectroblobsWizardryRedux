package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.common.commands.ListSpellsCommand;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;
import java.util.function.Consumer;

public final class EBCommands {

    public static final List<Consumer<CommandDispatcher<CommandSourceStack>>> COMMANDS_TO_REGISTER = ImmutableList.of(
            ListSpellsCommand::register
    );

    private EBCommands() {}
}
