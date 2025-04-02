package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.Spell;
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

public class ScrollItem extends Item {

    public ScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        Spell spell = SpellUtil.getSpell(player.getItemInHand(interactionHand));

        if (spell == null) {
            EBLogger.info(Component.literal("Spell is null"));
            return InteractionResultHolder.fail(player.getItemInHand(interactionHand));
        }

        if (!spell.isInstantCast()) {
            if(!player.isUsingItem()){
                player.startUsingItem(interactionHand);
            }
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        } else {
            Caster.of(player).castSpell(spell);
            player.getCooldowns().addCooldown(this, 60);
        }
        return InteractionResultHolder.success(player.getItemInHand(interactionHand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        Spell spell = SpellUtil.getSpell(livingEntity.getItemInHand(livingEntity.getUsedItemHand()));

        //EBLogger.info(Component.literal("Spell: " + spell));

        if(spell == null) {
            EBLogger.info(Component.literal("Spell is null"));
            return;
        }

        if(!spell.isInstantCast()){
            Caster.of((Player) livingEntity).castSpell(spell);
        } else {
            livingEntity.stopUsingItem();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {

        list.add(Component.translatable(SpellUtil.getSpellNameTranslationComponent(stack)));

        super.appendHoverText(stack, level, list, tooltipFlag);
    }
}
