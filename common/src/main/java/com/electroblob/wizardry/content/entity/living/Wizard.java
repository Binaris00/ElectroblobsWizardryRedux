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
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.SpellTiers;
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
    public @NotNull Component getDisplayName() {
        if (this.hasCustomName()) return super.getDisplayName();
        return this.getElement().getWizardName();
    }

    @Override
    public void notifyTradeUpdated(@NotNull ItemStack stack) {
        if (!this.level().isClientSide && this.ambientSoundTime > -this.getAmbientSoundInterval() + 20) {
            this.ambientSoundTime = -this.getAmbientSoundInterval();
            SoundEvent yes = WizardryMainMod.IS_THE_SEASON ? EBSounds.ENTITY_WIZARD_HOHOHO.get() : EBSounds.ENTITY_WIZARD_YES.get();
            this.playSound(stack.isEmpty() ? EBSounds.ENTITY_WIZARD_NO.get() : yes, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.isTrading() && this.timeUntilReset < 0) return;
        --this.timeUntilReset;

        if (this.timeUntilReset <= 0) {
            if (!this.updateRecipes) return;

            for (MerchantOffer offer : this.trades) {
                if (offer.isOutOfStock())
                    offer.addToSpecialPriceDiff(this.random.nextInt(6) + this.random.nextInt(6) + 2);
            }

            if (this.trades.size() < 12) this.addRandomRecipes(1);
            this.updateRecipes = false;

            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
        }
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isCreative() && stack.getItem() instanceof SpellBookItem) {
            Spell spell = SpellUtil.getSpell(stack);
            if (this.spells.size() >= 4 && spell.canCastByEntity()) {
                player.sendSystemMessage(Component.translatable("item." + WizardryMainMod.MOD_ID + ".spell_book.apply_to_wizard",
                        this.getDisplayName(), spells.set(random.nextInt(3) + 1, spell).getDescriptionFormatted(),
                        spell.getDescriptionFormatted()));
                return InteractionResult.SUCCESS;
            }
        }

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

        if (this.getTradingPlayer() != null) {
            //WizardryAdvancementTriggers.WIZARD_TRADE.triggerFor(this.getTradingPlayer());

            if (merchantOffer.getResult().getItem() instanceof SpellBookItem) {
                Spell spell = SpellUtil.getSpell(merchantOffer.getResult());

//                if (spell.getTier() == Tier.MASTER)
//                    WizardryAdvancementTriggers.BUY_MASTER_SPELL.triggerFor(this.getTradingPlayer());

                SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(this.getTradingPlayer());
                // Todo if canceled discovery event
                WizardryEventBus.getInstance().fire(new EBDiscoverSpellEvent(this.getTradingPlayer(), spell, EBDiscoverSpellEvent.Source.PURCHASE));
                data.discoverSpell(spell);

                if (!level().isClientSide && !this.getTradingPlayer().isCreative() && EBConfig.discoveryMode) {
                    EntityUtil.playSoundAtPlayer(this.getTradingPlayer(), EBSounds.MISC_DISCOVER_SPELL.get(), 1.25f, 1);
                    this.getTradingPlayer().sendSystemMessage(Component.translatable("spell.discover", spell.getDescriptionFormatted()));
                }
            }
        }

        if (this.random.nextInt(5) > 0 || EBAccessoriesIntegration.isEquipped(customer, EBItems.CHARM_HAGGLER.get())) {
            this.timeUntilReset = 40;
            this.updateRecipes = true;

            if (this.getTradingPlayer() != null) {
                this.getTradingPlayer().getName();
            }
        }
    }

    @Override
    public @NotNull MerchantOffers getOffers() {
        if (this.trades == null) {
            this.trades = new MerchantOffers();
            ItemStack anySpellBook = new ItemStack(EBItems.SPELL_BOOK.get(), 1);
            ItemStack crystalStack = new ItemStack(EBItems.MAGIC_CRYSTAL.get(), 5);

            this.trades.add(new MerchantOffer(anySpellBook, crystalStack, 7, 0, 0));
            this.addRandomRecipes(3);
        }

        return this.trades;
    }

    private void addRandomRecipes(int numberOfItemsToAdd) {
        MerchantOffers offers = new MerchantOffers();
        if (this.trades == null) return;

        for (int i = 0; i < numberOfItemsToAdd; i++) {
            ItemStack itemToSell = ItemStack.EMPTY;
            boolean itemAlreadySold = true;
            SpellTier tier = SpellTiers.NOVICE;

            while (itemAlreadySold) {
                itemAlreadySold = false;
                double tierIncreaseChance = 0.5 + 0.04 * (Math.max(this.trades.size() - 4, 0));

                tier = SpellTiers.NOVICE;

                if (random.nextDouble() < tierIncreaseChance) {
                    tier = SpellTiers.APPRENTICE;
                    if (random.nextDouble() < tierIncreaseChance) {
                        tier = SpellTiers.ADVANCED;
                        if (random.nextDouble() < tierIncreaseChance * 0.6) {
                            tier = SpellTiers.MASTER;
                        }
                    }
                }

                itemToSell = this.getRandomItemOfTier(tier);

                for (Object recipe : offers) {
                    if (ItemStack.isSameItem(((MerchantOffer) recipe).getResult(), itemToSell))
                        itemAlreadySold = true;
                }

                if (this.trades != null) {
                    for (Object recipe : this.trades) {
                        if (ItemStack.isSameItem(((MerchantOffer) recipe).getResult(), itemToSell))
                            itemAlreadySold = true;
                    }
                }
            }

            if (itemToSell.isEmpty()) return;

            ItemStack secondItemToBuy = tier == SpellTiers.MASTER ? new ItemStack(EBItems.ASTRAL_DIAMOND.get()) : new ItemStack(EBItems.MAGIC_CRYSTAL.get(), (tier.level + 1) * 3 + 1 + random.nextInt(4));

            offers.add(new MerchantOffer(this.getRandomPrice(tier), secondItemToBuy, itemToSell, 7, 0, 0));
        }

        Collections.shuffle(offers);

        if (this.trades == null) {
            this.trades = new MerchantOffers();
        }

        this.trades.addAll(offers);
    }

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

    /**
     * Creates a spell book ItemStack containing the given spell.
     *
     * @param spell The spell to put in the book.
     * @return The spell book ItemStack.
     */
    private static ItemStack spellBook(Spell spell) {
        ItemStack stack = new ItemStack(EBItems.SPELL_BOOK.get(), 1);
        SpellUtil.setSpell(stack, spell);
        return stack;
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
            } else {
                return false;
            }
        }
    }

    static class WizardTradeGoal extends Goal {
        private final Wizard wizard;

        public WizardTradeGoal(Wizard wizard) {
            this.wizard = wizard;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.wizard.isAlive()) {
                return false;
            } else if (this.wizard.isInWater()) {
                return false;
            } else if (!this.wizard.onGround()) {
                return false;
            } else if (this.wizard.hurtMarked) {
                return false;
            } else {
                Player entityplayer = this.wizard.getTradingPlayer();

                if (entityplayer == null) {
                    return false;
                } else return !(this.wizard.distanceToSqr(entityplayer) > 16.0D);
            }
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
