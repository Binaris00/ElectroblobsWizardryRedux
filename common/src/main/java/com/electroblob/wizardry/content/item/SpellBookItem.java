package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellBookItem extends Item {
    public SpellBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        // TODO: Spell book GUI
        return super.use(level, player, interactionHand);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        Spell spell = SpellUtil.getSpell(stack);
        if(spell.getElement() == null) return;
        // TODO FAST CHECK HERE

        list.add(Component.translatable(SpellUtil.getSpellNameTranslationComponent(stack)).withStyle(spell.getElement().getColor()));
        list.add(spell.getTier().getNameForTranslationFormatted());

    }
}
