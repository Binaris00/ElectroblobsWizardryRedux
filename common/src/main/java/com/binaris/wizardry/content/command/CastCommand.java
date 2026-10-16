package com.binaris.wizardry.content.command;

import com.binaris.wizardry.api.content.data.CastCommandData;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.CastItemUtils;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public final class CastCommand {
    public static final int DEFAULT_CASTING_DURATION = 400;
    public static final int MAX_CASTING_DURATION = 72000;

    private CastCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("cast")
                .requires(p -> p.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("spell", SpellArgument.spell())
                                .executes(context -> execute(context,
                                        EntityArgument.getPlayer(context, "player"),
                                        SpellArgument.getSpell(context, "spell"),
                                        DEFAULT_CASTING_DURATION))
                                .then(Commands.argument("duration", IntegerArgumentType.integer(1, MAX_CASTING_DURATION))
                                        .executes(context -> execute(context,
                                                EntityArgument.getPlayer(context, "player"),
                                                SpellArgument.getSpell(context, "spell"),
                                                IntegerArgumentType.getInteger(context, "duration")))
                                )
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, ServerPlayer player, Spell spell, int duration) {
        CommandSourceStack source = context.getSource();
        CastCommandData data = Services.OBJECT_DATA.getCastCommandData(player);

        if (!spell.isInstantCast() && data.isCommandCasting()) {
            data.stopCastingContinuousSpell();
            return 1;
        }

        PlayerCastContext ctx = new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, new SpellModifiers());

        if (WizardryEventBus.fireEvent(new SpellCastEvent.Pre(SpellCastEvent.Sources.COMMAND, spell, ctx))) {
            source.sendFailure(Component.translatable("commands.ebwizardry.cast.failure", spell.getDescriptionFormatted()));
            return 0;
        }

        if (!spell.isInstantCast()) {
            data.startCastingContinuousSpell(spell, ctx.modifiers(), duration);
            source.sendSystemMessage(Component.translatable("commands.ebwizardry.cast.success_continuous", spell.getDescriptionFormatted()));
            return 1;
        }

        return handleInstantSpell(source, spell, ctx);
    }

    private static int handleInstantSpell(CommandSourceStack source, Spell spell, PlayerCastContext ctx) {
        if (!spell.cast(ctx)) {
            source.sendFailure(Component.translatable("commands.ebwizardry.cast.failure", spell.getDescriptionFormatted()));
            return 0;
        }

        WizardryEventBus.fireEvent(new SpellCastEvent.Post(SpellCastEvent.Sources.COMMAND, spell, ctx));
        CastItemUtils.sendSpellCastPacket(ctx.caster(), spell, ctx);
        source.sendSystemMessage(Component.translatable("commands.ebwizardry.cast.success", spell.getDescriptionFormatted()));
        return 1;
    }
}
