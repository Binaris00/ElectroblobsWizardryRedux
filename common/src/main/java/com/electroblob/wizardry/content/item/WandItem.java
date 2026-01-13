package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.data.WizardData;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.*;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellContext;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.*;
import com.electroblob.wizardry.core.ClientSpellSoundManager;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.EBConstants;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
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
 * classes (e.g. {@link WandHelper} and {@link CastUtils}), so refer to those for more details on specific aspects of
 * wand functionality.
 * <p>
 * A wand is a spell-casting item that can store mana, hold multiple spells, and be upgraded in various ways. Wands
 * can cast both instant and continuous spells, with mechanics for charging, cooldowns, and mana consumption. They can be
 * upgraded using special items to enhance their capabilities, such as increasing mana capacity or adding spell slots.
 *
 * @see ISpellCastingItem
 * @see IWizardryItem
 */
public class WandItem extends Item implements ISpellCastingItem, IManaStoringItem, IWorkbenchItem, IWizardryItem, ITierValue, IElementValue {
    /* Base number of spell slots on a wand without upgrades. */
    public static final int BASE_SPELL_SLOTS = 5;
    /** Cooldown applied when a spell cast is cancelled by forfeit (or any listener from SpellPreCast/SpellTickCast) */
    public static final int COOLDOWN_FORFEIT_TICKS = 60;
    /** Maximum use duration for continuous spells. */
    public static final int MAX_USE_DURATION = 72000;

    private final SpellTier tier;
    private final Element element;

    public WandItem(SpellTier tier, Element element) {
        super(new Properties().stacksTo(1).durability(tier.maxCharge));
        this.tier = tier;
        this.element = element;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Spell spell = WandHelper.getCurrentSpell(stack);
        if (spell == Spells.NONE) return InteractionResultHolder.pass(stack);

        PlayerCastContext ctx = createContext(level, player, hand, 0, stack, spell);
        if (!canCastRequirements(stack, spell, ctx)) return InteractionResultHolder.fail(stack);
        int charge = calculateCharge(spell, ctx);

        if (!requiresCharging(spell, charge)) {
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

        Spell spell = WandHelper.getCurrentSpell(stack);
        SpellModifiers modifiers = Services.OBJECT_DATA.getWizardData(player).getSpellModifiers();
        int useTick = stack.getUseDuration() - timeLeft;
        int charge = calculateCharge(spell, modifiers);

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
        if (CastUtils.fireSpellCastEvent(SpellCastEvent.Source.WAND, spell, ctx)) {
            CastUtils.applyCooldownForfeit(ctx.caster(), COOLDOWN_FORFEIT_TICKS);
            return false;
        }
        return canCastRequirements(stack, spell, ctx);
    }

    private boolean canCastRequirements(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        int cost = (int) (spell.getCost() * ctx.modifiers().get(SpellModifiers.COST) + 0.1f);
        if (!spell.isInstantCast()) {
            cost = WandHelper.getDistributedCost(cost, ctx.castingTicks());
        }

        return cost <= this.getMana(stack)
                && spell.getTier().level <= this.tier.level
                && (WandHelper.getCurrentCooldown(stack, ctx.world().getGameTime()) == 0 || ctx.caster().isCreative());
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (ctx.world().isClientSide && spell.isInstantCast() && spell.requiresPacket()) return false;
        if (!CastUtils.executeSpellCast(SpellCastEvent.Source.WAND, spell, ctx)) return false;

        CastUtils.sendSpellCastPacket(ctx.caster(), spell, ctx);
        if (!spell.isInstantCast()) ctx.caster().startUsingItem(ctx.hand());

        handleProgression(ctx, spell, stack);
        CastUtils.trackSpellUsage(ctx.caster(), spell);
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
        Spell spell = WandHelper.getCurrentSpell(stack);
        SpellModifiers modifiers;
        WizardData wizardData = Services.OBJECT_DATA.getWizardData(player);
        modifiers = wizardData.getSpellModifiers();

        int castingTick = stack.getUseDuration() - timeCharged;
        int totalCost = (int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f);
        int accumulatedCost = getAccumulatedCost(spell, castingTick, totalCost);

        if (!spell.isInstantCast() && spell.getTier().level <= this.tier.level) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Finish(SpellCastEvent.Source.WAND, spell, livingEntity, modifiers, castingTick));
            spell.endCast(new CastContext(player.level(), player, castingTick, modifiers));

            if (!level.isClientSide) {
                this.consumeMana(stack, accumulatedCost, player);

                if (!player.isCreative()) {
                    WandHelper.setCurrentCooldown(stack, (int) (spell.getCooldown() * modifiers.get(EBItems.COOLDOWN_UPGRADE.get())), level.getGameTime());
                }
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
        if (upgrade.getItem() == EBItems.ARCANE_TOME.get()) {
            return applyTierUpgrade(player, wand, upgrade);
        } else if (WandUpgrades.isWandUpgrade(upgrade.getItem())) {
            applySpecialUpgrade(player, wand, upgrade);
        }
        return wand;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean isHeldInMainhand) {
        // Cooldowns are now handled via gametime comparison, no need to decrement every tick!
        // This significantly improves performance by avoiding constant NBT modifications.

        if (!world.isClientSide && !this.isManaFull(stack) && world.getGameTime() % EBConstants.CONDENSER_TICK_INTERVAL == 0) {
            this.rechargeMana(stack, WandHelper.getUpgradeLevel(stack, EBItems.CONDENSER_UPGRADE));
        }
    }

    @Override
    public void onClearButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        ItemStack stack = centre.getItem();
        if (!stack.getOrCreateTag().contains(WandHelper.SPELL_ARRAY_KEY)) return;
        List<Spell> spells = WandHelper.getSpells(stack);
        int expectedSlotCount = BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(stack, EBItems.ATTUNEMENT_UPGRADE);
        if (spells.size() < expectedSlotCount) spells = new ArrayList<>();

        WandHelper.setSpells(stack, spells);
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
                ? WandHelper.calculateModifiers(stack, player, spell)
                : Services.OBJECT_DATA.getWizardData(player).getSpellModifiers();
        return new PlayerCastContext(level, player, hand, tick, modifiers);
    }

    /**
     * Calculates the charge time required for a spell based on the spell's base charge and the player's spell modifiers.
     *
     * @param spell The spell being cast
     * @param ctx   The player cast context
     * @return The calculated charge time
     */
    protected int calculateCharge(Spell spell, PlayerCastContext ctx) {
        return calculateCharge(spell, ctx.modifiers());
    }

    /**
     * Calculates the charge time required for a spell based on the spell's base charge and the given spell modifiers.
     *
     * @param spell     The spell being cast
     * @param modifiers The spell modifiers affecting the cast
     * @return The calculated charge time
     */
    private int calculateCharge(Spell spell, SpellModifiers modifiers) {
        return (int) (spell.getCharge() * modifiers.get(SpellModifiers.CHARGEUP));
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
        int cost = calculateSpellCost(spell, modifiers);
        consumeMana(stack, cost, player);

        if (!player.isCreative()) {
            WandHelper.setCurrentCooldown(stack, (int) (spell.getCooldown() * modifiers.get(EBItems.COOLDOWN_UPGRADE.get())), player.level().getGameTime());
        }
    }

    /**
     * Calculates the mana cost of a spell based on its base cost and the given spell modifiers.
     *
     * @param spell     The spell being cast
     * @param modifiers The spell modifiers affecting the cast
     * @return The calculated mana cost
     */
    private int calculateSpellCost(Spell spell, SpellModifiers modifiers) {
        return (int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f);
    }

    /**
     * Determines if a spell requires charging based on whether it is instant cast and the charge time.
     *
     * @param spell  The spell being cast
     * @param charge The calculated charge time
     * @return true if the spell requires charging, false otherwise
     */
    private boolean requiresCharging(Spell spell, int charge) {
        return !spell.isInstantCast() || charge > 0;
    }

    /**
     * Calculates the accumulated mana cost for a continuous spell based on the casting ticks.
     *
     * @param spell       The spell being cast
     * @param castingTick The number of ticks the spell has been cast for
     * @param totalCost   The total mana cost of the spell
     * @return The accumulated mana cost for the given casting ticks
     */
    protected static int getAccumulatedCost(Spell spell, int castingTick, int totalCost) {
        int accumulatedCost = 0;
        if (!spell.isInstantCast() && castingTick > 0) {
            int completeCycles = castingTick / 20;  // Number of complete 20-tick cycles
            int remainingTicks = castingTick % 20;  // Ticks in the partial cycle (0-19)

            // Each complete cycle applies cost twice: at tick 0 and tick 10
            // This equals totalCost per cycle (cost/2 + cost%2 + cost/2 = cost)
            accumulatedCost = completeCycles * totalCost;

            // Only add partial cycle costs if there are remaining ticks
            // (remainingTicks == 0 means we completed exactly at a cycle boundary)
            if (remainingTicks > 0) {
                accumulatedCost += totalCost / 2 + totalCost % 2;
                if (remainingTicks >= 10) accumulatedCost += totalCost / 2;
            }
        }
        return accumulatedCost;
    }


    /**
     * Applies an upgrade from the workbench upgrade slot to the wand in the centre slot.
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

        if (player == null || WandHelper.getProgression(wand) >= nextTier.getProgression()) {
            int newProgression = Math.max(0, WandHelper.getProgression(wand) - nextTier.getProgression());
            WandHelper.setProgression(wand, newProgression);

            if (player != null) {
                Services.OBJECT_DATA.getWizardData(player).setTierReached(tier);
            }

            ItemStack newWand = new ItemStack(RegistryUtils.getWand(nextTier, element));
            newWand.setTag(wand.getTag());
            ((IManaStoringItem) newWand.getItem()).setMana(newWand, getMana(wand));
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
        int maxUpgrades = tier.upgradeLimit + (element == Elements.MAGIC ? EBConfig.NON_ELEMENTAL_UPGRADE_BONUS : 0);

        if (WandHelper.getTotalUpgrades(wand) >= maxUpgrades || WandHelper.getUpgradeLevel(wand, specialUpgrade) >= EBConfig.UPGRADE_STACK_LIMIT) {
            return;
        }

        int prevMana = getMana(wand);
        WandHelper.applyUpgrade(wand, specialUpgrade);

        if (specialUpgrade == EBItems.STORAGE_UPGRADE.get()) {
            setMana(wand, prevMana);
        } else if (specialUpgrade == EBItems.ATTUNEMENT_UPGRADE.get()) {
            expandSpellSlots(wand);
        }

        upgrade.shrink(1);
        if (player == null) return;

        EBAdvancementTriggers.SPECIAL_UPGRADE.triggerFor(player);
        if (WandHelper.getTotalUpgrades(wand) == SpellTiers.MASTER.upgradeLimit)
            EBAdvancementTriggers.MAX_OUT_WAND.triggerFor(player);
    }

    /**
     * Expands the spell slots on the wand when an attunement upgrade is applied. This method adjusts the spell list
     * to accommodate the new slot count.
     *
     * @param wand The wand item stack to expand spell slots for
     */
    protected void expandSpellSlots(ItemStack wand) {
        int newSlotCount = BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(wand, EBItems.ATTUNEMENT_UPGRADE.get());
        List<Spell> spells = WandHelper.getSpells(wand);

        Spell[] newSpells = new Spell[newSlotCount];
        for (int i = 0; i < newSpells.length; i++) {
            newSpells[i] = i < spells.size() && spells.get(i) != null ? spells.get(i) : Spells.NONE;
        }
        WandHelper.setSpells(wand, List.of(newSpells));
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
        if (tier.level >= SpellTiers.MASTER.level) return;

        // For instant spells, add progression at tick 0
        // For continuous spells, add progression every 20 ticks
        boolean shouldAddProgression = spell.isInstantCast()
            ? ctx.castingTicks() == 0
            : ctx.castingTicks() % 20 == 0;

        if (!shouldAddProgression) return;

        int progression = (int) (spell.getCost() * ctx.modifiers().get(SpellModifiers.PROGRESSION));
        WandHelper.addProgression(stack, progression);

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
        int excess = WandHelper.getProgression(stack) - nextTier.getProgression();

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
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return (this.element == null ? super.getName(stack) : Component.literal(super.getName(stack).getString()).withStyle(this.element.getColor()));
    }

    @Override
    public void selectNextSpell(ItemStack stack) {
        Spell nextSpell = WandHelper.getNextSpell(stack);
        WandHelper.setCurrentSpell(stack, nextSpell);
    }

    @Override
    public void selectPreviousSpell(ItemStack stack) {
        Spell previousSpell = WandHelper.getPreviousSpell(stack);
        WandHelper.setCurrentSpell(stack, previousSpell);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return (int) (this.getMaxDamage() * (1.0f + EBConfig.STORAGE_INCREASE_PER_LEVEL * WandHelper.getUpgradeLevel(stack, EBItems.STORAGE_UPGRADE)) + 0.5f);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (stack.getDamageValue() < damage) {
            stack.getOrCreateTag().putInt("Damage", Math.min(damage, stack.getMaxDamage()));
        }
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
        return WandHelper.selectSpell(stack, index);
    }

    @Override
    public int getCurrentCooldown(ItemStack stack, Level level) {
        return WandHelper.getCurrentCooldown(stack, level.getGameTime());
    }

    @Override
    public int getCurrentMaxCooldown(ItemStack stack) {
        return WandHelper.getCurrentMaxCooldown(stack);
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
        return BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(stack, EBItems.ATTUNEMENT_UPGRADE);
    }

    @NotNull
    @Override
    public Spell getCurrentSpell(ItemStack stack) {
        return WandHelper.getCurrentSpell(stack);
    }

    @Override
    public @NotNull Spell getNextSpell(ItemStack stack) {
        return WandHelper.getNextSpell(stack);
    }

    @Override
    public @NotNull Spell getPreviousSpell(ItemStack stack) {
        return WandHelper.getPreviousSpell(stack);
    }

    @Override
    public Spell[] getSpells(ItemStack stack) {
        return WandHelper.getSpells(stack).toArray(new Spell[0]);
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
