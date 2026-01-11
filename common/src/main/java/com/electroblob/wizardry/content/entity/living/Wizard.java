package com.electroblob.wizardry.content.entity.living;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.event.EBDiscoverSpellEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.api.content.util.RegistryUtils;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.content.entity.goal.WizardLookAtTradePlayer;
import com.electroblob.wizardry.content.entity.goal.WizardTradeGoal;
import com.electroblob.wizardry.content.item.SpellBookItem;
import com.electroblob.wizardry.content.item.WizardArmorType;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

/**
 * This Wizard class is only concerned with trading behaviour. Spell casting or other AI is handled in the
 * AbstractWizard superclass.
 */
public class Wizard extends AbstractWizard implements Npc, Merchant {
    private static final int[] XP_PER_LEVEL = new int[]{0, 10, 70, 150, 250};
    private static final int MAX_LEVEL = 5;
    private static final int MAX_TRADES = 2;

    private MerchantOffers trades;
    private @Nullable Player customer;
    private int timeUntilReset;
    private int wizardXp;
    private int wizardLevel = 1;

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

        if (this.timeUntilReset > 0) return;

        if (this.trades != null) {
            this.trades.stream().filter(MerchantOffer::isOutOfStock).forEach(MerchantOffer::resetUses);
        }

        this.timeUntilReset = -1;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (this.isAlive() && !this.isTrading() && !this.isBaby() && !player.isShiftKeyDown() && this.getTarget() != player) {
            if (!this.level().isClientSide) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), this.wizardLevel);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.trades != null) nbt.put("trades", trades.createTag());
        nbt.putInt("wizardXp", this.wizardXp);
        nbt.putInt("wizardLevel", this.wizardLevel);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("trades")) this.trades = new MerchantOffers(nbt.getCompound("trades"));
        this.wizardXp = nbt.getInt("wizardXp");
        this.wizardLevel = nbt.getInt("wizardLevel");
        if (this.wizardLevel == 0) this.wizardLevel = 1;
    }

    @Override
    public void notifyTrade(MerchantOffer merchantOffer) {
        merchantOffer.increaseUses();
        this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.playSound(EBSounds.ENTITY_WIZARD_YES.get(), this.getSoundVolume(), this.getVoicePitch());

        // Calculate XP gain
        int xpGain = merchantOffer.getXp();
        if (EBAccessoriesIntegration.isEquipped(customer, EBItems.CHARM_HAGGLER.get())) {
            xpGain *= 2;
        }

        this.wizardXp += xpGain;
        this.tryLevelUp();

        if (this.random.nextInt(5) == 0 || EBAccessoriesIntegration.isEquipped(customer, EBItems.CHARM_HAGGLER.get())) {
            this.timeUntilReset = 40;
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

        if (!level().isClientSide) {
            data.discoverSpell(spell);
            if (!this.getTradingPlayer().isCreative()) {
                EntityUtil.playSoundAtPlayer(this.getTradingPlayer(), EBSounds.MISC_DISCOVER_SPELL.get(), 1.25f, 1);
                this.getTradingPlayer().sendSystemMessage(Component.translatable("spell.discover", spell.getDescriptionFormatted()));
            }
        }
    }

    private void tryLevelUp() {
        int nextLevel = Mth.clamp(this.wizardLevel + 1, 1, MAX_LEVEL);

        if (nextLevel > this.wizardLevel && this.wizardXp >= XP_PER_LEVEL[nextLevel - 1]) {
            this.wizardLevel = nextLevel;
            this.updateTrades();

            if (!level().isClientSide) {
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
                this.level().broadcastEntityEvent(this, (byte) 14); // Particle effect
            }
        }
    }

    @Override
    public @NotNull MerchantOffers getOffers() {
        if (this.trades == null) {
            this.trades = new MerchantOffers();
            this.updateTrades();
        }

        return this.trades;
    }

    private void updateTrades() {
        if (this.trades == null) this.trades = new MerchantOffers();

        switch (this.wizardLevel) {
            case 1 -> addNoviceTrades();
            case 2 -> addApprenticeTrades();
            case 3 -> addAdvancedTrades();
            case 4, 5 -> addMasterTrades();
        }
    }

    private void addNoviceTrades() {
        List<MerchantOffer> possibleTrades = new ArrayList<>();

        // Always available: Spell book → 4 crystals
        this.trades.add(createTrade(
                new ItemStack(EBItems.SPELL_BOOK.get()),
                ItemStack.EMPTY,
                new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 4),
                12, 2, 0.05f
        ));

        // (if they have an element) 3-4 gold ingots + crystal → 1 crystal of the wizard's element
        if (this.getElement() != null && this.getElement() != Elements.MAGIC) {
            possibleTrades.add(createTrade(
                    new ItemStack(Items.GOLD_INGOT, 3 + random.nextInt(2)),
                    new ItemStack(EBItems.MAGIC_CRYSTAL.get()),
                    new ItemStack(RegistryUtils.getCrystal(this.getElement())),
                    12, 5, 0.05f
            ));
        }

        // 6-10 gold ingots + 4-5 crystals → novice wand of the wizard's element (or neutral)
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 6 + random.nextInt(5)),
                new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 4 + random.nextInt(2)),
                new ItemStack(RegistryUtils.getWand(SpellTiers.NOVICE, this.getElement())),
                3, 10, 0.2f
        ));

        // 1-4 gold ingots + 2-3 crystals → Novice spell
        possibleTrades.add(createSpellTrade(SpellTiers.NOVICE, 1 + random.nextInt(4), 2 + random.nextInt(2)));

        // 1-4 gold ingots + 2-3 crystals → Novice spell (second one)
        possibleTrades.add(createSpellTrade(SpellTiers.NOVICE, 1 + random.nextInt(4), 2 + random.nextInt(2)));

        addRandomTrades(possibleTrades);
    }

    private void addApprenticeTrades() {
        List<MerchantOffer> possibleTrades = new ArrayList<>();

        // 13-18 gold ingots + 6-10 crystals → apprentice wand
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 13 + random.nextInt(6)),
                new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 6 + random.nextInt(5)),
                new ItemStack(RegistryUtils.getWand(SpellTiers.APPRENTICE, this.getElement())),
                3, 20, 0.2f
        ));

        // 14-19 gold ingots + 4-5 paper → random wand upgrade
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 14 + random.nextInt(6)),
                new ItemStack(Items.PAPER, 4 + random.nextInt(2)),
                new ItemStack(WandUpgrades.getRandomUpgrade(this.random)),
                12, 10, 0.2f
        ));

        // 7-10 gold ingots + 6-10 crystals → apprentice spell
        possibleTrades.add(createSpellTrade(SpellTiers.APPRENTICE, 7 + random.nextInt(4), 6 + random.nextInt(5)));

        // 3-4 gold ingots + 3-4 crystals → magic silk
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 3 + random.nextInt(2)),
                new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 3 + random.nextInt(2)),
                new ItemStack(EBItems.MAGIC_SILK.get()),
                12, 10, 0.2f
        ));

        // 10-12 gold ingots + 1 book → Tome of Arcana (Novice to apprentice)
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 10 + random.nextInt(3)),
                new ItemStack(Items.BOOK),
                SpellUtil.arcaneTomeItem(SpellTiers.APPRENTICE),
                3, 30, 0.2f
        ));

        addRandomTrades(possibleTrades);
    }

    private void addAdvancedTrades() {
        List<MerchantOffer> possibleTrades = new ArrayList<>();

        // 14-19 gold ingots + 4-5 paper → random wand upgrade
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 14 + random.nextInt(6)),
                new ItemStack(Items.PAPER, 4 + random.nextInt(2)),
                new ItemStack(WandUpgrades.getRandomUpgrade(this.random)),
                12, 15, 0.2f
        ));

        // 12-16 gold ingots + 8-15 crystals → advanced spell
        possibleTrades.add(createSpellTrade(SpellTiers.ADVANCED, 12 + random.nextInt(5), 8 + random.nextInt(8)));

        // 14-16 gold ingots + 1 book → Tome of Arcana (Apprentice to advanced)
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 14 + random.nextInt(3)),
                new ItemStack(Items.BOOK),
                SpellUtil.arcaneTomeItem(SpellTiers.ADVANCED),
                3, 30, 0.2f
        ));

        // 13-16 gold ingots + 8-10 crystals → any armor of the wizard's element
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 13 + random.nextInt(4)),
                new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 8 + random.nextInt(3)),
                new ItemStack(RegistryUtils.getArmor(WizardArmorType.WIZARD, this.getElement(), this.random)),
                3, 20, 0.2f
        ));

        // 14-19 gold ingots + 1 blank scroll → scroll of identification
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 14 + random.nextInt(6)),
                new ItemStack(EBItems.BLANK_SCROLL.get()),
                new ItemStack(EBItems.IDENTIFICATION_SCROLL.get()),
                3, 30, 0.2f
        ));

        addRandomTrades(possibleTrades);
    }

    private void addMasterTrades() {
        List<MerchantOffer> possibleTrades = new ArrayList<>();

        // 14-19 gold ingots + 4-5 paper → random wand upgrade
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 14 + random.nextInt(6)),
                new ItemStack(Items.PAPER, 4 + random.nextInt(2)),
                new ItemStack(WandUpgrades.getRandomUpgrade(this.random)),
                12, 20, 0.2f
        ));

        // 20-25 gold ingots + 15-20 crystals → master spell
        possibleTrades.add(createSpellTrade(SpellTiers.MASTER, 20 + random.nextInt(6), 15 + random.nextInt(6)));

        // 20-25 gold ingots + 1 astral diamond → Tome of Arcana (advanced to master)
        possibleTrades.add(createTrade(
                new ItemStack(Items.GOLD_INGOT, 20 + random.nextInt(6)),
                new ItemStack(EBItems.ASTRAL_DIAMOND.get()),
                SpellUtil.arcaneTomeItem(SpellTiers.MASTER),
                3, 50, 0.2f
        ));

        addRandomTrades(possibleTrades);
    }

    private MerchantOffer createSpellTrade(SpellTier tier, int goldAmount, int crystalAmount) {
        List<Spell> spells = SpellUtil.getSpells((s) -> s.getTier() == tier);
        if (spells.isEmpty()) return null;

        Spell spell = spells.get(random.nextInt(spells.size()));
        ItemStack spellBook = new ItemStack(EBItems.SPELL_BOOK.get());
        SpellUtil.setSpell(spellBook, spell);

        int xp = getXpForTier(tier);

        return createTrade(
                new ItemStack(Items.GOLD_INGOT, goldAmount),
                new ItemStack(EBItems.MAGIC_CRYSTAL.get(), crystalAmount),
                spellBook,
                7, xp, 0.2f
        );
    }

    private static int getXpForTier(SpellTier tier) {
        if (tier == SpellTiers.NOVICE) return 2;
        if (tier == SpellTiers.APPRENTICE) return 10;
        if (tier == SpellTiers.ADVANCED) return 20;
        return 50; // MASTER and any other tiers
    }

    /**
     * Selects a random subset of trades from the possible trades list.
     *
     * @param possibleTrades List of all possible trades for this level
     */
    private void addRandomTrades(List<MerchantOffer> possibleTrades) {

        Collections.shuffle(possibleTrades);
        int tradesToAdd = Math.min(MAX_TRADES, possibleTrades.size());
        this.trades.addAll(possibleTrades.subList(0, tradesToAdd));
    }

    private MerchantOffer createTrade(ItemStack cost1, ItemStack cost2, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        return new MerchantOffer(cost1, cost2, result, maxUses, xp, priceMultiplier);
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
        this.trades = offers;
    }

    @Override
    public int getVillagerXp() {
        return this.wizardXp;
    }

    @Override
    public void overrideXp(int xp) {
        this.wizardXp = xp;
    }

    @Override
    public boolean showProgressBar() {
        return true;
    }

    @Override
    public boolean canRestock() {
        return true;
    }

    @Override
    public boolean isClientSide() {
        return level().isClientSide();
    }
}