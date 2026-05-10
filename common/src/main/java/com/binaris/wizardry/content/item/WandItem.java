package com.binaris.wizardry.content.item;

import com.binaris.wizardry.api.content.data.WizardData;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.*;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellContext;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.*;
import com.binaris.wizardry.core.ClientSpellSoundManager;
import com.binaris.wizardry.core.EBConstants;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Where the magic (normally) happens!! Most of the functions and logic for this item are handled on various utils
 * classes (e.g. {@link CastItemDataHelper} and {@link CastItemUtils}), so refer to those for more details on specific aspects of
 * wand functionality.
 * <p>
 * A wand is a spell-casting item that can store mana, hold multiple spells, and be upgraded in various ways. Wands
 * can cast both instant and continuous spells, with mechanics for charging, cooldowns, and mana consumption. They can be
 * upgraded using special items to enhance their capabilities, such as increasing mana capacity or adding spell slots.
 *
 * @see ICastItem
 * @see ICustomDamageItem
 */
public class WandItem extends Item implements ICastItem, IManaItem, IWorkbenchItem, ICustomDamageItem, ITierValue, IElementValue {
    /* Base number of spell slots on a wand without upgrades. */
    public static final int BASE_SPELL_SLOTS = 5;
    /** Cooldown applied when a spell cast is canceled by forfeit (or any listener from SpellPreCast/SpellTickCast) */
    public static final int COOLDOWN_FORFEIT_TICKS = 60;
    /** Maximum use duration for continuous spells. */
    public static final int MAX_USE_DURATION = 72000;

    private final SpellTier tier;
    private final Element element;

    public WandItem(SpellTier tier, Element element) {
        super(new Properties().stacksTo(1).durability(tier.getMaxCharge()));
        this.tier = tier;
        this.element = element;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Spell spell = CastItemDataHelper.getCurrentSpell(stack);
        if (spell == Spells.NONE) return InteractionResultHolder.pass(stack);

        PlayerCastContext ctx = createContext(level, player, hand, 0, stack, spell);
        if (!canCastRequirements(stack, spell, ctx)) return InteractionResultHolder.fail(stack);
        int charge = CastItemUtils.calcCharge(spell, ctx.modifiers());

        // if it requires charging or is a continuous spell
        if (!(!spell.isInstantCast() || charge > 0)) {
            // Instant spell without charge
            if (canCast(stack, spell, ctx)) {
                if (!level.isClientSide) {
                    consumeManaAndSetCooldown(stack, spell, player, ctx.modifiers());
                }
                if (cast(stack, spell, ctx)) {
                    return InteractionResultHolder.success(stack);
                }
            }
            return InteractionResultHolder.fail(stack);
        }

        return startCharging(level, player, hand, stack, charge, ctx);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity user, @NotNull ItemStack stack, int timeLeft) {
        if (!(user instanceof Player player)) return;

        Spell spell = CastItemDataHelper.getCurrentSpell(stack);
        SpellModifiers modifiers = Services.OBJECT_DATA.getWizardData(player).getSpellModifiers();
        int useTick = stack.getUseDuration() - timeLeft;
        int charge = CastItemUtils.calcCharge(spell, modifiers);

        int castingTick;
        if (spell.isInstantCast()) {
            castingTick = (useTick == charge) ? 0 : -1;
        } else {
            castingTick = Math.max(0, useTick - charge);
        }

        PlayerCastContext ctx = createContext(level, player, user.getUsedItemHand(), castingTick, stack, spell);

        if (spell.isInstantCast()) {
            handleInstantSpellTick(ctx, spell, stack, useTick, charge);
        } else {
            handleContinuousSpellTick(ctx, spell, stack, useTick, charge);
        }
    }

    @Override
    public boolean canCast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (CastItemUtils.fireSpellCastEvent(SpellCastEvent.Source.WAND, spell, ctx)) {
            CastItemUtils.applyCooldownForfeit(ctx.caster(), COOLDOWN_FORFEIT_TICKS);
            return false;
        }
        return canCastRequirements(stack, spell, ctx);
    }

    private boolean canCastRequirements(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        int cost = CastItemUtils.calcCastCost(spell, ctx.modifiers());
        if (!spell.isInstantCast()) cost = CastItemUtils.getDistributedCastCost(cost, ctx.castingTicks());

        return cost <= this.getMana(stack)
                && spell.getTier().getLevel() <= this.tier.getLevel()
                && (CastItemDataHelper.getCurrentCooldown(stack, ctx.world().getGameTime()) == 0 || ctx.caster().isCreative());
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (ctx.world().isClientSide && spell.isInstantCast() && spell.requiresPacket()) return false;
        if (!CastItemUtils.executeSpellCast(SpellCastEvent.Source.WAND, spell, ctx)) return false;

        CastItemUtils.sendSpellCastPacket(ctx.caster(), spell, ctx);
        if (!spell.isInstantCast()) ctx.caster().startUsingItem(ctx.hand());

        handleProgression(ctx, spell, stack);
        if (spell.isInstantCast()) CastItemUtils.trackSpellUsage(ctx.caster(), spell);
        return true;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity interactionTarget, @NotNull InteractionHand usedHand) {
        if (player.isCrouching() && interactionTarget instanceof Player playerTarget) {
            WizardData data = Services.OBJECT_DATA.getWizardData(player);
            String string = data.toggleAlly(playerTarget) ? "item.ebwizardry.wand.add_ally" : "item.ebwizardry.wand.remove_ally";
            if (!player.level().isClientSide)
                player.sendSystemMessage(Component.translatable(string, playerTarget.getName()));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
        if (!(livingEntity instanceof Player player)) return;
        Spell spell = CastItemDataHelper.getCurrentSpell(stack);
        SpellModifiers modifiers;
        WizardData wizardData = Services.OBJECT_DATA.getWizardData(player);
        modifiers = wizardData.getSpellModifiers();

        int castingTick = stack.getUseDuration() - timeCharged;
        int totalCost = CastItemUtils.calcCastCost(spell, modifiers);
        int accumulatedCost = CastItemUtils.getAccumulatedCastCost(spell, castingTick, totalCost);

        if (!spell.isInstantCast() && spell.getTier().getLevel() <= this.tier.getLevel()) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Finish(SpellCastEvent.Source.WAND, spell, livingEntity, modifiers, castingTick));
            spell.endCast(new CastContext(player.level(), player, castingTick, modifiers));

            if (!level.isClientSide) {
                this.consumeMana(stack, accumulatedCost, player);
                if (!player.isCreative()) CastItemDataHelper.setCurrentCooldown(stack, totalCost, level.getGameTime());
                CastItemUtils.trackSpellUsage(player, spell);
            }
        }
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        boolean changed = false;
        if (upgrade.hasItem()) changed = applyUpgradeSlot(player, centre, upgrade);

        changed |= WorkbenchUtils.applySpellBooks(centre, spellBooks, SpellContext.WANDS);
        changed |= WorkbenchUtils.rechargeManaFromCrystals(centre, crystals);
        return changed;
    }

    @Override
    public ItemStack applyUpgrade(@Nullable Player player, ItemStack wand, ItemStack upgrade) {
        if (upgrade.getItem() instanceof ArcaneTomeItem) {
            return applyTierUpgrade(player, wand, upgrade);
        } else if (WandUpgrades.isWandUpgrade(upgrade.getItem())) {
            applySpecialUpgrade(player, wand, upgrade);
        }
        return wand;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean isHeldInMainHand) {
        if (!world.isClientSide && !this.isManaFull(stack) && world.getGameTime() % EBConstants.CONDENSER_TICK_INTERVAL == 0) {
            this.rechargeMana(stack, CastItemDataHelper.getUpgradeLevel(stack, EBItems.CONDENSER_UPGRADE.get()));
        }
    }

    // utility methods
    // Mostly related to mana cost calculations for continuous spells

    /**
     * Handles the logic for instant spell casting on {@code useTick}. If the current {@code useTick} matches the required
     * {@code charge}, it checks if the spell can be cast, consumes mana, sets cooldown, and casts the spell. Finally,
     * it stops the player's item use.
     *
     * @param stack   The wand item stack
     * @param spell   The spell being cast
     * @param ctx     The player cast context
     * @param useTick The current use tick
     * @param charge  The required charge time for the spell
     */
    protected void handleInstantSpellTick(PlayerCastContext ctx, Spell spell, ItemStack stack, int useTick, int charge) {
        if (useTick != charge) return;

        if (canCast(stack, spell, ctx)) {
            if (!ctx.world().isClientSide) {
                consumeManaAndSetCooldown(stack, spell, ctx.caster(), ctx.modifiers());
            }
            cast(stack, spell, ctx);
        }
        ctx.caster().stopUsingItem();
    }

    /**
     * Handles the logic for continuous spell casting on {@code useTick}. If the current {@code useTick} is greater than
     * or equal to the required {@code charge}, it checks if the spell can be cast and casts it. If the spell cannot be
     * cast, it stops the player's item use.
     *
     * @param stack   The wand item stack
     * @param spell   The spell being cast
     * @param ctx     The player cast context
     * @param useTick The current use tick
     * @param charge  The required charge time for the spell
     */
    private void handleContinuousSpellTick(PlayerCastContext ctx, Spell spell, ItemStack stack, int useTick, int charge) {
        if (useTick < charge) return;

        if (canCast(stack, spell, ctx)) {
            cast(stack, spell, ctx);
        } else {
            ctx.caster().stopUsingItem();
        }
    }

    /**
     * Starts the charging process for a spell. If the player is not already using an item, it initiates the item use,
     * sets the spell modifiers in the wizard data, and plays a charge sound if applicable.
     *
     * @param level  The current level
     * @param player The player casting the spell
     * @param hand   The hand used to cast the spell
     * @param stack  The wand item stack
     * @param charge The required charge time for the spell
     * @param ctx    The player cast context
     * @return An InteractionResultHolder indicating success or failure of starting the charge
     */
    private InteractionResultHolder<ItemStack> startCharging(Level level, Player player, InteractionHand hand, ItemStack stack, int charge, PlayerCastContext ctx) {
        if (!player.isUsingItem()) {
            player.startUsingItem(hand);
            Services.OBJECT_DATA.getWizardData(player).setSpellModifiers(ctx.modifiers());
            if (charge > 0 && level.isClientSide) {
                ClientSpellSoundManager.playChargeSound(player);
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    /**
     * Creates a PlayerCastContext for the given parameters, calculating spell modifiers based on the tick count.
     *
     * @param level  The current level
     * @param player The player casting the spell
     * @param hand   The hand used to cast the spell
     * @param tick   The current tick count of the casting process
     * @param stack  The wand item stack
     * @param spell  The spell being cast
     * @return A PlayerCastContext with the appropriate modifiers
     */
    protected PlayerCastContext createContext(Level level, Player player, InteractionHand hand, int tick, ItemStack stack, Spell spell) {
        SpellModifiers modifiers = tick == 0
                ? CastItemUtils.calculateModifiers(stack, player, spell)
                : Services.OBJECT_DATA.getWizardData(player).getSpellModifiers();
        return new PlayerCastContext(level, player, hand, tick, modifiers);
    }

    /**
     * Consumes mana from the wand and sets the cooldown for the spell.
     *
     * @param stack     The wand item stack
     * @param spell     The spell being cast
     * @param player    The player casting the spell
     * @param modifiers The spell modifiers affecting the cast
     */
    private void consumeManaAndSetCooldown(ItemStack stack, Spell spell, Player player, SpellModifiers modifiers) {
        int cost = CastItemUtils.calcCastCost(spell, modifiers);
        consumeMana(stack, cost, player);

        if (!player.isCreative()) {
            CastItemDataHelper.setCurrentCooldown(stack, CastItemUtils.calcCastCooldown(spell, modifiers), player.level().getGameTime());
        }
    }

    /**
     * Applies an upgrade from the workbench upgrade slot to the wand in the center slot.
     *
     * @param player  The player applying the upgrade (can be null)
     * @param centre  The workbench slot containing the wand
     * @param upgrade The workbench slot containing the upgrade item
     * @return true if the wand was changed, false otherwise
     */
    protected boolean applyUpgradeSlot(Player player, Slot centre, Slot upgrade) {
        ItemStack original = centre.getItem().copy();
        centre.set(applyUpgrade(player, centre.getItem(), upgrade.getItem()));
        return !ItemStack.isSameItem(centre.getItem(), original);
    }

    /**
     * Applies a tier upgrade to the wand if the tome's tier is higher than the wand's current tier. This method checks
     * if the player has enough progression to upgrade and updates the wand's tier and progression accordingly.
     *
     * @param player    The player applying the upgrade (can be null)
     * @param wand      The wand item stack to apply the upgrade to
     * @param tomeStack The arcane tome item stack being used for the upgrade
     * @return The upgraded wand item stack, or the original wand if no upgrade was applied
     */
    protected ItemStack applyTierUpgrade(@Nullable Player player, ItemStack wand, ItemStack tomeStack) {
        if (tier == SpellTiers.MASTER) return wand;
        if (!(tomeStack.getItem() instanceof ArcaneTomeItem tomeItem)) return wand;

        SpellTier nextTier = tomeItem.getTier(tomeStack);
        if (tier == nextTier) return wand;

        if (player == null || CastItemDataHelper.getProgression(wand) >= nextTier.getProgression()) {
            int newProgression = Math.max(0, CastItemDataHelper.getProgression(wand) - nextTier.getProgression());
            CastItemDataHelper.setProgression(wand, newProgression);

            if (player != null) {
                Services.OBJECT_DATA.getWizardData(player).setTierReached(tier);
            }

            ItemStack newWand = new ItemStack(RegistryUtils.getWand(nextTier, element));
            newWand.setTag(wand.getTag());
            ((IManaItem) newWand.getItem()).setMana(newWand, getMana(wand));
            tomeStack.shrink(1);
            return newWand;
        }
        return wand;
    }

    /**
     * Applies a special upgrade to the wand, such as storage or attunement upgrades. This method checks for upgrade
     * limits and applies the upgrade effects accordingly.
     *
     * @param player  The player applying the upgrade (can be null)
     * @param wand    The wand item stack to apply the upgrade to
     * @param upgrade The upgrade item stack being applied
     */
    protected void applySpecialUpgrade(@Nullable Player player, ItemStack wand, ItemStack upgrade) {
        Item specialUpgrade = upgrade.getItem();
        int maxUpgrades = tier.getUpgradeLimit() + (element == Elements.MAGIC ? EBServerConfig.NON_ELEMENTAL_UPGRADE_BONUS.get() : 0);

        if (CastItemDataHelper.getTotalUpgrades(wand) >= maxUpgrades || CastItemDataHelper.getUpgradeLevel(wand, specialUpgrade) >= EBServerConfig.UPGRADE_STACK_LIMIT.get()) {
            return;
        }

        int prevMana = getMana(wand);
        CastItemDataHelper.applyUpgrade(wand, specialUpgrade);

        if (specialUpgrade == EBItems.STORAGE_UPGRADE.get()) {
            setMana(wand, prevMana);
        } else if (specialUpgrade == EBItems.ATTUNEMENT_UPGRADE.get()) {
            expandSpellSlots(wand);
        }

        upgrade.shrink(1);
        if (player == null) return;

        EBAdvancementTriggers.SPECIAL_UPGRADE.triggerFor(player);
        if (CastItemDataHelper.getTotalUpgrades(wand) == SpellTiers.MASTER.getUpgradeLimit())
            EBAdvancementTriggers.MAX_OUT_WAND.triggerFor(player);
    }

    /**
     * Expands the spell slots on the wand when an attunement upgrade is applied. This method adjusts the spell list
     * to accommodate the new slot count.
     *
     * @param wand The wand item stack to expand spell slots for
     */
    protected void expandSpellSlots(ItemStack wand) {
        int newSlotCount = BASE_SPELL_SLOTS + CastItemDataHelper.getUpgradeLevel(wand, EBItems.ATTUNEMENT_UPGRADE.get());
        List<Spell> spells = CastItemDataHelper.getSpells(wand);

        Spell[] newSpells = new Spell[newSlotCount];
        for (int i = 0; i < newSpells.length; i++) {
            newSpells[i] = i < spells.size() && spells.get(i) != null ? spells.get(i) : Spells.NONE;
        }
        CastItemDataHelper.setSpells(wand, List.of(newSpells));
    }

    /**
     * Handles wand progression when a spell is cast. Progression is only added for non-instant spells and for wands
     * below master tier. Progression is added every second (20 ticks) during casting.
     *
     * @param ctx   The player cast context
     * @param spell The spell being cast
     * @param stack The wand item stack
     */
    protected void handleProgression(PlayerCastContext ctx, Spell spell, ItemStack stack) {
        if (tier.getLevel() >= SpellTiers.MASTER.getLevel()) return;

        // For instant spells, add progression at tick 0
        // For continuous spells, add progression every 20 ticks
        boolean shouldAddProgression = spell.isInstantCast()
                ? ctx.castingTicks() == 0
                : ctx.castingTicks() % 20 == 0;

        if (!shouldAddProgression) return;

        EBLogger.info("Recent cast: " + Services.OBJECT_DATA.getWizardData(ctx.caster()).getRecentSpells());

        int progression = Math.max(1, CastItemUtils.calcCastProgression(spell, ctx.modifiers()));
        CastItemDataHelper.addProgression(stack, progression);
        checkLevelUp(ctx, stack, progression);
    }

    /**
     * Checks if the wand has enough progression to level up and notifies the player if it has. This method is called
     * after progression is added to the wand.
     *
     * @param ctx         The player cast context
     * @param stack       The wand item stack
     * @param progression The amount of progression just added
     */
    protected void checkLevelUp(PlayerCastContext ctx, ItemStack stack, int progression) {
        Player player = ctx.caster();
        SpellTier nextTier = tier.next();
        int excess = CastItemDataHelper.getProgression(stack) - nextTier.getProgression();

        if (excess >= 0 && excess < progression) {
            player.playSound(EBSounds.ITEM_WAND_LEVELUP.get(), 1.25f, 1);
            if (!player.level().isClientSide) {
                player.sendSystemMessage(Component.translatable("item.ebwizardry.wand.levelup", getName(stack), nextTier.getDescriptionFormatted()));
            }
        }
    }

    // Simple interface implementations and getters
    // Not much logic here, just boilerplate to satisfy the interfaces

    @Override
    public void onClearButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        ItemStack stack = centre.getItem();
        if (!stack.getOrCreateTag().contains(CastItemDataHelper.SPELL_ARRAY_KEY)) return;
        List<Spell> spells = CastItemDataHelper.getSpells(stack);
        int expectedSlotCount = BASE_SPELL_SLOTS + CastItemDataHelper.getUpgradeLevel(stack, EBItems.ATTUNEMENT_UPGRADE.get());
        if (spells.size() < expectedSlotCount) spells = new ArrayList<>();

        CastItemDataHelper.setSpells(stack, spells);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return (this.element == null ? super.getName(stack) : Component.literal(super.getName(stack).getString()).withStyle(this.element.getColor()));
    }

    @Override
    public void selectNextSpell(ItemStack stack) {
        CastItemDataHelper.selectNextSpell(stack);
    }

    @Override
    public void selectPreviousSpell(ItemStack stack) {
        CastItemDataHelper.selectPreviousSpell(stack);
    }

    @Override
    public int getCustomMaxDamage(ItemStack stack) {
        return (int) (this.getMaxDamage() * (1.0f + EBServerConfig.STORAGE_INCREASE_PER_LEVEL.get() * CastItemDataHelper.getUpgradeLevel(stack, EBItems.STORAGE_UPGRADE.get())) + 0.5f);
    }

    @Override
    public void setCustomDamage(ItemStack stack, int damage) {
        stack.getOrCreateTag().putInt("Damage", Math.max(0, Math.min(damage, stack.getMaxDamage())));
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        if (tier == SpellTiers.MASTER) return false;
        return CastItemDataHelper.getProgression(stack) >= tier.next().getProgression();
    }

    @Override
    public boolean canBreak(ItemStack stack) {
        return false;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float) stack.getDamageValue());
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return MAX_USE_DURATION;
    }

    @Override
    public boolean isClearable() {
        return true;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return true;
    }

    @Override
    public boolean selectSpell(ItemStack stack, int index) {
        return CastItemDataHelper.setCurrentSpell(stack, index);
    }

    @Override
    public int getCurrentCooldown(ItemStack stack, Level level) {
        return CastItemDataHelper.getCurrentCooldown(stack, level.getGameTime());
    }

    @Override
    public int getCurrentMaxCooldown(ItemStack stack) {
        return CastItemDataHelper.getCurrentMaxCooldown(stack);
    }

    @Override
    public boolean showSpellHUD(Player player, ItemStack stack) {
        return true;
    }

    @Override
    public int getMana(ItemStack stack) {
        return getManaCapacity(stack) - stack.getDamageValue();
    }

    @Override
    public int getManaCapacity(ItemStack stack) {
        return stack.getMaxDamage();
    }

    @Override
    public void setMana(ItemStack stack, int mana) {
        stack.setDamageValue(getManaCapacity(stack) - mana);
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return BASE_SPELL_SLOTS + CastItemDataHelper.getUpgradeLevel(stack, EBItems.ATTUNEMENT_UPGRADE.get());
    }

    @NotNull
    @Override
    public Spell getCurrentSpell(ItemStack stack) {
        return CastItemDataHelper.getCurrentSpell(stack);
    }

    @Override
    public @NotNull Spell getNextSpell(ItemStack stack) {
        return CastItemDataHelper.getNextSpell(stack);
    }

    @Override
    public @NotNull Spell getPreviousSpell(ItemStack stack) {
        return CastItemDataHelper.getPreviousSpell(stack);
    }

    @Override
    public Spell[] getSpells(ItemStack stack) {
        return CastItemDataHelper.getSpells(stack).toArray(new Spell[0]);
    }

    @Override
    public Element getElement() {
        return this.element;
    }

    @Override
    public boolean validForReceptacle() {
        return false;
    }

    @Override
    public SpellTier getTier(ItemStack stack) {
        return this.tier;
    }
}
