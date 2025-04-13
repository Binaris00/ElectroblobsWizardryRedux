package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

// TODO IMPLEMENT CUSTOM EVENTS :D
public class WandItem extends Item implements ISpellCastingItem {

    public WandItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        var itemInHand = player.getItemInHand(hand);

        Spell spell = getCurrentSpell(player.getMainHandItem());

        SpellModifiers modifiers = new SpellModifiers();

        int charge = (int)(spell.getCharge() * modifiers.get(SpellModifiers.CHARGEUP));
        // Chargeup or continues spells (it could be both too)
        if(!spell.isInstantCast() || charge > 0){
            if(!player.isUsingItem()){
                player.startUsingItem(hand);
            }
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        } else {
            PlayerCastContext ctx = new PlayerCastContext(level, player, hand, 0, new SpellModifiers());
            if(spell.cast(ctx)){
                player.getCooldowns().addCooldown(this, 30);
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
        }

        return InteractionResultHolder.fail(itemInHand);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int timeLeft) {
        Spell spell = getCurrentSpell(stack);

        SpellModifiers modifiers = new SpellModifiers();
        int castingTick = stack.getUseDuration() - timeLeft;
        int chargeup = (int)(spell.getCharge() * modifiers.get(SpellModifiers.CHARGEUP));

        if(!spell.isInstantCast()) {
            if (castingTick >= chargeup) {
                // TODO CHECK?
                if(livingEntity instanceof Player player){
                    PlayerCastContext ctx = new PlayerCastContext(level, player, player.getUsedItemHand(), castingTick, new SpellModifiers());
                    spell.cast(ctx);
                }
            } else {
                if(livingEntity instanceof Player player){
                    PlayerCastContext ctx = new PlayerCastContext(level, player, player.getUsedItemHand(), castingTick, new SpellModifiers());
                    spell.onCharge(ctx);
                }
            }
        } else {
            livingEntity.stopUsingItem();
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @NotNull
    @Override
    public Spell getCurrentSpell(ItemStack stack) {
        return Spells.DART;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        Spell spell = SpellUtil.getSpell(stack);
        if(spell.isInstantCast()) return super.finishUsingItem(stack, level, livingEntity);

        SpellModifiers modifiers = new SpellModifiers();
        // TODO livingEntity.getUseItemRemainingTicks() TEMP FIX MAYBE????
        int castingTick = stack.getUseDuration() - livingEntity.getUseItemRemainingTicks();

        WizardryEventBus.getInstance().fire(new SpellCastEvent.Finish(SpellCastEvent.Source.SCROLL, spell, livingEntity, modifiers, castingTick));
        spell.endCast(new CastContext(livingEntity.level(), castingTick, modifiers) {
            @Override
            public LivingEntity caster() {
                return livingEntity;
            }
        });
        return super.finishUsingItem(stack, level, livingEntity);
    }
}
