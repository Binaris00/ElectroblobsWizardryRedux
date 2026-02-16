package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.CastUtils;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.networking.s2c.SpellCastS2C;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
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
 * <b>Scroll Item!! Fast and easy way to use spells</b>
 * <p>
 * Compared to wands, scrolls are single use items that allow the player to cast a single spell without any mana/charge
 * cost. They are consumed upon use. You can think of them as disposable spellcasting items.
 */
public class ScrollItem extends Item implements ISpellCastingItem, IWorkbenchItem {
    /** The limit time for a continuous spell cast from a scroll. */
    public static final int CASTING_TIME = 120;
    /** Cooldown applied when a spell cast is canceled by forfeit (or any listener from SpellPreCast/SpellTickCast) */
    public static final int COOLDOWN_FORFEIT_TICKS = 60;

    public ScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Spell spell = getCurrentSpell(stack);
        if (spell == Spells.NONE) return InteractionResultHolder.fail(stack);

        SpellModifiers modifiers = new SpellModifiers();
        PlayerCastContext ctx = new PlayerCastContext(level, player, hand, 0, modifiers);

        if (!canCast(stack, spell, ctx)) return InteractionResultHolder.fail(stack);

        if (!spell.isInstantCast()) {
            Services.OBJECT_DATA.getWizardData(player).setSpellModifiers(modifiers);
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        // For instant spells, cast immediately
        if (cast(stack, spell, ctx)) {
            if (!level.isClientSide && spell.requiresPacket()) {
                Services.NETWORK_HELPER.sendToDimension(level.getServer(), new SpellCastS2C(player.getId(), hand, spell, modifiers), level.dimension());
            }
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int timeLeft) {
        if (!(livingEntity instanceof Player player)) return;

        Spell spell = SpellUtil.getSpell(stack);
        int castingTick = stack.getUseDuration() - timeLeft - 1;

        PlayerCastContext ctx = new PlayerCastContext(level, player, player.getUsedItemHand(), castingTick,
                Services.OBJECT_DATA.getWizardData(player).getSpellModifiers());

        if (!spell.isInstantCast()) {
            if (canCast(stack, spell, ctx)) {
                cast(stack, spell, ctx);
            } else {
                livingEntity.stopUsingItem();
            }
        }
    }

    @Override
    public boolean canCast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (CastUtils.fireSpellCastEvent(SpellCastEvent.Source.SCROLL, spell, ctx)) {
            CastUtils.applyCooldownForfeit(ctx.caster(), COOLDOWN_FORFEIT_TICKS);
            return false;
        }
        return true;
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (!CastUtils.executeSpellCast(SpellCastEvent.Source.SCROLL, spell, ctx)) return false;

        if (spell.isInstantCast() && !ctx.caster().isCreative()) {
            stack.shrink(1);
            ctx.caster().getCooldowns().addCooldown(this, spell.getCooldown());
        }

        if (ctx.castingTicks() == 0) {
            CastUtils.trackSpellUsage(ctx.caster(), spell);
        }

        return true;
    }

    private void finishCast(ItemStack stack, Level world, LivingEntity entity, int timeCharged) {
        if (!(entity instanceof Player player)) return;
        Spell spell = SpellUtil.getSpell(stack);
        if (spell.isInstantCast()) return;

        if (!player.isCreative()) {
            stack.shrink(1);
            player.getCooldowns().addCooldown(this, spell.getCooldown());
        }

        int castingTick = stack.getUseDuration() - timeCharged;
        SpellModifiers modifiers = Services.OBJECT_DATA.getWizardData(player).getSpellModifiers();

        WizardryEventBus.getInstance().fire(new SpellCastEvent.Finish(SpellCastEvent.Source.SCROLL, spell, entity, modifiers, castingTick));
        spell.endCast(new CastContext(world, entity, castingTick, modifiers));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        if (level == null) return;
        Spell spell = SpellUtil.getSpell(stack);

        if (ClientUtils.shouldDisplayDiscovered(spell, stack) && tooltipFlag.isAdvanced()) {
            list.add(Component.translatable(spell.getTier().getDescriptionId()).withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable(spell.getElement().getDescriptionId()).withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable(spell.getType().getUnlocalisedName()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        finishCast(stack, level, livingEntity, 0);
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
        finishCast(stack, level, livingEntity, timeCharged);
    }

    @NotNull
    @Override
    public Spell getCurrentSpell(ItemStack stack) {
        return SpellUtil.getSpell(stack);
    }

    @Override
    public boolean showSpellHUD(Player player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (Services.PLATFORM.isDedicatedServer()) {
            Spell spell = SpellUtil.getSpell(stack);
            return Component.translatable("item.ebwizardry.scroll", spell.getDescriptionFormatted());
        }
        return ClientUtils.getScrollDisplayName(stack);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return CASTING_TIME;
    }

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