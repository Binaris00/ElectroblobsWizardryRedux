package com.binaris.wizardry.content.command.argument;

import com.binaris.wizardry.api.content.spell.SpellTier;
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
 * Custom argument type for spell tier objects in commands.
 * This provides proper type safety and validation for spell tier arguments.
 */
public class SpellTierArgument implements ArgumentType<SpellTier> {
    private static final Collection<String> EXAMPLES = Arrays.asList("ebwizardry:apprentice", "ebwizardry:advanced");
    private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType(
            id -> Component.translatable("argument.ebwizardry.spelltier.notfound", id)
    );

    public SpellTierArgument() {
    }

    public static SpellTierArgument tier() {
        return new SpellTierArgument();
    }

    public static SpellTier getTier(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, SpellTier.class);
    }

    @Override
    public SpellTier parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        ResourceLocation location = ResourceLocation.read(reader);
        SpellTier tier = Services.REGISTRY_UTIL.getTier(location);

        if (tier == null) {
            reader.setCursor(cursor);
            throw ERROR_NOT_FOUND.createWithContext(reader, location);
        }

        return tier;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
                Services.REGISTRY_UTIL.getTiers().stream().map(SpellTier::getLocation).map(ResourceLocation::toString).toList(),
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