package com.binaris.wizardry.content.item;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.IElementValue;
import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.api.content.util.WorkbenchUtils;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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

public class WizardArmorItem extends ArmorItem implements IManaStoringItem, IWorkbenchItem, IElementValue {
    private static final float SAGE_OTHER_COST_REDUCTION = 0.2f;
    private static final float WARLOCK_SPEED_BOOST = 0.2f;
    private final Element element;
    private final WizardArmorType wizardArmorType;

    public WizardArmorItem(WizardArmorType material, Type type, Element element) {
        super(material, type, new Properties());
        this.wizardArmorType = material;
        this.element = element;
    }

    public static void onSpellPreCast(SpellCastEvent.Pre event) {
        if (event.getCaster() == null || !(event.getCaster() instanceof Player player)) return;
        SpellModifiers armourModifiers = new SpellModifiers();
        collectArmorModifiers(player, event.getSpell(), armourModifiers);
        event.getModifiers().combine(armourModifiers);
    }

    public static void onSpellTickCast(SpellCastEvent.Tick event) {
        if (event.getCaster() == null || !(event.getCaster() instanceof Player player)) return;
        SpellModifiers armourModifiers = new SpellModifiers();
        collectArmorModifiers(player, event.getSpell(), armourModifiers);
        event.getModifiers().combine(armourModifiers);
    }

    // Shared helper used by both pre- and tick-cast events to collect modifiers from worn armor.
    private static void collectArmorModifiers(Player caster, Spell spell, SpellModifiers out) {
        Arrays.stream(InventoryUtil.ARMOR_SLOTS)
                .map(slot -> caster.getItemBySlot(slot).getItem())
                .filter(i -> i instanceof WizardArmorItem)
                .forEach(i -> ((WizardArmorItem) i).applySpellModifiers(caster, spell, out));
    }

    @Override
    public ItemStack applyUpgrade(@Nullable Player player, ItemStack stack, ItemStack upgrade) {
        if (this.wizardArmorType != WizardArmorType.WIZARD) return stack;

        for (WizardArmorType armourClass : WizardArmorType.values()) {
            if (upgrade.getItem() != armourClass.upgradeItem.get()) continue;

            Item newItem = RegistryUtils.getArmor(armourClass, this.element, getEquipmentSlot());
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
    public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
        if (getElement() != null) {
            tooltip.add(Component.translatable("item.%s.wizard_armor.element_cost_reduction".formatted(WizardryMainMod.MOD_ID),
                    (int) (this.wizardArmorType.elementalCostReduction * 100),
                    element.getDescriptionFormatted().getString()).withStyle(ChatFormatting.DARK_GRAY));
        }

        if (this.wizardArmorType == WizardArmorType.SAGE) {
            tooltip.add(Component.translatable("item.%s.wizard_armor.enchantability".formatted(WizardryMainMod.MOD_ID))
                    .withStyle(ChatFormatting.BLUE));
        }

        if (this.wizardArmorType.cooldownReduction > 0) {
            tooltip.add(Component.translatable("item.%s.wizard_armor.cooldown_reduction".formatted(WizardryMainMod.MOD_ID),
                    (int) (this.wizardArmorType.cooldownReduction * 100)).withStyle(ChatFormatting.DARK_GRAY));
        }

        if (this.wizardArmorType != WizardArmorType.WIZARD) {
            tooltip.add(Component.translatable("item.%s.wizard_armor.full_set".formatted(WizardryMainMod.MOD_ID))
                    .withStyle(ChatFormatting.AQUA));

            Object[] args = new Object[0];
            if (this.wizardArmorType == WizardArmorType.SAGE)
                args = new Object[]{(int) (SAGE_OTHER_COST_REDUCTION * 100)};
            if (this.wizardArmorType == WizardArmorType.WARLOCK) args = new Object[]{(int) (WARLOCK_SPEED_BOOST * 100)};

            tooltip.add(Component.translatable("item.%s.%s_armor.full_set_bonus"
                    .formatted(WizardryMainMod.MOD_ID, wizardArmorType.name().toLowerCase()), args).withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide) return;
        if (level.getGameTime() % 40 != 0) return;

        if (entity instanceof LivingEntity livingEntity && getEquipmentSlot() == EquipmentSlot.HEAD
                && InventoryUtil.isWearingFullSet(livingEntity, element, getWizardArmorType()) && InventoryUtil.doAllArmourPiecesHaveMana(livingEntity)) {

            if (getWizardArmorType() == WizardArmorType.WARLOCK) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 0, false, false, false));
                return;
            }

            if (getWizardArmorType() == WizardArmorType.BATTLEMAGE) {
                livingEntity.addEffect(new MobEffectInstance(EBMobEffects.WARD.get(), 80, 0, false, false, false));
            }
        }
    }

    protected void applySpellModifiers(LivingEntity caster, Spell spell, SpellModifiers modifiers) {
        if (spell.getElement() == this.getElement()) {
            modifiers.set(SpellModifiers.COST, modifiers.get(SpellModifiers.COST) - getWizardArmorType().elementalCostReduction, false);
        }
        modifiers.set(SpellModifiers.POTENCY, 2, false);
        modifiers.set(EBItems.COOLDOWN_UPGRADE.get(), modifiers.get(EBItems.COOLDOWN_UPGRADE.get()) - getWizardArmorType().cooldownReduction, true);

        if (getEquipmentSlot() == EquipmentSlot.HEAD
                && InventoryUtil.isWearingFullSet(caster, element, getWizardArmorType())
                && InventoryUtil.doAllArmourPiecesHaveMana(caster)) {

            if (getWizardArmorType() == WizardArmorType.SAGE && spell.getElement() != this.element) {
                modifiers.set(SpellModifiers.COST, 1 - SAGE_OTHER_COST_REDUCTION, false);
            }
        }
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
}