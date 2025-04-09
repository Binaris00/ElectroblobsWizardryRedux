package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScrollItem extends Item implements ISpellCastingItem {
    public ScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        Spell spell = getCurrentSpell(player.getItemInHand(hand));

        if (!spell.isInstantCast()) {
            if(!player.isUsingItem()){
                player.startUsingItem(hand);
            }
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        } else {
            PlayerCastContext ctx = new PlayerCastContext(level, player, hand, 0, new SpellModifiers());
            if(spell.cast(ctx)){
                if(!player.isUsingItem()){
                    player.startUsingItem(hand);
                }
                player.getCooldowns().addCooldown(this, 30);
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
        }
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(@NotNull Level level, LivingEntity livingEntity, @NotNull ItemStack stack, int timeLeft) {
        Spell spell = SpellUtil.getSpell(livingEntity.getItemInHand(livingEntity.getUsedItemHand()));

        int castingTick = stack.getUseDuration() - timeLeft;

        if(!spell.isInstantCast()){
            if(livingEntity instanceof Player player){
                PlayerCastContext ctx = new PlayerCastContext(level, player, player.getUsedItemHand(), castingTick, new SpellModifiers());
                spell.cast(ctx);
            }
        } else {
            livingEntity.stopUsingItem();
        }
    }

    @NotNull
    @Override
    public Spell getCurrentSpell(ItemStack stack) {
        return SpellUtil.getSpell(stack);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable(SpellUtil.getSpellNameTranslationComponent(stack)));
        super.appendHoverText(stack, level, list, tooltipFlag);
    }
}
