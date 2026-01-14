package com.binaris.wizardry.content.command.argument;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.platform.Services;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Custom argument type for Spell objects in commands.
 * This provides proper type safety and validation for spell arguments.
 */
public class SpellArgument implements ArgumentType<Spell> {
    private static final Collection<String> EXAMPLES = Arrays.asList("ebwizardry:magic_missile", "ebwizardry:fireball");
    private static final DynamicCommandExceptionType ERROR_SPELL_NOT_FOUND = new DynamicCommandExceptionType(
            id -> Component.translatable("argument.ebwizardry.spell.notfound", id)
    );

    public SpellArgument() {
    }

    public static SpellArgument spell() {
        return new SpellArgument();
    }

    public static Spell getSpell(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Spell.class);
    }

    @Override
    public Spell parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        ResourceLocation location = ResourceLocation.read(reader);
        Spell spell = Services.REGISTRY_UTIL.getSpell(location);

        if (spell == null) {
            reader.setCursor(cursor);
            throw ERROR_SPELL_NOT_FOUND.createWithContext(reader, location);
        }

        return spell;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
                Services.REGISTRY_UTIL.getSpells().stream().map(Spell::getLocation).map(ResourceLocation::toString).toList(),
                builder,
                value -> value,
                Component::literal
        );
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

