package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.item.IWizardryItem;
import com.electroblob.wizardry.api.content.item.IWorkbenchItem;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.Tier;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.SpellSoundManager;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Tiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

public class WandItem extends Item implements ISpellCastingItem, IManaStoringItem, IWorkbenchItem, IWizardryItem {
    public static final int BASE_SPELL_SLOTS = 5;
    private static final int CONTINUOUS_TRACKING_INTERVAL = 20;
    private static final float ELEMENTAL_PROGRESSION_MODIFIER = 1.2f;
    private static final float DISCOVERY_PROGRESSION_MODIFIER = 5f;
    private static final float SECOND_TIME_PROGRESSION_MODIFIER = 1.5f;
    private static final float MAX_PROGRESSION_REDUCTION = 0.75f;
    public Tier tier;
    public Element element;

    public WandItem(Tier tier, Element element) {
        super(new Properties().stacksTo(1).durability(tier.maxCharge));
        this.tier = tier;
        this.element = element;
        //WizardryRecipes.addToManaFlaskCharging(this);
        //Wizardry.proxy.registerItemProperties(this);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.selectMinionTarget(player, level)) return InteractionResultHolder.success(stack);

        Spell spell = WandHelper.getCurrentSpell(stack);
        SpellModifiers modifiers = this.calculateModifiers(stack, player, spell);

        if (canCast(stack, spell, player, hand, 0, modifiers)) {
            int chargeup = (int) (spell.getCharge() * modifiers.get(SpellModifiers.CHARGEUP));

            if (!spell.isInstantCast() || chargeup > 0) {
                if (!player.isUsingItem()) {
                    player.startUsingItem(hand);
                    Services.WIZARD_DATA.getWizardData(player, level).itemModifiers = modifiers;
                    if (chargeup > 0 && level.isClientSide) SpellSoundManager.playChargeSound(player);
                    return InteractionResultHolder.success(stack);
                }
            } else {
                if (cast(stack, spell, player, hand, 0, modifiers)) {
                    return InteractionResultHolder.success(stack);
                }
            }
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int timeLeft) {
        if(!(livingEntity instanceof Player user)) return;

        Spell spell = WandHelper.getCurrentSpell(stack);
        SpellModifiers modifiers;
        PlayerWizardData data = Services.WIZARD_DATA.getWizardData(user, user.level());
        modifiers = data.itemModifiers;


        int useTick = stack.getUseDuration() - timeLeft;
        int chargeup = (int) (spell.getCharge() * modifiers.get(SpellModifiers.CHARGEUP));
        int castingTick = useTick - chargeup;

        if (!spell.isInstantCast()) {
            if (useTick >= chargeup) {
                if (castingTick == 0 || canCast(stack, spell, user, user.getUsedItemHand(), castingTick, modifiers)) {
                    cast(stack, spell, user, user.getUsedItemHand(), castingTick, modifiers);
                } else {
                    user.stopUsingItem();
                }
            } else {
                // Charge time
                PlayerCastContext ctx = new PlayerCastContext(level, user, user.getUsedItemHand(), castingTick, new SpellModifiers());
                spell.onCharge(ctx);
            }
        } else {
            if (chargeup > 0 && useTick == chargeup) {
                cast(stack, spell, user, user.getUsedItemHand(), 0, modifiers);
            }
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity interactionTarget, @NotNull InteractionHand usedHand) {
        if(player.isCrouching() && interactionTarget instanceof Player playerTarget){
            PlayerWizardData wizardData = Services.WIZARD_DATA.getWizardData(player, player.level());

            String string = wizardData.toggleAlly(player, playerTarget) ? "item." + WizardryMainMod.MOD_ID + ":wand.addally"
                    : "item." + WizardryMainMod.MOD_ID + ":wand.removeally";
            if(!player.level().isClientSide) player.sendSystemMessage(Component.translatable(string, playerTarget.getName()));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    private boolean selectMinionTarget(Player player, Level world) {
//        HitResult rayTrace = RayTracer.standardEntityRayTrace(world, player, 16, false);
//
//        if (rayTrace != null && rayTrace instanceof EntityHitResult && EntityUtils.isLiving(((EntityHitResult) rayTrace).getEntity())) {
//            LivingEntity entity = (LivingEntity) ((EntityHitResult) rayTrace).getEntity();
//
//            if (player.isShiftKeyDown() && WizardData.get(player) != null && WizardData.get(player).selectedMinion != null) {
//                ISummonedCreature minion = WizardData.get(player).selectedMinion.get();
//
//                if (minion instanceof Mob && minion != entity) {
//                    ((Mob) minion).setTarget(entity);
//
//                    WizardData.get(player).selectedMinion = null;
//                    return true;
//                }
//            }
//        }

        return false;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean canCast(ItemStack stack, Spell spell, Player caster, InteractionHand hand, int castingTick, SpellModifiers modifiers) {
        if (castingTick == 0) {
            if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Pre(SpellCastEvent.Source.WAND, spell, caster, modifiers)))
                return false;
        } else {
            if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(SpellCastEvent.Source.WAND, spell, caster, modifiers, castingTick)))
                return false;
        }

        int cost = (int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f);
        if (!spell.isInstantCast()) cost = getDistributedCost(cost, castingTick);

        boolean first = cost <= this.getMana(stack);
        boolean second = spell.getTier().level <= this.tier.level;
        boolean third = WandHelper.getCurrentCooldown(stack) == 0 || caster.isCreative();
        return first && second && third;
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, Player caster, InteractionHand hand, int castingTick, SpellModifiers modifiers) {
        Level world = caster.level();
        // TODO
        //if (world.isClientSide && spell.isInstantCast() && spell.requiresPacket()) return false;

        PlayerCastContext ctx = new PlayerCastContext(world, caster, hand, castingTick, modifiers);

        if (spell.cast(ctx)) {
            if (castingTick == 0)
                WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.WAND, spell, caster, modifiers));

            if (!world.isClientSide) {
                // TODO
//                if (!spell.isContinuous && spell.requiresPacket()) {
//                    WizardryPacketHandler.net.send(PacketDistributor.DIMENSION.with(() -> world.dimension()), new PacketCastSpell(caster.getId(), hand, spell, modifiers));
//                }

                int cost = (int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f);
                if (!spell.isInstantCast()) cost = getDistributedCost(cost, castingTick);
                if (cost > 0) this.consumeMana(stack, cost, caster);
            }

            caster.startUsingItem(hand);
            if (spell.isInstantCast() && !caster.isCreative()) {
                WandHelper.setCurrentCooldown(stack, (int) (spell.getCooldown() * modifiers.get(EBItems.COOLDOWN_UPGRADE.get())));
            }

            if (this.tier.level < Tiers.MASTER.level && castingTick % CONTINUOUS_TRACKING_INTERVAL == 0) {
                int progression = (int) (spell.getCost() * modifiers.get(SpellModifiers.PROGRESSION));
                WandHelper.addProgression(stack, progression);

                if (!EBConfig.legacyWandLevelling) {
                    Tier nextTier = tier.next();
                    int excess = WandHelper.getProgression(stack) - nextTier.getProgression();
                    if (excess >= 0 && excess < progression) {
                        caster.playSound(EBSounds.ITEM_WAND_LEVELUP.get(), 1.25f, 1);
                        // TODO ADVANCEMENTS
                        //WizardryAdvancementTriggers.WAND_LEVELUP.triggerFor(caster);
                        if (!world.isClientSide)
                            caster.sendSystemMessage(Component.translatable("item." + WizardryMainMod.MOD_ID + ".wand.levelup",
                                    this.getName(stack), nextTier.getNameForTranslationFormatted()));
                    }
                }

                // TODO WIZARD DATA
                //WizardData.get(caster).trackRecentSpell(spell);
            }

            return true;
        }

        return false;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity, int timeCharged) {
        if(!(livingEntity instanceof Player player)) return;
        Spell spell = WandHelper.getCurrentSpell(stack);
        SpellModifiers modifiers;
        PlayerWizardData wizardData = Services.WIZARD_DATA.getWizardData(player, player.level());
        modifiers = wizardData.itemModifiers;
        int castingTick = stack.getUseDuration() - timeCharged;
        int cost = getDistributedCost((int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f), castingTick);
        if(!spell.isInstantCast() && spell.getTier().level <= this.tier.level && cost <= this.getMana(stack)){
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Finish(SpellCastEvent.Source.WAND, spell, livingEntity, modifiers, castingTick));
            // TODO: Whats this bin????
            spell.endCast(new CastContext(livingEntity.level(), castingTick, modifiers) {
                @Override
                public LivingEntity caster() {
                    return null;
                }
            });

            if(!player.isCreative()){
                WandHelper.setCurrentCooldown(stack, (int) (spell.getCooldown() * modifiers.get(EBItems.COOLDOWN_UPGRADE.get())));
            }
        }
    }

    // ==================================
    // Spell Casting methods - Handle spells
    // This is where you can see how the item
    // saves and loads the spells
    // ==================================

    @NotNull @Override public Spell getCurrentSpell(ItemStack stack) {
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

        if (upgrade.hasItem()) {
            ItemStack original = centre.getItem().copy();
            centre.set(this.applyUpgrade(player, centre.getItem(), upgrade.getItem()));
            changed = !ItemStack.isSameItem(centre.getItem(), original);
        }

        List<Spell> spells = WandHelper.getSpells(centre.getItem());

        if (spells.size() <= 0) spells = new ArrayList<>();

        for (int i = 0; i < spells.size(); i++) {
            if(spellBooks[i].getItem() == ItemStack.EMPTY) continue;

            Spell spell = SpellUtil.getSpell(spellBooks[i].getItem());

            // todo spell.isEnabled(SpellProperties.Context.WANDS)
            if (!(spell.getTier().level > this.tier.level) && spells.get(i) != spell) {
                if (EBConfig.preventBindingSameSpellTwiceToWands && spells.stream().anyMatch(s -> s == spell)) {
                    continue;
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
            CompoundTag nbt = stack.getOrCreateTag();
            List<Spell> spells = WandHelper.getSpells(stack);
            int expectedSlotCount = BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(stack,
                    EBItems.ATTUNEMENT_UPGRADE);

            // unbrick broken wands
//            if (spells.size() < expectedSlotCount) {
//                spells = new ArrayList<>();
//            }
            nbt.put(WandHelper.SPELL_ARRAY_KEY, new ListTag());
            stack.setTag(nbt);
        }
    }

    @Override
    public boolean isClearable() {
        return true;
    }

    @Override
    public ItemStack applyUpgrade(@Nullable Player player, ItemStack wand, ItemStack upgrade) {
        if (upgrade.getItem() == EBItems.ARCANE_TOME.get()) {
//            Tier tier = Tier.values()[upgrade.getTagElement("Tiers").getInt("Tier")];
//
//            if ((player == null || player.isCreative() || Wizardry.settings.legacyWandLevelling || WandHelper.getProgression(wand) >= tier.getProgression()) && tier == this.tier.next() && this.tier != Tier.MASTER) {
//                if (Wizardry.settings.legacyWandLevelling) {
//                    WandHelper.setProgression(wand, 0);
//                } else {
//                    WandHelper.setProgression(wand, WandHelper.getProgression(wand) - tier.getProgression());
//                }
//
//                if (player != null) WizardData.get(player).setTierReached(tier);
//
//                ItemStack newWand = new ItemStack(getWand(tier, this.element));
//                newWand.setTag(wand.getTag());
//
//                ((IManaStoringItem) newWand.getItem()).setMana(newWand, this.getMana(wand));
//
//                upgrade.shrink(1);
//
//                return newWand;
//            }

        } else if (WandHelper.isWandUpgrade(new DeferredObject<>(upgrade::getItem))) {
//            Item specialUpgrade = upgrade.getItem();
//
//            int maxUpgrades = this.tier.upgradeLimit;
//            if (this.element == Element.MAGIC) maxUpgrades += Constants.NON_ELEMENTAL_UPGRADE_BONUS;
//
//            if (WandHelper.getTotalUpgrades(wand) < maxUpgrades && WandHelper.getUpgradeLevel(wand, specialUpgrade) < Constants.UPGRADE_STACK_LIMIT) {
//                int prevMana = this.getMana(wand);
//
//                WandHelper.applyUpgrade(wand, specialUpgrade);
//
//                if (specialUpgrade == WizardryItems.STORAGE_UPGRADE.get()) {
//                    this.setMana(wand, prevMana);
//                } else if (specialUpgrade == WizardryItems.ATTUNEMENT_UPGRADE.get()) {
//                    int newSlotCount = BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(wand, WizardryItems.ATTUNEMENT_UPGRADE.get());
//
//                    Spell[] spells = WandHelper.getSpells(wand);
//                    Spell[] newSpells = new Spell[newSlotCount];
//
//                    for (int i = 0; i < newSpells.length; i++) {
//                        newSpells[i] = i < spells.length && spells[i] != null ? spells[i] : Spells.NONE;
//                    }
//
//                    WandHelper.setSpells(wand, newSpells);
//
//                    int[] cooldowns = WandHelper.getCooldowns(wand);
//                    int[] newCooldowns = new int[newSlotCount];
//
//                    if (cooldowns.length > 0) {
//                        System.arraycopy(cooldowns, 0, newCooldowns, 0, cooldowns.length);
//                    }
//
//                    WandHelper.setCooldowns(wand, newCooldowns);
//                }
//
//                upgrade.shrink(1);
//
//                if (player != null) {
//                    WizardryAdvancementTriggers.SPECIAL_UPGRADE.triggerFor(player);
//
//                    if (WandHelper.getTotalUpgrades(wand) == Tier.MASTER.upgradeLimit) {
//                        WizardryAdvancementTriggers.MAX_OUT_WAND.triggerFor(player);
//                    }
//                }
        }

        return wand;
    }

    // =====================================
    // Utils
    // =====================================

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
        // TODO: quick solution, crash on forge when only use stack.setDamageValue
        if (stack.getDamageValue() < damage) {
            stack.getOrCreateTag().putInt("Damage", Math.min(damage, stack.getMaxDamage()));
        }
    }


    @Override
    public int getBarColor(ItemStack stack) {
        return DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float) stack.getDamageValue());
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

        // TODO
        //float progressionModifier = 1.0f - ((float) WizardData.get(player).countRecentCasts(spell) / WizardData.MAX_RECENT_SPELLS) * MAX_PROGRESSION_REDUCTION;
        float progressionModifier = 1.0f;
        PlayerWizardData data = Services.WIZARD_DATA.getWizardData(player, player.level());

        if (this.element == spell.getElement()) {
            modifiers.set(SpellModifiers.POTENCY, 1.0f + (this.tier.level + 1) * EBConfig.POTENCY_INCREASE_PER_TIER, true);
            progressionModifier *= ELEMENTAL_PROGRESSION_MODIFIER;
        }

        if (!data.hasSpellBeenDiscovered(spell)) {
            progressionModifier *= DISCOVERY_PROGRESSION_MODIFIER;
        }

        // TODO DATA TIER
//        if (!data.hasReachedTier(this.tier.next())) {
//            progressionModifier *= SECOND_TIME_PROGRESSION_MODIFIER;
//        }


        modifiers.set(SpellModifiers.PROGRESSION, progressionModifier, false);
        return modifiers;
    }
}
