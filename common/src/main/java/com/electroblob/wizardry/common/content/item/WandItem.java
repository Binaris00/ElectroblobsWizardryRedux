package com.electroblob.wizardry.common.content.item;

import com.electroblob.wizardry.api.Benchmarker;
import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WandItem extends Item {

    public WandItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        var itemInHand = player.getItemInHand(hand);

        if(!level.isClientSide) {
            Caster.of(player).castSpell(Spells.EXAMPLE);
        }

        return InteractionResultHolder.consume(itemInHand);
    }
}
