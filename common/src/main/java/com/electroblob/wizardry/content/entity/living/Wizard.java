package com.electroblob.wizardry.content.entity.living;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.event.EBDiscoverSpellEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.content.item.SpellBookItem;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Wizard extends AbstractWizard implements Npc, Merchant {
    private static final int MAX_TRADES = 12;
    private static final int BASE_TRADE_USES = 7;

    private MerchantOffers trades;
    private @Nullable Player customer;
    private int timeUntilReset;
    private boolean updateRecipes;

    public Wizard(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new WizardTradeGoal(this));
        this.goalSelector.addGoal(1, new WizardLookAtTradePlayer(this));
    }

    @Override
    public void notifyTradeUpdated(@NotNull ItemStack stack) {
        if (this.level().isClientSide || this.ambientSoundTime <= -this.getAmbientSoundInterval() + 20) return;

        this.ambientSoundTime = -this.getAmbientSoundInterval();
        SoundEvent sound = stack.isEmpty() ? EBSounds.ENTITY_WIZARD_NO.get() : (WizardryMainMod.IS_THE_SEASON ? EBSounds.ENTITY_WIZARD_HOHOHO.get() : EBSounds.ENTITY_WIZARD_YES.get());
        this.playSound(sound, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.isTrading() || this.timeUntilReset < 0) return;
        --this.timeUntilReset;

        if (this.timeUntilReset > 0 || !this.updateRecipes) return;
        this.trades.stream().filter(MerchantOffer::isOutOfStock)
                .forEach((o) -> {
                    o.resetUses();
                    o.addToSpecialPriceDiff(this.random.nextInt(6) + this.random.nextInt(6) + 2);
                });
        if (this.trades.size() < MAX_TRADES) this.addRandomRecipes(1);

        this.updateRecipes = false;
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
    }


    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (this.isAlive() && !this.isTrading() && !this.isBaby() && !player.isShiftKeyDown() && this.getTarget() != player) {
            if (!this.level().isClientSide) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.trades != null) nbt.put("trades", trades.createTag());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("trades")) this.trades = new MerchantOffers(nbt.getCompound("trades"));
    }

    @Override
    public void notifyTrade(MerchantOffer merchantOffer) {
        merchantOffer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.playSound(EBSounds.ENTITY_WIZARD_YES.get(), this.getSoundVolume(), this.getVoicePitch());

        if (this.random.nextInt(5) > 0 || EBAccessoriesIntegration.isEquipped(customer, EBItems.CHARM_HAGGLER.get())) {
            this.timeUntilReset = 40;
            this.updateRecipes = true;
        }

        if (this.getTradingPlayer() == null) return;
        EBAdvancementTriggers.WIZARD_TRADE.triggerFor(this.getTradingPlayer());

        if (!(merchantOffer.getResult().getItem() instanceof SpellBookItem)) return;
        Spell spell = SpellUtil.getSpell(merchantOffer.getResult());

        if (spell.getTier() == SpellTiers.MASTER)
            EBAdvancementTriggers.BUY_MASTER_SPELL.triggerFor(this.getTradingPlayer());

        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(this.getTradingPlayer());

        if (WizardryEventBus.getInstance().fire(new EBDiscoverSpellEvent(this.getTradingPlayer(), spell, EBDiscoverSpellEvent.Source.PURCHASE)))
            return;
        data.discoverSpell(spell);
        if (!level().isClientSide && !this.getTradingPlayer().isCreative() && EBConfig.discoveryMode) {
            EntityUtil.playSoundAtPlayer(this.getTradingPlayer(), EBSounds.MISC_DISCOVER_SPELL.get(), 1.25f, 1);
            this.getTradingPlayer().sendSystemMessage(Component.translatable("spell.discover", spell.getDescriptionFormatted()));
        }
    }

    @Override
    public @NotNull MerchantOffers getOffers() {
        if (this.trades == null) {
            this.trades = new MerchantOffers();
            ItemStack book = new ItemStack(EBItems.SPELL_BOOK.get(), 1);
            SpellUtil.setSpell(book, Spells.MAGIC_MISSILE);
            ItemStack crystal = new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 5);

            this.trades.add(new MerchantOffer(book, crystal, 7, 0, 0));
            this.addRandomRecipes(5);
        }

        return this.trades;
    }

    /**
     * Adds a specified number of random trade recipes to the wizard's existing trades.
     *
     * @param count The number of random recipes to add.
     */
    private void addRandomRecipes(int count) {
        if (this.trades == null) return;

        MerchantOffers offer = new MerchantOffers();

        for (int i = 0; i < count; i++) {
            SpellTier tier = selectRandomTier();
            ItemStack itemToSell = findUniqueItemOfTier(tier, offer);

            if (itemToSell.isEmpty()) continue;

            ItemStack secondItemToBuy = tier == SpellTiers.MASTER
                    ? new ItemStack(EBItems.ASTRAL_DIAMOND.get())
                    : new ItemStack(EBItems.MAGIC_CRYSTAL.get(), (tier.level + 1) * 3 + 1 + random.nextInt(4));

            offer.add(new MerchantOffer(getRandomPrice(tier), secondItemToBuy, itemToSell, BASE_TRADE_USES, 0, 0));
        }

        Collections.shuffle(offer);
        this.trades.addAll(offer);
    }

    /**
     * Finds a unique item of the specified tier that is not already present in either the existing trades or the new offers
     * being generated.
     *
     * @param tier      The spell tier for which to find a unique item.
     * @param newOffers The new offers being generated.
     * @return A unique ItemStack of the specified tier.
     */
    private ItemStack findUniqueItemOfTier(SpellTier tier, MerchantOffers newOffers) {
        ItemStack itemToSell;
        boolean isUnique;
        int attempts = 0;
        int maxAttempts = 100;

        do {
            itemToSell = getRandomItemOfTier(tier);
            isUnique = isItemUnique(itemToSell, newOffers);
            attempts++;

            if (attempts >= maxAttempts) {
                return ItemStack.EMPTY;
            }
        } while (!isUnique);

        return itemToSell;
    }


    /**
     * Checks whether the given item is already present in either the existing trades or the new offers being generated.
     *
     * @param item      The item to check for uniqueness.
     * @param newOffers The new offers being generated.
     * @return true if the item is unique, false otherwise.
     */
    private boolean isItemUnique(ItemStack item, MerchantOffers newOffers) {
        return newOffers.stream().noneMatch(offer -> ItemStack.isSameItem(offer.getResult(), item))
                && this.trades.stream().noneMatch(offer -> ItemStack.isSameItem(offer.getResult(), item));
    }

    private SpellTier selectRandomTier() {
        double tierIncreaseChance = 0.5 + 0.04 * Math.max(this.trades.size() - 4, 0);

        if (random.nextDouble() >= tierIncreaseChance) return SpellTiers.NOVICE;
        if (random.nextDouble() >= tierIncreaseChance) return SpellTiers.APPRENTICE;
        if (random.nextDouble() >= tierIncreaseChance * 0.6) return SpellTiers.ADVANCED;

        return SpellTiers.MASTER;
    }

    /**
     * Generates a random price for a trade based on the specified spell tier.
     *
     * @param tier The spell tier for which to generate a price.
     * @return An ItemStack representing the price for the trade.
     */
    @SuppressWarnings("unchecked")
    private ItemStack getRandomPrice(SpellTier tier) {
        Map<Pair<ResourceLocation, Short>, Integer> map = EBConfig.currencyItems;
        map.put(Pair.of(BuiltInRegistries.ITEM.getKey(Items.GOLD_INGOT), (short) 0), 3);
        map.put(Pair.of(BuiltInRegistries.ITEM.getKey(Items.EMERALD), (short) 0), 6);
        Pair<ResourceLocation, Short> itemName = map.keySet().toArray(new Pair[0])[random.nextInt(map.size())];
        Item item = BuiltInRegistries.ITEM.get(itemName.getLeft());

        int value = map.get(itemName);
        return new ItemStack(item, Mth.clamp((8 + (tier.level + 1) * 16 + random.nextInt(9)) / value, 1, 64));
    }

    private ItemStack getRandomItemOfTier(SpellTier tier) {
        List<Spell> spells = SpellUtil.getSpells((s) -> true);
        List<Spell> specialismSpells = SpellUtil.getSpells((s) -> s.getElement() == this.getElement());

        return tier.getTradeItem(this.getElement(), random, (ArrayList<Spell>) spells, (ArrayList<Spell>) specialismSpells);
    }

    @Override
    public @NotNull Component getDisplayName() {
        if (this.hasCustomName()) return super.getDisplayName();
        return this.getElement().getWizardName();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (WizardryMainMod.IS_THE_SEASON) return EBSounds.ENTITY_WIZARD_HOHOHO.get();
        return this.isTrading() ? EBSounds.ENTITY_WIZARD_TRADING.get() : EBSounds.ENTITY_WIZARD_AMBIENT.get();
    }


    public boolean isTrading() {
        return this.getTradingPlayer() != null;
    }

    @Override
    public Player getTradingPlayer() {
        return this.customer;
    }

    @Override
    public void setTradingPlayer(Player player) {
        this.customer = player;
    }

    @Override
    public @NotNull SoundEvent getNotifyTradeSound() {
        return EBSounds.ENTITY_WIZARD_YES.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return EBSounds.ENTITY_WIZARD_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return EBSounds.ENTITY_WIZARD_DEATH.get();
    }

    @Override
    public void overrideOffers(@NotNull MerchantOffers offers) {
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int xp) {
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public boolean isClientSide() {
        return level().isClientSide();
    }

    /** Makes the wizard look at the player it is trading with. */
    static class WizardLookAtTradePlayer extends LookAtPlayerGoal {
        private final Wizard wizard;

        public WizardLookAtTradePlayer(Wizard wizard) {
            super(wizard, Player.class, 8.0F);
            this.wizard = wizard;
        }

        @Override
        public boolean canUse() {
            if (this.wizard.isTrading()) {
                this.lookAt = this.wizard.getTradingPlayer();
                return true;
            }

            return false;
        }
    }

    /** Stops the wizard from moving when trading. */
    static class WizardTradeGoal extends Goal {
        private final Wizard wizard;

        public WizardTradeGoal(Wizard wizard) {
            this.wizard = wizard;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.wizard.isAlive()) return false;
            else if (this.wizard.isInWater()) return false;
            else if (!this.wizard.onGround()) return false;
            else if (this.wizard.hurtMarked) return false;
            Player player = this.wizard.getTradingPlayer();

            if (player == null) return false;
            else return !(this.wizard.distanceToSqr(player) > 16.0D);

        }

        @Override
        public void start() {
            this.wizard.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.wizard.setTradingPlayer(null);
        }
    }
}
