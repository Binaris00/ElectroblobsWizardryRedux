package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.item.IWorkbenchItem;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class WizardArmorItem extends ArmorItem implements IManaStoringItem, IWorkbenchItem {
    private static final float SAGE_OTHER_COST_REDUCTION = 0.2f;
    private static final float WARLOCK_SPEED_BOOST = 0.2f;
    Element element;
    WizardArmorType wizardArmorType;

    public WizardArmorItem(WizardArmorType material, Type type, Element element) {
        super(material, type, new Properties());
        this.wizardArmorType = material;
        this.element = element;
    }

    public WizardArmorType getWizardArmorType() {
        return wizardArmorType;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return DrawingUtils.mix(0xff8bfe, 0x8e2ee4, (float) getBarWidth(stack));
    }

    @Override
    public int getMana(ItemStack stack) {
        return getManaCapacity(stack) - stack.getDamageValue();
    }

    @Override
    public void setMana(ItemStack stack, int mana) {
        stack.setDamageValue(getManaCapacity(stack) - mana);
    }

    @Override
    public int getManaCapacity(ItemStack stack) {
        return this.getMaxDamage();
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack applyUpgrade(@Nullable Player player, ItemStack stack, ItemStack upgrade) {
        if (this.wizardArmorType == WizardArmorType.WIZARD) {
            for (WizardArmorType armourClass : WizardArmorType.values()) {
                if (upgrade.getItem() == armourClass.upgradeItem.get()) {
                    Item newArmour = SpellUtil.getArmour(armourClass, this.element, getEquipmentSlot());
                    ItemStack newStack = new ItemStack(newArmour);
                    ((WizardArmorItem) newArmour).setMana(newStack, this.getMana(stack));
                    newStack.setTag(stack.getTag());
                    upgrade.shrink(1);
                    return newStack;
                }
            }
        }

        return stack;
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        boolean changed = false;

        if (upgrade.hasItem()) {
            ItemStack original = centre.getItem().copy();
            centre.set(this.applyUpgrade(player, centre.getItem(), upgrade.getItem()));
            changed = !ItemStack.isSameItem(centre.getItem(), original);
        }

        if (crystals.getItem() != ItemStack.EMPTY && !this.isManaFull(centre.getItem())) {
            int chargeDepleted = this.getManaCapacity(centre.getItem()) - this.getMana(centre.getItem());

            int manaPerItem = crystals.getItem().getItem() instanceof IManaStoringItem ? ((IManaStoringItem) crystals.getItem().getItem()).getMana(crystals.getItem()) : crystals.getItem().getItem() instanceof CrystalItem ? EBConfig.MANA_PER_CRYSTAL : EBConfig.MANA_PER_SHARD;

            if (crystals.getItem().getItem() == EBItems.MAGIC_CRYSTAL_SHARD.get())
                manaPerItem = EBConfig.MANA_PER_SHARD;
            if (crystals.getItem().getItem() == EBItems.MAGIC_CRYSTAL_GRAND.get())
                manaPerItem = EBConfig.GRAND_CRYSTAL_MANA;

            if (crystals.getItem().getCount() * manaPerItem < chargeDepleted) {
                this.rechargeMana(centre.getItem(), crystals.getItem().getCount() * EBConfig.MANA_PER_CRYSTAL);
                crystals.remove(crystals.getItem().getCount());
            } else {
                this.setMana(centre.getItem(), this.getManaCapacity(centre.getItem()));
                crystals.remove((int) Math.ceil(((double) chargeDepleted) / EBConfig.MANA_PER_CRYSTAL));
            }

            changed = true;
        }

        return changed;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        if (element != null)
            tooltip.add(Component.translatable("item.%s.wizard_armour.element_cost_reduction".formatted(WizardryMainMod.MOD_ID),
                    (int) (this.wizardArmorType.elementalCostReduction * 100),
                    element.getDescriptionFormatted().getString()).withStyle(ChatFormatting.DARK_GRAY));

        if (this.wizardArmorType == WizardArmorType.SAGE) {
            tooltip.add(Component.translatable("item.%s.wizard_armour.enchantability".formatted(WizardryMainMod.MOD_ID))
                    .withStyle(ChatFormatting.BLUE));
        }

        if (this.wizardArmorType.cooldownReduction > 0)
            tooltip.add(Component.translatable("item.%s.wizard_armour.cooldown_reduction".formatted(WizardryMainMod.MOD_ID),
                    (int) (this.wizardArmorType.cooldownReduction * 100)).withStyle(ChatFormatting.DARK_GRAY));


        if (this.wizardArmorType != WizardArmorType.WIZARD) {
            tooltip.add(Component.translatable("item.%s.wizard_armour.full_set".formatted(WizardryMainMod.MOD_ID))
                    .withStyle(ChatFormatting.AQUA));

            Object args = new Object[0];

            if (this.wizardArmorType == WizardArmorType.SAGE) args = (int) (SAGE_OTHER_COST_REDUCTION * 100);
            if (this.wizardArmorType == WizardArmorType.WARLOCK) args = (int) (WARLOCK_SPEED_BOOST * 100);

            tooltip.add(Component.translatable("item.%s.%s_armour.full_set_bonus"
                    .formatted(WizardryMainMod.MOD_ID, wizardArmorType.name), args).withStyle(ChatFormatting.AQUA));
        }
    }

    protected void applySpellModifiers(LivingEntity caster, Spell spell, SpellModifiers modifiers){
        if(spell.getElement() == this.element){
            modifiers.set(SpellModifiers.COST, modifiers.get(SpellModifiers.COST) - getWizardArmorType().elementalCostReduction, false);
        }
        modifiers.set(SpellModifiers.POTENCY, 2, false);
        modifiers.set(EBItems.COOLDOWN_UPGRADE.get(), modifiers.get(EBItems.COOLDOWN_UPGRADE.get()) - getWizardArmorType().cooldownReduction, true);

        if(getEquipmentSlot() == EquipmentSlot.HEAD && InventoryUtil.isWearingFullSet(caster, element, getWizardArmorType()) && InventoryUtil.doAllArmourPiecesHaveMana(caster)){
            if(getWizardArmorType() == WizardArmorType.SAGE && spell.getElement() != this.element){
                modifiers.set(SpellModifiers.COST, 1 - SAGE_OTHER_COST_REDUCTION, false);
            }
        }
    }

    public static void onSpellPreCast(SpellCastEvent.Pre event){
        if(event.getCaster() == null) return;

        SpellModifiers armourModifiers = new SpellModifiers();

        Arrays.stream(InventoryUtil.ARMOUR_SLOTS).map(slot -> event.getCaster().getItemBySlot(slot).getItem())
                .filter(i -> i instanceof WizardArmorItem)
                .forEach(i -> ((WizardArmorItem)i).applySpellModifiers(event.getCaster(), event.getSpell(), armourModifiers));

        event.getModifiers().combine(armourModifiers);
    }

    public static void onSpellTickCast(SpellCastEvent.Tick event){
        if(event.getCaster() == null) return;

        SpellModifiers armourModifiers = new SpellModifiers();

        Arrays.stream(InventoryUtil.ARMOUR_SLOTS).map(slot -> event.getCaster().getItemBySlot(slot).getItem())
                .filter(i -> i instanceof WizardArmorItem)
                .forEach(i -> ((WizardArmorItem)i).applySpellModifiers(event.getCaster(), event.getSpell(), armourModifiers));

        event.getModifiers().combine(armourModifiers);
    }
}
