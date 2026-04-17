package com.binaris.wizardry.content.item.armor;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.*;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.*;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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

/**
 * Base class for all wizard armor items in Electroblob's Wizardry Redux. <p>
 *
 * This provides the mana storage, full armor checking and helper methods to apply new {@code SpellModifiers} and set
 * bonuses depending on your armor type. Subclasses of this class will have the deletion of the entity attributes (Protection),
 * spell modifiers and full set bonus if mana is 0 in any of the armor pieces.
 *
 * <p>To create a new armor via addon, extend this class and override the relevant hooks:
 * <ul>
 *   <li>{@link #effectTick} — passive effect applied every tick when wearing the full set</li>
 *   <li>{@link #applyModifiers} — spell modifiers applied when wearing the full set</li>
 *   <li>{@link #appendHoverText} - tooltip lines shown for the item</li>
 * </ul>
 *
 * By default, this already handles the cost reduction when cast a spell with the same element as the armor, increment
 * potency, check cooldown reduction based on the armor type and include the tooltip lines for these bonuses. But you
 * can override these methods to customize/delete any of these behaviors.
 */
public class WizardArmorItem extends ArmorItem implements IManaItem, ICustomDamageItem, IWorkbenchItem, IElementValue, ICustomAttributesItem {
    private final Element element;
    private final WizardArmorType wizardArmorType;

    public WizardArmorItem(WizardArmorType material, Type type, Element element) {
        super(material, type, new Properties());
        this.wizardArmorType = material;
        this.element = element;
    }

    public WizardArmorItem(Type type, Element element) {
        this(WizardArmorType.WIZARD, type, element);
    }

    /**
     * Called every {@code inventoryTick} when the entity is wearing the full armor set and all pieces have mana.
     * In case you want to apply a specific effect without the full set check override {@code inventoryTick} instead.
     *
     * @param stack  the armor item stack
     * @param entity the entity wearing the armor
     * @param level  the current level (world)
     */
    public void effectTick(ItemStack stack, LivingEntity entity, Level level) {
    }

    /**
     * Hook for applying additional {@link SpellModifiers} when the caster is wearing the full
     * armor set with mana. Called from {@link #applySpellModifiers} after base modifiers
     * (elemental cost reduction, cooldown reduction) have already been applied.
     *
     * @param entity    the entity casting the spell
     * @param modifiers the modifiers being built, modify this in place
     * @param armor     the specific armor piece triggering this call
     * @param spell     the spell being cast
     */
    public void applyModifiers(LivingEntity entity, SpellModifiers modifiers, WizardArmorItem armor, Spell spell) {
    }

    /**
     * Internal method that collects and applies all spell modifiers from a single armor piece.
     * Handles elemental cost reduction, potency, and cooldown reduction, then delegates
     * to {@link #applyModifiers} for full-set bonuses.
     *
     * <p>This method is not intended to be overridden in normal cases, do it only if you want to modify the base behavior.
     */
    private void applySpellModifiers(LivingEntity caster, WizardArmorItem armor, Spell spell, SpellModifiers modifiers) {
        ItemStack armorStack = caster.getItemBySlot(armor.getEquipmentSlot());
        if (armor.getMana(armorStack) == 0) return;

        if (spell.getElement() == armor.getElement()) {
            modifiers.set(SpellModifiers.COST, modifiers.get(SpellModifiers.COST) - armor.getWizardArmorType().elementalCostReduction);
        }

        modifiers.set(SpellModifiers.POTENCY, 2);

        if (this.getWizardArmorType().cooldownReduction > 0) {
            modifiers.set(SpellModifiers.COOLDOWN, modifiers.get(SpellModifiers.COOLDOWN) - armor.getWizardArmorType().cooldownReduction);
        }

        if (InventoryUtil.isWearingFullSet(caster, armor.getElement(), armor.getWizardArmorType()) && InventoryUtil.doAllArmourPiecesHaveMana(caster)) {
            applyModifiers(caster, modifiers, armor, spell);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        if (getElement() != null) {
            tooltip.add(Component.translatable("item.%s.wizard_armor.element_cost_reduction".formatted(WizardryMainMod.MOD_ID),
                    (int) (this.getWizardArmorType().elementalCostReduction * 100), getElement().getDescriptionFormatted().getString()).withStyle(ChatFormatting.DARK_GRAY));
        }

        if (this.getWizardArmorType().cooldownReduction > 0) {
            tooltip.add(Component.translatable("item.%s.wizard_armor.cooldown_reduction".formatted(WizardryMainMod.MOD_ID),
                    (int) (this.getWizardArmorType().cooldownReduction * 100)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (getMana(stack) == 0) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;

        if (InventoryUtil.isWearingFullSet(livingEntity, getElement(), getWizardArmorType()) && InventoryUtil.doAllArmourPiecesHaveMana(livingEntity)) {
            effectTick(stack, livingEntity, level);
        }
    }

    public static void onSpellPreCast(SpellCastEvent.Pre event) {
        if (event.getCaster() == null || !(event.getCaster() instanceof Player player)) return;
        SpellModifiers armourModifiers = new SpellModifiers();
        collectArmorModifiers(player, event.getSpell(), armourModifiers);
        event.getModifiers().combine(armourModifiers);
    }

    // Shared helper used by both pre- and tick-cast events to collect modifiers from worn armor.
    public static void collectArmorModifiers(Player caster, Spell spell, SpellModifiers out) {
        Arrays.stream(InventoryUtil.ARMOR_SLOTS)
                .map(slot -> caster.getItemBySlot(slot).getItem()).filter(i -> i instanceof WizardArmorItem)
                .forEach(i -> ((WizardArmorItem) i).applySpellModifiers(caster, (WizardArmorItem) i, spell, out));
    }

    @Override
    public ItemStack applyUpgrade(@Nullable Player player, ItemStack stack, ItemStack upgrade) {
        if (this.getWizardArmorType() != WizardArmorType.WIZARD) return stack;

        for (WizardArmorType armourClass : WizardArmorType.values()) {
            if (upgrade.getItem() != armourClass.upgradeItem.get()) continue;

            Item newItem = RegistryUtils.getArmor(armourClass, this.getElement(), getEquipmentSlot());
            ItemStack newStack = new ItemStack(newItem);
            ((WizardArmorItem) newItem).setMana(newStack, this.getMana(stack));
            newStack.setTag(stack.getTag());
            upgrade.shrink(1);
            return newStack;
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
        changed |= WorkbenchUtils.rechargeManaFromCrystals(centre, crystals);
        return changed;
    }

    @Override
    public void setCustomDamage(ItemStack stack, int damage) {
        stack.getOrCreateTag().putInt("Damage", Math.max(0, Math.min(damage, stack.getMaxDamage())));
    }

    @Override
    public boolean canBreak(ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return (getElement() == null ? super.getName(stack) : Component.literal(super.getName(stack).getString()).withStyle(getElement().getColor()));
    }

    public WizardArmorType getWizardArmorType() {
        return wizardArmorType;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public boolean validForReceptacle() {
        return false;
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
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
        return false;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getCustomAttributes(ItemStack stack, EquipmentSlot slot) {
        if (getMana(stack) != 0) return super.getDefaultAttributeModifiers(slot);
        return ImmutableMultimap.of();
    }
}