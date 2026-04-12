package com.binaris.wizardry.content.command.debug;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.api.content.util.WandHelper;
import com.binaris.wizardry.content.command.argument.ElementArgument;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.binaris.wizardry.content.command.argument.SpellTierArgument;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public final class GenWandCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("ebw")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("generator")
                        .then(Commands.literal("wand")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("element", ElementArgument.element())
                                                .then(Commands.argument("tier", SpellTierArgument.tier())
                                                        .then(Commands.argument("spell1", SpellArgument.spell())
                                                                .then(Commands.argument("spell2", SpellArgument.spell())
                                                                        .then(Commands.argument("spell3", SpellArgument.spell())
                                                                                .then(Commands.argument("spell4", SpellArgument.spell())
                                                                                        .then(Commands.argument("spell5", SpellArgument.spell())
                                                                                                .executes((c) ->
                                                                                                        execute(c, EntityArgument.getPlayer(c, "player"),
                                                                                                                ElementArgument.getElement(c, "element"),
                                                                                                                SpellTierArgument.getTier(c, "tier"),
                                                                                                                SpellArgument.getSpell(c, "spell1"),
                                                                                                                SpellArgument.getSpell(c, "spell2"),
                                                                                                                SpellArgument.getSpell(c, "spell3"),
                                                                                                                SpellArgument.getSpell(c, "spell4"),
                                                                                                                SpellArgument.getSpell(c, "spell5")
                                                                                                        )
                                                                                                )
                                                                                        )

                                                                                        .executes((c) ->
                                                                                                execute(c, EntityArgument.getPlayer(c, "player"),
                                                                                                        ElementArgument.getElement(c, "element"),
                                                                                                        SpellTierArgument.getTier(c, "tier"),
                                                                                                        SpellArgument.getSpell(c, "spell1"),
                                                                                                        SpellArgument.getSpell(c, "spell2"),
                                                                                                        SpellArgument.getSpell(c, "spell3"),
                                                                                                        SpellArgument.getSpell(c, "spell4")
                                                                                                )
                                                                                        )
                                                                                )

                                                                                .executes((c) ->
                                                                                        execute(c, EntityArgument.getPlayer(c, "player"),
                                                                                                ElementArgument.getElement(c, "element"),
                                                                                                SpellTierArgument.getTier(c, "tier"),
                                                                                                SpellArgument.getSpell(c, "spell1"),
                                                                                                SpellArgument.getSpell(c, "spell2"),
                                                                                                SpellArgument.getSpell(c, "spell3")
                                                                                        )
                                                                                )
                                                                        )


                                                                        .executes((c) ->
                                                                                execute(c, EntityArgument.getPlayer(c, "player"),
                                                                                        ElementArgument.getElement(c, "element"),
                                                                                        SpellTierArgument.getTier(c, "tier"),
                                                                                        SpellArgument.getSpell(c, "spell1"),
                                                                                        SpellArgument.getSpell(c, "spell2")
                                                                                )
                                                                        )
                                                                )

                                                                .executes((c) ->
                                                                        execute(c, EntityArgument.getPlayer(c, "player"),
                                                                                ElementArgument.getElement(c, "element"),
                                                                                SpellTierArgument.getTier(c, "tier"),
                                                                                SpellArgument.getSpell(c, "spell1")
                                                                        )
                                                                )

                                                        )
                                                        .executes((c) ->
                                                                execute(c, EntityArgument.getPlayer(c, "player"),
                                                                        ElementArgument.getElement(c, "element"),
                                                                        SpellTierArgument.getTier(c, "tier")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, ServerPlayer player, Element element, SpellTier tier, Spell... spells) {
        Item wand = RegistryUtils.getWand(tier, element);

        if (wand == null) {
            context.getSource().sendFailure(Component.translatable("commands.ebwizardry.generator_wand.no_wand", element.getDescriptionFormatted(), tier.getDescriptionFormatted()));
            return 0;
        }

        ItemStack stack = wand.getDefaultInstance();
        if (spells.length != 0) WandHelper.setSpells(stack, Arrays.stream(spells).toList());
        player.getInventory().add(stack);
        return 1;
    }

    private GenWandCommand() {
    }
}
