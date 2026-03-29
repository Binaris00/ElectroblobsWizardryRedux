package com.binaris.wizardry.content.command.argument;

import com.binaris.wizardry.api.content.spell.SpellCondition;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * An argument type for parsing {@link SpellCondition} instances from command input.
 */
public class SpellConditionArgument implements ArgumentType<SpellCondition> {
    private static final DynamicCommandExceptionType ERROR = new DynamicCommandExceptionType(msg -> Component.literal((String) msg));

    public static SpellConditionArgument spellCondition() {
        return new SpellConditionArgument();
    }

    public static SpellCondition getSpellCondition(CommandContext<CommandSourceStack> ctx, String name) {
        return ctx.getArgument(name, SpellCondition.class);
    }

    /**
     * Parses a {@link SpellCondition} from the provided {@link StringReader}.
     * Reads the input until a space or the end of the input is reached, then attempts to parse it using {@link SpellCondition#parse(String)}.
     *
     * @param reader the string reader containing the input to parse
     * @return the parsed {@link SpellCondition}
     * @throws CommandSyntaxException if the input cannot be parsed into a valid {@link SpellCondition}
     */
    @Override
    public SpellCondition parse(StringReader reader) throws CommandSyntaxException {
        // Read until end of input or a space (Brigadier will stop the reader for us)
        int start = reader.getCursor();
        while (reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        String raw = reader.getString().substring(start, reader.getCursor());

        try {
            SpellCondition condition = SpellCondition.parse(raw);
            if (condition == null) throw ERROR.createWithContext(reader, "Empty condition string");
            return condition;
        } catch (IllegalArgumentException e) {
            throw ERROR.createWithContext(reader, e.getMessage());
        }
    }
}
