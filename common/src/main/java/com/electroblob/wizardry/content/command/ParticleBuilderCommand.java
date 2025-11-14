package com.electroblob.wizardry.content.command;

import com.electroblob.wizardry.setup.registries.client.EBParticles;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class ParticleBuilderCommand {
    private static final SuggestionProvider<CommandSourceStack> PARTICLE_SUGGESTIONS = (context, builder) -> SharedSuggestionProvider.suggest(
            EBParticles.getParticleNames(), builder,
            value -> value,
            Component::literal
    );

    private ParticleBuilderCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("particlebuilder")
                .requires((p) -> p.hasPermission(2))
                .then(Commands.argument("particle", ResourceLocationArgument.id()).suggests(PARTICLE_SUGGESTIONS)
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(c -> execute(c.getSource(),
                                        ResourceLocationArgument.getId(c, "particle"),
                                        BlockPosArgument.getBlockPos(c, "pos")))
                        )
                )
        );
    }

    private static int execute(CommandSourceStack source, ResourceLocation particle, BlockPos pos) {
        return 0;
    }

}
