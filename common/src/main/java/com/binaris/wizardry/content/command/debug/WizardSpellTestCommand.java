package com.binaris.wizardry.content.command.debug;


import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.binaris.wizardry.content.entity.living.AbstractWizard;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobSpawnType;

import java.util.List;

public class WizardSpellTestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wizard_spell_test")
                .then(Commands.argument("spell", SpellArgument.spell())
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("evil", BoolArgumentType.bool())
                                        .executes((c) -> execute(c, SpellArgument.getSpell(c, "spell"), BlockPosArgument.getLoadedBlockPos(c, "pos"), BoolArgumentType.getBool(c, "evil")))
                                )
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, Spell spell, BlockPos pos, boolean isEvil) {
        AbstractWizard wizard;
        if (isEvil) {
            wizard = EBEntities.EVIL_WIZARD.get().create(context.getSource().getLevel());
        } else {
            wizard = EBEntities.WIZARD.get().create(context.getSource().getLevel());
        }

        if (wizard == null) {
            context.getSource().sendFailure(Component.literal("Failed to create wizard entity."));
            return 0;
        }

        wizard.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        wizard.finalizeSpawn(context.getSource().getLevel(), context.getSource().getLevel().getCurrentDifficultyAt(pos), MobSpawnType.COMMAND, null, null);
        wizard.setSpells(List.of(spell));
        context.getSource().getLevel().addFreshEntity(wizard);

        context.getSource().sendSuccess(() -> Component.literal("Spawned wizard with spell: " + spell.getDescriptionFormatted()), true);
        return 1;
    }

    private WizardSpellTestCommand() {
    }
}
