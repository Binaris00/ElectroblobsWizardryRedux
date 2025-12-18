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
import com.electroblob.wizardry.setup.registries.Spells;
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
    /** Cooldown applied when a spell cast is cancelled by forfeit (or any listener from SpellPreCast/SpellTickCast) */
    public static final int COOLDOWN_FORFEIT_TICKS = 140;

    public ScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Spell spell = getCurrentSpell(stack);
        if (spell == Spells.NONE) return InteractionResultHolder.fail(stack);

        PlayerCastContext ctx = new PlayerCastContext(level, player, hand, 0, new SpellModifiers());

        if (!canCast(stack, spell, ctx)) return InteractionResultHolder.fail(stack);

        if (spell.isInstantCast()) {
            if (cast(stack, spell, ctx)) {
                if (!level.isClientSide && spell.requiresPacket()) {
                    Services.NETWORK_HELPER.sendToDimension(level.getServer(), new SpellCastS2C(player.getId(), hand, spell, ctx.modifiers()), level.dimension());
                }
                return InteractionResultHolder.success(stack);
            }
        } else {
            if (!player.isUsingItem()) player.startUsingItem(hand);
            Services.OBJECT_DATA.getWizardData(player).setSpellModifiers(ctx.modifiers());
            return InteractionResultHolder.pass(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int timeLeft) {
        if (!(livingEntity instanceof Player player)) return;

        Spell spell = SpellUtil.getSpell(stack);
        int castingTick = stack.getUseDuration() - timeLeft;
        PlayerCastContext ctx = new PlayerCastContext(level, player, player.getUsedItemHand(), castingTick, new SpellModifiers());

        if (!spell.isInstantCast() && canCast(stack, spell, ctx)) {
            spell.cast(ctx);
        } else {
            livingEntity.stopUsingItem();
        }
    }

    @Override
    public boolean canCast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        SpellCastEvent event = ctx.castingTicks() == 0
                ? new SpellCastEvent.Pre(SpellCastEvent.Source.WAND, spell, ctx.caster(), ctx.modifiers())
                : new SpellCastEvent.Tick(SpellCastEvent.Source.WAND, spell, ctx.caster(), ctx.modifiers(), ctx.castingTicks());

        if (WizardryEventBus.getInstance().fire(event)) {
            ctx.caster().getCooldowns().addCooldown(this, COOLDOWN_FORFEIT_TICKS);
            return false;
        }
        return true;
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (!spell.cast(ctx)) return false;

        if (ctx.castingTicks() == 0) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.SCROLL, spell, ctx.caster(), ctx.modifiers()));
        }

        if (spell.isInstantCast() && !ctx.caster().isCreative()) {
            stack.shrink(1);
            ctx.caster().getCooldowns().addCooldown(this, spell.getCooldown());
        }

        Services.OBJECT_DATA.getWizardData(ctx.caster()).trackRecentSpell(spell, ctx.caster().level().getGameTime());
        return true;
    }

    private void finishCast(ItemStack stack, Level world, LivingEntity entity, int timeCharged) {
        if (!(entity instanceof Player player)) return;
        Spell spell = SpellUtil.getSpell(stack);
        if (spell.isInstantCast()) return;

        if (!player.isCreative()) stack.shrink(1);

        int castingTick = stack.getUseDuration() - timeCharged;
        SpellModifiers modifiers = new SpellModifiers();

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