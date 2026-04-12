package com.binaris.wizardry.content.command.debug;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.content.command.argument.ElementArgument;
import com.binaris.wizardry.content.item.WizardArmorType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

public final class GenArmorCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        dispatcher.register(Commands.literal("ebw")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("generator")
                        .then(Commands.literal("armor")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("element", ElementArgument.element())
                                                .then(Commands.literal("wizard")
                                                        .executes(c -> execute(c, EntityArgument.getPlayer(c, "player"),
                                                                ElementArgument.getElement(c, "element"), WizardArmorType.WIZARD))
                                                )

                                                .then(Commands.literal("sage")
                                                        .executes(c -> execute(c, EntityArgument.getPlayer(c, "player"),
                                                                ElementArgument.getElement(c, "element"), WizardArmorType.SAGE))
                                                )

                                                .then(Commands.literal("battlemage")
                                                        .executes(c -> execute(c, EntityArgument.getPlayer(c, "player"),
                                                                ElementArgument.getElement(c, "element"), WizardArmorType.BATTLEMAGE))
                                                )

                                                .then(Commands.literal("warlock")
                                                        .executes(c -> execute(c, EntityArgument.getPlayer(c, "player"),
                                                                ElementArgument.getElement(c, "element"), WizardArmorType.WARLOCK))
                                                )
                                        )
                                )

                        )
                )
        );
    }


    private static int execute(CommandContext<CommandSourceStack> context, ServerPlayer player, Element element, WizardArmorType type) {
        for (EquipmentSlot slot : InventoryUtil.ARMOR_SLOTS) {
            Item armor = RegistryUtils.getArmor(type, element, slot);
            player.getInventory().add(armor.getDefaultInstance());
        }

        return 1;
    }

    private GenArmorCommand() {
    }
}
