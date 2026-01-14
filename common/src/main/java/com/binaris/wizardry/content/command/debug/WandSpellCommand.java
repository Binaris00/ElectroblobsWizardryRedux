package com.binaris.wizardry.content.command.debug;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.WandHelper;
import com.binaris.wizardry.content.command.argument.SpellArgument;
import com.binaris.wizardry.content.item.WandItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class WandSpellCommand {

    private WandSpellCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("wand")
                .then(Commands.argument("spell", SpellArgument.spell())
                        .then(Commands.argument("slot", IntegerArgumentType.integer(0))
                                .executes((c) -> execute(c, SpellArgument.getSpell(c, "spell"), IntegerArgumentType.getInteger(c, "slot"))))));
    }

    private static int execute(CommandContext<CommandSourceStack> context, Spell spell, int slot) {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.translatable("command.ebwizardry.wand.not_player"));
            return 0;
        }
        ServerPlayer player = source.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof WandItem wandItem)) {
            context.getSource().sendFailure(Component.translatable("command.ebwizardry.wand.not_holding_wand"));
            return 0;
        }

        if (spell == null) {
            context.getSource().sendFailure(Component.translatable("command.ebwizardry.wand.spell_not_found"));
            return 0;
        }

        int maxSlots = wandItem.getSpellSlotCount(stack);
        if (slot < 0 || slot >= maxSlots) {
            context.getSource().sendFailure(Component.translatable("command.ebwizardry.wand.invalid_slot", maxSlots - 1));
            return 0;
        }

        java.util.List<Spell> spells = WandHelper.getSpells(stack);

        int currentSelectedIndex = spells.indexOf(WandHelper.getCurrentSpell(stack));
        spells.set(slot, spell);

        if (currentSelectedIndex == slot) {
            WandHelper.setCurrentSpell(stack, spell);
        }

        WandHelper.setSpells(stack, spells);
        context.getSource().sendSystemMessage(Component.translatable("command.ebwizardry.wand.success", spell.getLocation(), slot));
        return 1;
    }
}
