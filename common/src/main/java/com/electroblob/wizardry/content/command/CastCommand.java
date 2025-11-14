package com.electroblob.wizardry.content.command;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.CastCommandData;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public final class CastCommand {
    public static final int DEFAULT_CASTING_DURATION = 400;

    private CastCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cast")
                .requires(p -> p.hasPermission(2))
                .then(Commands.argument("spell", ResourceLocationArgument.id())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                SpellUtil.getSpellNames(), builder, value -> value, Component::literal))
                        .executes(context -> executePlayer(context, ResourceLocationArgument.getId(context, "spell")))
                )
        );
    }

    private static int executePlayer(CommandContext<CommandSourceStack> context, ResourceLocation location) {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("You need to be a player to execute this!!"));
            return 0;
        }

        Spell spell = Services.REGISTRY_UTIL.getSpell(location);
        if (spell == null) {
            source.sendFailure(Component.literal("Spell not found!"));
            return 0;
        }

        ServerPlayer player = source.getPlayer();
        SpellModifiers modifiers = new SpellModifiers();

        if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.COMMAND, spell, player, modifiers))) {
            source.sendFailure(Component.translatable("commands." + WizardryMainMod.MOD_ID + ":cast.failure" + spell.getDescriptionId()));
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
            source.sendSystemMessage(Component.translatable("commands." + WizardryMainMod.MOD_ID + ":cast.success_continuous",
                    spell.getDescriptionId().toString()));
        }
        return 1;
    }

    private static int handleInstantSpell(CommandSourceStack source, Spell spell, ServerPlayer player, SpellModifiers modifiers) {
        if (spell.cast(new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, modifiers))) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.COMMAND, spell, player, modifiers));
            source.sendSystemMessage(Component.translatable("commands." + WizardryMainMod.MOD_ID + ":cast.success",
                    spell.getLocation().toString()));
        }
        return 0;
    }
}
