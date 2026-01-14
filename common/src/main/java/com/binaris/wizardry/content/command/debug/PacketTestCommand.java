package com.binaris.wizardry.content.command.debug;

import com.binaris.wizardry.core.networking.s2c.TestParticlePacketS2C;
import com.binaris.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

public class PacketTestCommand {
    private PacketTestCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("packet_test")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(context -> execute(context, BlockPosArgument.getBlockPos(context, "pos")))
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, BlockPos pos) {
        context.getSource().getLevel().players().forEach((player -> {
            Services.NETWORK_HELPER.sendTo(player, new TestParticlePacketS2C(pos, 1));
        }));
        return 1;
    }
}
