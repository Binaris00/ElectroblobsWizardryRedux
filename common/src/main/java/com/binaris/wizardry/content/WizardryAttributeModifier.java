package com.binaris.wizardry.content;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.*;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.setup.registries.EBAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/// Extension of the base Attribute Modifiers to add [SpellCondition] for being used with the `/magic_attribute`
/// command. We use the Mixin `AttributeModifierMixin` to add the nbt loading.
///
/// This doesn't cancel or interrupt the base Attribute Modifier system, we can still use it normally. This just adds new
/// load values when need it.
public class WizardryAttributeModifier extends AttributeModifier {
    @Nullable
    private final SpellCondition condition;

    public WizardryAttributeModifier(String name, double amount, Operation operation) {
        super(name, amount, operation);
        condition = null;
    }

    public WizardryAttributeModifier(UUID uuid, String name, double amount,
                                     Operation operation, @Nullable SpellCondition condition) {
        super(uuid, name, amount, operation);
        this.condition = condition;
    }

    public @Nullable SpellCondition getCondition() {
        return condition;
    }

    @Override
    public @NotNull CompoundTag save() {
        CompoundTag tag = super.save();
        if (condition != null) condition.save(tag);
        return tag;
    }

    public static void onPreCast(SpellCastEvent.Pre event) {
        if (event.getCaster() == null) return;
        LivingEntity caster = event.getCaster();

        convertToModifiers(event.getModifiers(), SpellModifiers.POTENCY, caster, event.getSpell(), EBAttributes.CAST_POTENCY.get());
        convertToModifiers(event.getModifiers(), SpellModifiers.COST, caster, event.getSpell(), EBAttributes.CAST_COST.get());
        convertToModifiers(event.getModifiers(), SpellModifiers.CHARGEUP, caster, event.getSpell(), EBAttributes.CAST_CHARGEUP.get());
        convertToModifiers(event.getModifiers(), SpellModifiers.PROGRESSION, caster, event.getSpell(), EBAttributes.CAST_PROGRESSION.get());
        convertToModifiers(event.getModifiers(), SpellModifiers.DURATION, caster, event.getSpell(), EBAttributes.CAST_DURATION.get());
        convertToModifiers(event.getModifiers(), SpellModifiers.BLAST, caster, event.getSpell(), EBAttributes.CAST_BLAST.get());
        convertToModifiers(event.getModifiers(), SpellModifiers.RANGE, caster, event.getSpell(), EBAttributes.CAST_RANGE.get());
        convertToModifiers(event.getModifiers(), SpellModifiers.COOLDOWN, caster, event.getSpell(), EBAttributes.CAST_COOLDOWN.get());
    }

    private static void convertToModifiers(SpellModifiers modifiers, String key, LivingEntity caster, Spell spell, Attribute attribute) {
        AttributeInstance instance = caster.getAttribute(attribute);
        if (instance == null) return;

        List<AttributeModifier> attributes = instance.getModifiers().stream().sorted(Comparator.comparingInt(m -> m.getOperation().toValue())).toList();

        for (AttributeModifier attributeModifier : attributes) {
            if (!(attributeModifier instanceof WizardryAttributeModifier wizardryModifier) || wizardryModifier.getCondition() == null || wizardryModifier.getCondition().isEmpty()) {
                applyVanillaModifier(modifiers, key, attributeModifier);
                continue;
            }

            if (wizardryModifier.getCondition().test(spell)) {
                applyVanillaModifier(modifiers, key, attributeModifier);
            }
        }
    }

    /// Maps a vanilla attribute modifier onto the spell modifiers, converting vanilla increments into direct factors.
    private static void applyVanillaModifier(SpellModifiers modifiers, String key, AttributeModifier attributeModifier) {
        float amount = (float) attributeModifier.getAmount();
        switch (attributeModifier.getOperation()) {
            case ADDITION -> modifiers.add(key, amount);
            case MULTIPLY_BASE, MULTIPLY_TOTAL -> modifiers.multiplyTotal(key, 1.0f + amount);
            default -> {}
        }
    }

    /// Search and load the attribute modifiers saved in the living entity, first, organize the attributes based on the
    /// operation order and then values the vanilla attribute modifiers and wizard attribute modifiers (modifiers with
    /// conditions). In case the entity doesn't have the attribute instance it will return 1.
    ///
    /// @param entity    living entity that could have the given attribute
    /// @param condition filter that goes to test the modifiers result
    /// @param attribute attribute that needs to be check in order to find its modifiers
    /// @return the calculation result of all the modifiers, 1 if there wasn't any modifiers or the entity doesn't have
    /// the attribute instance
    public static float calculateModifiers(LivingEntity entity, SpellCondition condition, Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return 1;

        List<AttributeModifier> attributes = instance.getModifiers().stream().sorted(Comparator.comparingInt(m -> m.getOperation().toValue())).toList();
        Accumulator acc = new Accumulator();
        for (AttributeModifier attributeModifier : attributes) {
            if (!(attributeModifier instanceof WizardryAttributeModifier wizardryModifier) || wizardryModifier.getCondition() == null || wizardryModifier.getCondition().isEmpty()) {
                acc.apply(attributeModifier.getOperation(), attributeModifier.getAmount());
                continue;
            }

            if (wizardryModifier.getCondition().test(condition)) {
                acc.apply(wizardryModifier.getOperation(), wizardryModifier.getAmount());
            }
        }

        return (float) acc.result();
    }

    /// Vanilla-style accumulator: base 1, additions sum, MULTIPLY_BASE adds to base, MULTIPLY_TOTAL multiplies total.
    private static final class Accumulator {
        private double value = 1;
        private double additions = 0;
        private double multiplyBase = 0;
        private double multiplyTotal = 1;

        void apply(AttributeModifier.Operation op, double amount) {
            switch (op) {
                case ADDITION -> additions += amount;
                case MULTIPLY_BASE -> multiplyBase += amount;
                case MULTIPLY_TOTAL -> multiplyTotal *= 1 + amount;
            }
        }

        double result() {
            return (value + additions + value * multiplyBase) * multiplyTotal;
        }
    }
}
