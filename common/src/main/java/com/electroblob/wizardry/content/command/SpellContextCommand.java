package com.electroblob.wizardry.content.command;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellContext;
import com.electroblob.wizardry.core.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class SpellContextCommand {

    private static final SuggestionProvider<CommandSourceStack> CONTEXT_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(SpellContext.getAllKeys(), builder);

    private SpellContextCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spellContext")
                .requires((p) -> p.hasPermission(2))
                .then(Commands.argument("context", StringArgumentType.word())
                        .suggests(CONTEXT_SUGGESTIONS)
                        .executes(c -> listSpellsByContext(
                                c.getSource(),
                                StringArgumentType.getString(c, "context")
                        ))
                )
        );
    }

    // IA slope: this method is long but it's mostly just sending messages
    private static int listSpellsByContext(CommandSourceStack source, String context) {
        if (!SpellContext.isValidKey(context)) {
            source.sendFailure(Component.literal("Invalid context: " + context)
                    .withStyle(ChatFormatting.RED));
            source.sendSystemMessage(Component.literal("Valid contexts: " +
                    String.join(", ", SpellContext.getAllKeys())).withStyle(ChatFormatting.GRAY));
            return 0;
        }

        SpellContext spellContext = SpellContext.fromKey(context);

        List<Spell> enabledSpells = Services.REGISTRY_UTIL.getSpells().stream()
                .filter(spell -> spell.isEnabled(spellContext))
                .toList();

        List<Spell> disabledSpells = Services.REGISTRY_UTIL.getSpells().stream()
                .filter(spell -> !spell.isEnabled(spellContext))
                .toList();

        source.sendSystemMessage(Component.literal("=".repeat(50))
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        source.sendSystemMessage(Component.literal("Spells for context: ")
                .withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(context).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)));

        source.sendSystemMessage(Component.literal("=".repeat(50))
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        // Enabled spells
        source.sendSystemMessage(Component.literal("\n✓ ENABLED (" + enabledSpells.size() + ")")
                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));

        if (enabledSpells.isEmpty()) {
            source.sendSystemMessage(Component.literal("  No enabled spells")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        } else {
            for (Spell spell : enabledSpells) {
                source.sendSystemMessage(Component.literal("  • ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(spell.getDescriptionFormatted())
                        .append(Component.literal(" [" + spell.getLocation() + "]")
                                .withStyle(ChatFormatting.GRAY)));
            }
        }

        // Disabled spells
        source.sendSystemMessage(Component.literal("\n✗ DISABLED (" + disabledSpells.size() + ")")
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));

        if (disabledSpells.isEmpty()) {
            source.sendSystemMessage(Component.literal("  No disabled spells")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        } else {
            for (Spell spell : disabledSpells) {
                source.sendSystemMessage(Component.literal("  • ")
                        .withStyle(ChatFormatting.RED)
                        .append(spell.getDescriptionFormatted())
                        .append(Component.literal(" [" + spell.getLocation() + "]")
                                .withStyle(ChatFormatting.GRAY)));
            }
        }

        source.sendSystemMessage(Component.literal("=".repeat(50))
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        return 1;
    }
}
