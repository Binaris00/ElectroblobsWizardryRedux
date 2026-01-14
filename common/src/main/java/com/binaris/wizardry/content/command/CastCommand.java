package com.binaris.wizardry.content.command;

import com.binaris.wizardry.api.content.data.CastCommandData;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public final class CastCommand {
    public static final int DEFAULT_CASTING_DURATION = 400;

    private CastCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cast")
                .requires(p -> p.hasPermission(2))
                .then(Commands.argument("spell", SpellArgument.spell())
                        .executes(context -> executePlayer(context, SpellArgument.getSpell(context, "spell")))
                )
        );
    }

    private static int executePlayer(CommandContext<CommandSourceStack> context, Spell spell) {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.translatable("commands.ebwizardry.cast.player_only"));
            return 0;
        }

        if (spell == null) {
            source.sendFailure(Component.translatable("commands.ebwizardry.cast.unknown_spell"));
            return 0;
        }

        ServerPlayer player = source.getPlayer();
        SpellModifiers modifiers = new SpellModifiers();

        if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.COMMAND, spell, player, modifiers))) {
            source.sendFailure(Component.translatable("commands.ebwizardry.cast.failure" + spell.getDescriptionId()));
            return 0;
        }

        if (!spell.isInstantCast()) {
            return handleContinuousSpell(source, spell, player, modifiers);
        }

        return handleInstantSpell(source, spell, player, modifiers);
    }

    private static int handleContinuousSpell(CommandSourceStack source, Spell spell, ServerPlayer player, SpellModifiers modifiers) {
        CastCommandData data = Services.OBJECT_DATA.getCastCommandData(player);
        if (data.isCommandCasting()) {
            data.stopCastingContinuousSpell();
        } else {
            data.startCastingContinuousSpell(spell, modifiers, DEFAULT_CASTING_DURATION);
            source.sendSystemMessage(Component.translatable("commands.ebwizardry.cast.success_continuous", spell.getDescriptionId()));
        }
        return 1;
    }

    private static int handleInstantSpell(CommandSourceStack source, Spell spell, ServerPlayer player, SpellModifiers modifiers) {
        if (spell.cast(new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, modifiers))) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.COMMAND, spell, player, modifiers));
            source.sendSystemMessage(Component.translatable("commands.ebwizardry.cast.success", spell.getDescriptionFormatted()));
        }
        return 0;
    }
}
