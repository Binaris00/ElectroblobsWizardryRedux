package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.hell.BetterWizardData;
import com.electroblob.wizardry.api.content.hell.BinWizardDataInternal;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.ChatFormatting;
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

        SpellModifiers modifiers = new SpellModifiers();

        // TODO REMOVE THIS, TEMP TEMP
//        if(!level.isClientSide){
//            BinWizardDataInternal wizardData = Services.WIZARD_DATA.getWizardData(player, level);
//            BetterWizardData betterWizardData = new BetterWizardData(wizardData);
//            if(!betterWizardData.hasSpellBeenDiscovered(spell)){
//                player.sendSystemMessage(Component.literal("You didn't discover: " + spell.getLocation()).withStyle(ChatFormatting.ITALIC));
//                player.sendSystemMessage(Component.literal("Discovering!! " + spell.getLocation()).withStyle(ChatFormatting.AQUA));
//                betterWizardData.discoverSpell(spell);
//                player.getCooldowns().addCooldown(this, 20);
//                return InteractionResultHolder.fail(player.getItemInHand(hand));
//            } else {
//                player.sendSystemMessage(Component.literal("You knew this spell :0 -> " + spell.getLocation()).withStyle(ChatFormatting.ITALIC));
//            }
//        }


        if(canCast(player.getItemInHand(hand), spell, player, hand, 0, modifiers)) {
            if (!spell.isInstantCast()) {
                if (!player.isUsingItem()) {
                    player.startUsingItem(hand);
                }
                return InteractionResultHolder.pass(player.getItemInHand(hand));
            } else {
                PlayerCastContext ctx = new PlayerCastContext(level, player, hand, 0, modifiers);
                if (spell.cast(ctx)) {
                    if (!player.isUsingItem()) {
                        player.startUsingItem(hand);
                    }
                    player.getCooldowns().addCooldown(this, 30);
                    return InteractionResultHolder.success(player.getItemInHand(hand));
                }
            }
        }
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    public boolean canCast(ItemStack stack, Spell spell, Player caster, InteractionHand hand, int castingTick, SpellModifiers modifiers){
        // Even neater!
        if(castingTick == 0){
            return !WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.SCROLL, spell, caster, modifiers));
        }else{
            return !WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(SpellCastEvent.Source.SCROLL, spell, caster, modifiers, castingTick));
        }
    }

    @Override
    public void onUseTick(@NotNull Level level, LivingEntity livingEntity, @NotNull ItemStack stack, int timeLeft) {
        Spell spell = SpellUtil.getSpell(livingEntity.getItemInHand(livingEntity.getUsedItemHand()));
        if(!(livingEntity instanceof Player player)) return;

        int castingTick = stack.getUseDuration() - timeLeft;

        SpellModifiers modifiers = new SpellModifiers();

        if(!spell.isInstantCast() && canCast(stack, spell, player, player.getUsedItemHand(), castingTick, modifiers)){
            PlayerCastContext ctx = new PlayerCastContext(level, player, player.getUsedItemHand(), castingTick, modifiers);
            spell.cast(ctx);
        } else {
            livingEntity.stopUsingItem();
        }
    }

//    @Override
//    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
//        Spell spell = SpellUtil.getSpell(stack);
//        if(spell.isInstantCast()) return super.finishUsingItem(stack, level, livingEntity);
//
//        SpellModifiers modifiers = new SpellModifiers();
//        // TODO livingEntity.getUseItemRemainingTicks() TEMP FIX MAYBE????
//        int castingTick = stack.getUseDuration() - livingEntity.getUseItemRemainingTicks();
//
//        WizardryEventBus.getInstance().fire(new SpellCastEvent.Finish(SpellCastEvent.Source.SCROLL, spell, livingEntity, modifiers, castingTick));
//        spell.endCast(new CastContext(livingEntity.level(), castingTick, modifiers) {
//            @Override
//            public LivingEntity caster() {
//                return livingEntity;
//            }
//        });
//        return super.finishUsingItem(stack, level, livingEntity);
//    }

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
