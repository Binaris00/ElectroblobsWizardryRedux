package com.binaris.wizardry.content.command.argument;

import com.binaris.wizardry.api.content.spell.Element;
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
 * Custom argument type for Element objects in commands.
 * This provides proper type safety and validation for Element arguments.
 */
public class ElementArgument implements ArgumentType<Element> {
    private static final Collection<String> EXAMPLES = Arrays.asList("ebwizardry:fire", "ebwizardry:ice");
    private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType(
            id -> Component.translatable("argument.ebwizardry.element.notfound", id)
    );

    public ElementArgument() {
    }

    public static ElementArgument element() {
        return new ElementArgument();
    }

    public static Element getElement(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Element.class);
    }

    @Override
    public Element parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        ResourceLocation location = ResourceLocation.read(reader);
        Element element = Services.REGISTRY_UTIL.getElement(location);

        if (element == null) {
            reader.setCursor(cursor);
            throw ERROR_NOT_FOUND.createWithContext(reader, location);
        }

        return element;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
                Services.REGISTRY_UTIL.getElements().stream().map(Element::getLocation).map(ResourceLocation::toString).toList(),
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