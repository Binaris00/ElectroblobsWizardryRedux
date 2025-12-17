package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.client.util.ClientUtils;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.item.IWorkbenchItem;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.networking.s2c.SpellCastS2C;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <b>Scroll Item!! Fast and easy way to use spells</b> <br>
 * This contains some special features comparing it with the wand:
 * <ul>
 *     <li>Not showing an actual spell gui</li>
 *     <li>Saving one spell for scroll (more easy to get the saved spell)</li>
 *     <li>Temporal use item, after release it will disappear</li>
 * </ul>
 */
public class ScrollItem extends Item implements ISpellCastingItem, IWorkbenchItem {
    public static final int CASTING_TIME = 120;
    public static final int COOLDOWN_FORFEIT_TICKS = 140;

    public ScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        Spell spell = getCurrentSpell(player.getItemInHand(hand));
        SpellModifiers modifiers = new SpellModifiers();

        PlayerCastContext ctx = new PlayerCastContext(level, player, player.getUsedItemHand(), 0, modifiers);
        if (!canCast(player.getItemInHand(hand), spell, ctx))
            return InteractionResultHolder.fail(player.getItemInHand(hand));

        if (spell.isInstantCast()) {
            if (cast(player.getItemInHand(hand), spell, ctx)) {
                if (!level.isClientSide && spell.requiresPacket()) {
                    SpellCastS2C msg = new SpellCastS2C(player.getId(), hand, spell, ctx.modifiers());
                    Services.NETWORK_HELPER.sendToDimension(level.getServer(), msg, level.dimension());
                }
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
        } else {
            if (!player.isUsingItem()) player.startUsingItem(hand);
            Services.OBJECT_DATA.getWizardData(player).setSpellModifiers(ctx.modifiers());
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int timeLeft) {
        if (!(livingEntity instanceof Player player)) return;
        Spell spell = SpellUtil.getSpell(livingEntity.getItemInHand(livingEntity.getUsedItemHand()));
        SpellModifiers modifiers = new SpellModifiers();

        int castingTick = stack.getUseDuration() - timeLeft;

        // Continuous spells
        PlayerCastContext ctx = new PlayerCastContext(level, player, player.getUsedItemHand(), castingTick, modifiers);
        if (!spell.isInstantCast() && canCast(stack, spell, ctx)) {
            spell.cast(ctx);
        } else {
            livingEntity.stopUsingItem();
        }
    }

    // Just checking the spell cast events
    @Override
    public boolean canCast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (ctx.castingTicks() == 0) {
            if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.WAND, spell, ctx.caster(), ctx.modifiers()))) {
                // We want to add a short cooldown if the spell is cancelled at the start
                ctx.caster().getCooldowns().addCooldown(this, COOLDOWN_FORFEIT_TICKS);
                return false;
            }
        } else {
            if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(SpellCastEvent.Source.WAND, spell, ctx.caster(), ctx.modifiers(), ctx.castingTicks()))) {
                // We want to add a short cooldown if the spell is cancelled at the start
                ctx.caster().getCooldowns().addCooldown(this, COOLDOWN_FORFEIT_TICKS);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (!spell.cast(ctx)) return false;

        if (ctx.castingTicks() == 0)
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.SCROLL, spell, ctx.caster(), ctx.modifiers()));
        if (spell.isInstantCast() && !ctx.caster().isCreative()) stack.shrink(1);
        if (spell.isInstantCast() && !ctx.caster().isCreative())
            ctx.caster().getCooldowns().addCooldown(this, spell.getCooldown());

        Services.OBJECT_DATA.getWizardData(ctx.caster()).trackRecentSpell(spell, ctx.caster().level().getGameTime());
        return true;
    }

    // When the player has finished the using time of the scroll, after that the scroll will vanish,
    // use this if you want to give a temporal spell effect
    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        finishCast(stack, level, livingEntity, 0);
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
        finishCast(stack, level, livingEntity, timeCharged);
    }

    private void finishCast(ItemStack stack, Level world, LivingEntity entity, int timeCharged) {
        if (!(entity instanceof Player player)) return;
        Spell spell = SpellUtil.getSpell(stack);
        if (spell.isInstantCast()) return;
        if (!player.isCreative()) stack.shrink(1);

        SpellModifiers modifiers = new SpellModifiers();
        int castingTick = stack.getUseDuration() - timeCharged;
        WizardryEventBus.getInstance().fire(new SpellCastEvent.Finish(SpellCastEvent.Source.SCROLL, spell, entity, modifiers, castingTick));

        // I'm not proud of this
        spell.endCast(new CastContext(entity.level(), castingTick, modifiers) {
            @Override
            public LivingEntity caster() {
                return entity;
            }
        });
    }

    // This item just saves one spell
    @NotNull
    @Override
    public Spell getCurrentSpell(ItemStack stack) {
        return SpellUtil.getSpell(stack);
    }

    @Override
    public boolean showSpellHUD(Player player, ItemStack stack) {
        return false;
    }

    // =====================================
    // item util
    // =====================================
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return ClientUtils.getScrollDisplayName(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        if (level == null) return;
        Spell spell = SpellUtil.getSpell(stack);
        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, stack);

        if (discovered && tooltipFlag.isAdvanced()) {
            list.add(Component.translatable(spell.getTier().getDescriptionId()).withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable(spell.getElement().getDescriptionId()).withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable(spell.getType().getUnlocalisedName()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return CASTING_TIME;
    }

    // =====================================
    // Workbench Item Methods
    // =====================================
    @Override
    public boolean showTooltip(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        return false;
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean canPlace(ItemStack stack) {
        return false;
    }
}