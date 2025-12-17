package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.data.WizardData;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.item.IWizardryItem;
import com.electroblob.wizardry.api.content.item.IWorkbenchItem;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellContext;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.core.ClientSpellSoundManager;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.networking.s2c.SpellCastS2C;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
 * Where the magic (normally) happens!! Most of the functions and logic for this item was moved to {@link WandHelper}
 * <ul>
 *     <li>{@link Item#use(Level, Player, InteractionHand)} check when to use continuous and instant spells</li>
 *     <li>{@link Item#interactLivingEntity(ItemStack, Player, LivingEntity, InteractionHand)} remove and add allies</li>
 *     <li>{@link Item#onUseTick(Level, LivingEntity, ItemStack, int)} handle charge and continuous effects</li>
 *     <li>{@link Item#releaseUsing(ItemStack, Level, LivingEntity, int)} handle cooldown and casting finish</li>
 * </ul>
 *
 * @see ISpellCastingItem
 * @see IWizardryItem
 */
public class WandItem extends Item implements ISpellCastingItem, IManaStoringItem, IWorkbenchItem, IWizardryItem {
    public static final int BASE_SPELL_SLOTS = 5;
    public SpellTier tier;
    public Element element;

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

        PlayerCastContext ctx = new PlayerCastContext(level, player, player.getUsedItemHand(), 0, this.calculateModifiers(stack, player, spell));
        if (!canCast(stack, spell, ctx)) return InteractionResultHolder.fail(stack);

        int charge = (int) (spell.getCharge() * ctx.modifiers().get(SpellModifiers.CHARGEUP));
        if (!spell.isInstantCast() || charge > 0) {
            if (!player.isUsingItem()) {
                player.startUsingItem(hand);
                Services.OBJECT_DATA.getWizardData(player).setSpellModifiers(ctx.modifiers());
                if (charge > 0 && level.isClientSide) ClientSpellSoundManager.playChargeSound(player);
                return InteractionResultHolder.success(stack);
            }
        } else {
            if (cast(stack, spell, ctx)) return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity user, @NotNull ItemStack stack, int timeLeft) {
        if (!(user instanceof Player player)) return;

        Spell spell = WandHelper.getCurrentSpell(stack);
        SpellModifiers modifiers;
        modifiers = Services.OBJECT_DATA.getWizardData(player).getSpellModifiers();

        int useTick = stack.getUseDuration() - timeLeft;
        int charge = (int) (spell.getCharge() * modifiers.get(SpellModifiers.CHARGEUP));
        PlayerCastContext ctx = new PlayerCastContext(level, player, user.getUsedItemHand(), useTick, modifiers);

        if (spell.isInstantCast()) {
            if (charge > 0 && useTick == charge) cast(stack, spell, ctx);
            return;
        }

        if (useTick >= charge) {
            int castingTick = useTick - charge;
            if (castingTick == 0 || canCast(stack, spell, ctx)) {
                cast(stack, spell, ctx);
            } else {
                player.stopUsingItem();
            }
        }
    }

    @Override
    public boolean canCast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (ctx.castingTicks() == 0) {
            if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.WAND, spell, ctx.caster(), ctx.modifiers()))) {
                // We want to add a short cooldown if the spell is cancelled at the start
                ctx.caster().getCooldowns().addCooldown(this, 40);
                return false;
            }
        } else {
            if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(SpellCastEvent.Source.WAND, spell, ctx.caster(), ctx.modifiers(), ctx.castingTicks()))) {
                // We want to add a short cooldown if the spell is cancelled at the start
                ctx.caster().getCooldowns().addCooldown(this, 40);
                return false;
            }
        }

        int cost = (int) (spell.getCost() * ctx.modifiers().get(SpellModifiers.COST) + 0.1f);
        if (!spell.isInstantCast()) cost = getDistributedCost(cost, ctx.castingTicks());

        return cost <= this.getMana(stack) && spell.getTier().level <= this.tier.level && (WandHelper.getCurrentCooldown(stack) == 0 || ctx.caster().isCreative());
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, PlayerCastContext ctx) {
        if (ctx.world().isClientSide && spell.isInstantCast() && spell.requiresPacket()) return false;
        if (!spell.cast(ctx)) return false;

        if (ctx.castingTicks() == 0)
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.WAND, spell, ctx.caster(), ctx.modifiers()));

        if (!ctx.world().isClientSide) {
            if (spell.isInstantCast() && spell.requiresPacket()) {
                SpellCastS2C msg = new SpellCastS2C(ctx.caster().getId(), ctx.hand(), spell, ctx.modifiers());
                Services.NETWORK_HELPER.sendToDimension(ctx.world().getServer(), msg, ctx.world().dimension());
            }

            int cost = (int) (spell.getCost() * ctx.modifiers().get(SpellModifiers.COST) + 0.1f);
            if (!spell.isInstantCast()) cost = getDistributedCost(cost, ctx.castingTicks());
            if (cost > 0) this.consumeMana(stack, cost, ctx.caster());
        }

        ctx.caster().startUsingItem(ctx.hand());
        if (spell.isInstantCast() && !ctx.caster().isCreative())
            WandHelper.setCurrentCooldown(stack, (int) (spell.getCooldown() * ctx.modifiers().get(EBItems.COOLDOWN_UPGRADE.get())));
        if (!(this.tier.level < SpellTiers.MASTER.level && ctx.castingTicks() % 20 == 0)) return false;

        int progression = (int) (spell.getCost() * ctx.modifiers().get(SpellModifiers.PROGRESSION));
        WandHelper.addProgression(stack, progression);
        SpellTier nextTier = tier.next();
        int excess = WandHelper.getProgression(stack) - nextTier.getProgression();

        if (excess >= 0 && excess < progression) {
            ctx.caster().playSound(EBSounds.ITEM_WAND_LEVELUP.get(), 1.25f, 1);
            if (!ctx.world().isClientSide)
                ctx.caster().sendSystemMessage(Component.translatable("item.ebwizardry.wand.levelup", this.getName(stack), nextTier.getDescriptionFormatted()));
        }

        Services.OBJECT_DATA.getWizardData(ctx.caster()).trackRecentSpell(spell, ctx.caster().level().getGameTime());
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

    // ==================================
    // Spell Casting methods - Handle spells
    // This is where you can see how the item
    // saves and loads the spells
    // ==================================

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
        if (!(livingEntity instanceof Player player)) return;
        Spell spell = WandHelper.getCurrentSpell(stack);
        SpellModifiers modifiers;
        WizardData wizardData = Services.OBJECT_DATA.getWizardData(player);
        modifiers = wizardData.getSpellModifiers();

        int castingTick = stack.getUseDuration() - timeCharged;
        int cost = getDistributedCost((int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f), castingTick);

        if (!spell.isInstantCast() && spell.getTier().level <= this.tier.level && cost <= this.getMana(stack)) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Finish(SpellCastEvent.Source.WAND, spell, livingEntity, modifiers, castingTick));
            // TODO: Whats this bin????
            spell.endCast(new CastContext(livingEntity.level(), castingTick, modifiers) {
                @Override
                public LivingEntity caster() {
                    return null;
                }
            });

            if (!player.isCreative())
                WandHelper.setCurrentCooldown(stack, (int) (spell.getCooldown() * modifiers.get(EBItems.COOLDOWN_UPGRADE.get())));
        }
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
    public boolean selectSpell(ItemStack stack, int index) {
        return WandHelper.selectSpell(stack, index);
    }

    @Override
    public int getCurrentCooldown(ItemStack stack) {
        return WandHelper.getCurrentCooldown(stack);
    }

    @Override
    public int getCurrentMaxCooldown(ItemStack stack) {
        return WandHelper.getCurrentMaxCooldown(stack);
    }

    @Override
    public boolean showSpellHUD(Player player, ItemStack stack) {
        return true;
    }

    // ==================================
    // Mana Storing Item Methods
    // How the wand stores mana and recharges
    // ==================================
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

    // ==================================
    // Workbench Item Methods
    // This is how you modify how the wands
    // are used on the workbench
    // ==================================
    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(stack, EBItems.ATTUNEMENT_UPGRADE);
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        boolean changed = false;
        // Apply upgrade
        if (upgrade.hasItem()) {
            ItemStack original = centre.getItem().copy();
            centre.set(this.applyUpgrade(player, centre.getItem(), upgrade.getItem()));
            changed = !ItemStack.isSameItem(centre.getItem(), original);
        }

        List<Spell> spells = WandHelper.getSpells(centre.getItem());

        if (spells.size() <= 0) spells = new ArrayList<>();

        for (int i = 0; i < spells.size(); i++) {
            if (spellBooks[i].getItem() == ItemStack.EMPTY) continue;

            Spell spell = SpellUtil.getSpell(spellBooks[i].getItem());

            if (!(spell.getTier().level > this.tier.level) && spells.get(i) != spell && spell.isEnabled(SpellContext.WANDS)) {
                if (EBConfig.preventBindingSameSpellTwiceToWands && spells.stream().anyMatch(s -> s == spell)) {
                    continue;
                }


                // Fix to ""sync"" the selected spell with the rest of the spells
                int currentSelectedIndex = spells.indexOf(WandHelper.getCurrentSpell(centre.getItem()));
                if (currentSelectedIndex == i) {
                    WandHelper.setCurrentSpell(centre.getItem(), spell);
                }

                spells.set(i, spell);
                changed = true;

                if (EBConfig.singleUseSpellBooks) {
                    spellBooks[i].getItem().shrink(1);
                }
            }
        }

        WandHelper.setSpells(centre.getItem(), spells);

        if (crystals.getItem() != ItemStack.EMPTY && !this.isManaFull(centre.getItem())) {
            int chargeDepleted = this.getManaCapacity(centre.getItem()) - this.getMana(centre.getItem());

            int manaPerItem = crystals.getItem().getItem() instanceof IManaStoringItem ? ((IManaStoringItem) crystals.getItem().getItem()).getMana(crystals.getItem()) : crystals.getItem().getItem() instanceof CrystalItem ? EBConfig.MANA_PER_CRYSTAL : EBConfig.MANA_PER_SHARD;

            if (crystals.getItem().getItem() == EBItems.MAGIC_CRYSTAL_SHARD.get())
                manaPerItem = EBConfig.MANA_PER_SHARD;
            if (crystals.getItem().getItem() == EBItems.MAGIC_CRYSTAL_GRAND.get())
                manaPerItem = EBConfig.GRAND_CRYSTAL_MANA;

            if (crystals.getItem().getCount() * manaPerItem < chargeDepleted) {
                this.rechargeMana(centre.getItem(), crystals.getItem().getCount() * manaPerItem);
                crystals.remove(crystals.getItem().getCount());
            } else {
                this.setMana(centre.getItem(), this.getManaCapacity(centre.getItem()));
                crystals.remove((int) Math.ceil(((double) chargeDepleted) / manaPerItem));
            }

            changed = true;
        }

        return changed;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return true;
    }

    @Override
    public void onClearButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        ItemStack stack = centre.getItem();
        if (stack.getOrCreateTag().contains(WandHelper.SPELL_ARRAY_KEY)) {
            List<Spell> spells = WandHelper.getSpells(stack);
            int expectedSlotCount = BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(stack, EBItems.ATTUNEMENT_UPGRADE);
            if (spells.size() < expectedSlotCount) spells = new ArrayList<>();

            WandHelper.setSpells(stack, spells);
        }
    }

    @Override
    public boolean isClearable() {
        return true;
    }

    @Override
    public ItemStack applyUpgrade(@Nullable Player player, ItemStack wand, ItemStack upgrade) {
        if (upgrade.getItem() == EBItems.ARCANE_TOME.get()) {
            String tierKey = upgrade.getOrCreateTag().getString("Tier");
            SpellTier nextTier = Services.REGISTRY_UTIL.getTier(ResourceLocation.tryParse(tierKey));

            if (nextTier == null || this.tier == SpellTiers.MASTER) return wand;
            if (this.tier == nextTier) return wand; // Don't do anything if the tome tier is equals with the wand tier

            if (player == null || player.isCreative() || EBConfig.legacyWandLevelling || WandHelper.getProgression(wand) >= nextTier.getProgression()) {
                int newProgression = EBConfig.legacyWandLevelling ? 0 : Math.max(0, WandHelper.getProgression(wand) - nextTier.getProgression());
                WandHelper.setProgression(wand, newProgression);

                if (player != null) Services.OBJECT_DATA.getWizardData(player).setTierReached(tier);

                ItemStack newWand = new ItemStack(getWand(nextTier, this.element));
                newWand.setTag(wand.getTag());
                ((IManaStoringItem) newWand.getItem()).setMana(newWand, this.getMana(wand));
                upgrade.shrink(1);

                return newWand;
            }
        } else if (WandUpgrades.isWandUpgrade(upgrade.getItem())) {
            Item specialUpgrade = upgrade.getItem();

            int maxUpgrades = this.tier.upgradeLimit;
            if (this.element == Elements.MAGIC) maxUpgrades += EBConfig.NON_ELEMENTAL_UPGRADE_BONUS;

            if (WandHelper.getTotalUpgrades(wand) < maxUpgrades && WandHelper.getUpgradeLevel(wand, specialUpgrade) < EBConfig.UPGRADE_STACK_LIMIT) {
                int prevMana = this.getMana(wand);

                WandHelper.applyUpgrade(wand, specialUpgrade);

                if (specialUpgrade == EBItems.STORAGE_UPGRADE.get()) {
                    this.setMana(wand, prevMana);
                } else if (specialUpgrade == EBItems.ATTUNEMENT_UPGRADE.get()) {
                    int newSlotCount = BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(wand, EBItems.ATTUNEMENT_UPGRADE.get());

                    List<Spell> spells = WandHelper.getSpells(wand);
                    Spell[] newSpells = new Spell[newSlotCount];

                    for (int i = 0; i < newSpells.length; i++) {
                        newSpells[i] = i < spells.size() && spells.get(i) != null ? spells.get(i) : Spells.NONE;
                    }

                    WandHelper.setSpells(wand, List.of(newSpells));

                    int[] cooldowns = WandHelper.getCooldowns(wand);
                    int[] newCooldowns = new int[newSlotCount];

                    if (cooldowns.length > 0) {
                        System.arraycopy(cooldowns, 0, newCooldowns, 0, cooldowns.length);
                    }

                    WandHelper.setCooldowns(wand, newCooldowns);
                }

                upgrade.shrink(1);

                if (player != null) {
                    //WizardryAdvancementTriggers.SPECIAL_UPGRADE.triggerFor(player);

                    if (WandHelper.getTotalUpgrades(wand) == SpellTiers.MASTER.upgradeLimit) {
                        //WizardryAdvancementTriggers.MAX_OUT_WAND.triggerFor(player);
                    }
                }
            }
        }

        return wand;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return (this.element == null ? super.getName(stack) : Component.literal(super.getName(stack).getString()).withStyle(this.element.getColor()));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean isHeldInMainhand) {
        boolean isHeld = isHeldInMainhand || entity instanceof LivingEntity && ItemStack.isSameItem(stack, ((LivingEntity) entity).getOffhandItem());

        if (!EBConfig.wandsMustBeHeldToDecrementCooldown || isHeld) {
            WandHelper.decrementCooldowns(stack);
        }

        if (!world.isClientSide && !this.isManaFull(stack) && world.getGameTime() % EBConfig.CONDENSER_TICK_INTERVAL == 0) {
            this.rechargeMana(stack, WandHelper.getUpgradeLevel(stack, EBItems.CONDENSER_UPGRADE));
        }
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

    public SpellModifiers calculateModifiers(ItemStack stack, Player player, Spell spell) {
        SpellModifiers modifiers = new SpellModifiers();

        int level = WandHelper.getUpgradeLevel(stack, EBItems.RANGE_UPGRADE);
        if (level > 0)
            modifiers.set(EBItems.RANGE_UPGRADE.get(), 1.0f + level * EBConfig.RANGE_INCREASE_PER_LEVEL, true);

        level = WandHelper.getUpgradeLevel(stack, EBItems.DURATION_UPGRADE);
        if (level > 0)
            modifiers.set(EBItems.DURATION_UPGRADE.get(), 1.0f + level * EBConfig.DURATION_INCREASE_PER_LEVEL, false);

        level = WandHelper.getUpgradeLevel(stack, EBItems.BLAST_UPGRADE);
        if (level > 0)
            modifiers.set(EBItems.BLAST_UPGRADE.get(), 1.0f + level * EBConfig.BLAST_RADIUS_INCREASE_PER_LEVEL, true);

        level = WandHelper.getUpgradeLevel(stack, EBItems.COOLDOWN_UPGRADE);
        if (level > 0)
            modifiers.set(EBItems.COOLDOWN_UPGRADE.get(), 1.0f - level * EBConfig.COOLDOWN_REDUCTION_PER_LEVEL, true);

        float progressionModifier = 1.0F - ((float) Services.OBJECT_DATA.getWizardData(player).countRecentCasts(spell) / EBConfig.MAX_RECENT_SPELLS) * EBConfig.MAX_PROGRESSION_REDUCTION;
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);

        if (this.element == spell.getElement()) {
            modifiers.set(SpellModifiers.POTENCY, 1.0f + (this.tier.level + 1) * EBConfig.POTENCY_INCREASE_PER_TIER, true);
            progressionModifier *= 1.2f;
        }

        if (!data.hasSpellBeenDiscovered(spell)) {
            progressionModifier *= 5f;
        }

        // TODO DATA TIER
//        if (!data.hasReachedTier(this.tier.next())) {
//            progressionModifier *= SECOND_TIME_PROGRESSION_MODIFIER;
//        }


        modifiers.set(SpellModifiers.PROGRESSION, progressionModifier, false);
        return modifiers;
    }

    // =====================================
    // Utils
    // =====================================
    public static Item getWand(SpellTier tier, Element element) {
        if (tier == null) throw new NullPointerException("The given tier cannot be null.");
        if (element == null) element = Elements.MAGIC;
        String registryName = tier == SpellTiers.NOVICE && element == Elements.MAGIC ? "novice" : tier.getOrCreateLocation().getPath();
        if (element != Elements.MAGIC) registryName = registryName + "_" + element.getLocation().getPath();
        registryName = "wand_" + registryName;
        return BuiltInRegistries.ITEM.get(new ResourceLocation(element.getLocation().getNamespace(), registryName));
    }

    protected static int getDistributedCost(int cost, int castingTick) {
        int partialCost;

        if (castingTick % 20 == 0) {
            partialCost = cost / 2 + cost % 2;
        } else if (castingTick % 10 == 0) {
            partialCost = cost / 2;
        } else {
            partialCost = 0;
        }

        return partialCost;
    }
}
