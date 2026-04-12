package com.binaris.wizardry.content.command.debug;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.command.argument.ElementArgument;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.binaris.wizardry.content.command.argument.SpellTierArgument;
import com.binaris.wizardry.content.entity.living.AbstractWizard;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public final class GenWizardCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("ebw")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("generator")
                        .then(Commands.literal("wizard")
                                .then(Commands.argument("element", ElementArgument.element())
                                        .then(Commands.argument("tier", SpellTierArgument.tier())
                                                .then(Commands.argument("isEvil", BoolArgumentType.bool())
                                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                                .then(Commands.argument("spell1", SpellArgument.spell())
                                                                        .then(Commands.argument("spell2", SpellArgument.spell())
                                                                                .then(Commands.argument("spell3", SpellArgument.spell())
                                                                                        .then(Commands.argument("spell4", SpellArgument.spell())
                                                                                                .then(Commands.argument("spell5", SpellArgument.spell())
                                                                                                        .executes(c -> execute(c, ElementArgument.getElement(c, "element"),
                                                                                                                SpellTierArgument.getTier(c, "tier"),
                                                                                                                BoolArgumentType.getBool(c, "isEvil"), BlockPosArgument.getBlockPos(c, "pos"),
                                                                                                                SpellArgument.getSpell(c, "spell1"), SpellArgument.getSpell(c, "spell2"),
                                                                                                                SpellArgument.getSpell(c, "spell3"), SpellArgument.getSpell(c, "spell4"), SpellArgument.getSpell(c, "spell5")))
                                                                                                )

                                                                                                .executes(c -> execute(c, ElementArgument.getElement(c, "element"),
                                                                                                        SpellTierArgument.getTier(c, "tier"),
                                                                                                        BoolArgumentType.getBool(c, "isEvil"), BlockPosArgument.getBlockPos(c, "pos"),
                                                                                                        SpellArgument.getSpell(c, "spell1"), SpellArgument.getSpell(c, "spell2"),
                                                                                                        SpellArgument.getSpell(c, "spell3"), SpellArgument.getSpell(c, "spell4")))
                                                                                        )

                                                                                        .executes(c -> execute(c, ElementArgument.getElement(c, "element"),
                                                                                                SpellTierArgument.getTier(c, "tier"),
                                                                                                BoolArgumentType.getBool(c, "isEvil"), BlockPosArgument.getBlockPos(c, "pos"),
                                                                                                SpellArgument.getSpell(c, "spell1"), SpellArgument.getSpell(c, "spell2"),
                                                                                                SpellArgument.getSpell(c, "spell3")))
                                                                                )

                                                                                .executes(c -> execute(c, ElementArgument.getElement(c, "element"),
                                                                                        SpellTierArgument.getTier(c, "tier"),
                                                                                        BoolArgumentType.getBool(c, "isEvil"), BlockPosArgument.getBlockPos(c, "pos"),
                                                                                        SpellArgument.getSpell(c, "spell1"), SpellArgument.getSpell(c, "spell2")))
                                                                        )
                                                                        .executes(c -> execute(c, ElementArgument.getElement(c, "element"),
                                                                                SpellTierArgument.getTier(c, "tier"),
                                                                                BoolArgumentType.getBool(c, "isEvil"), BlockPosArgument.getBlockPos(c, "pos"),
                                                                                SpellArgument.getSpell(c, "spell1")))
                                                                )
                                                                .executes(c -> execute(c, ElementArgument.getElement(c, "element"),
                                                                        SpellTierArgument.getTier(c, "tier"),
                                                                        BoolArgumentType.getBool(c, "isEvil"), BlockPosArgument.getBlockPos(c, "pos")))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context, Element element, SpellTier tier, boolean isEvil, BlockPos pos, Spell... spells) {
        AbstractWizard wizard;

        if (isEvil) wizard = EBEntities.EVIL_WIZARD.get().create(context.getSource().getLevel());
        else wizard = EBEntities.WIZARD.get().create(context.getSource().getLevel());

        wizard.setTextureIndex(wizard.getRandom().nextInt(6));
        wizard.setElement(element);

        wizard.equipArmorAndDisableDrops(element);

        if (spells.length != 0) {
            wizard.setSpells(List.of(spells));
        } else {
            ArrayList<Spell> spellArrayList = new ArrayList<>();
            EntityUtil.populateSpells(spellArrayList, element, false, 3, wizard.getRandom());
            wizard.setSpells(spellArrayList);
        }

        wizard.prepareWandWithSpells(element, tier);
        context.getSource().getLevel().addFreshEntity(wizard);
        return 1;
    }


    private GenWizardCommand() {

    }
}
