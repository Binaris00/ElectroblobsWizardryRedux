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

/**
 * Extension of the base Attribute Modifiers to add {@link SpellCondition} for being used with the {@code /magic_attribute}
 * command. We use the Mixin {@code AttributeModifierMixin} to add the nbt loading.
 * <p>
 * This doesn't cancel or interrupt the base Attribute Modifier system, we can still use it normally. This just adds new
 * load values when need it.
 */
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

        event.getModifiers().add(SpellModifiers.POTENCY, calculateModifiers(caster, event.getSpell(), EBAttributes.CAST_POTENCY.get()));
        event.getModifiers().add(SpellModifiers.COST, calculateModifiers(caster, event.getSpell(), EBAttributes.CAST_COST.get()));
        event.getModifiers().add(SpellModifiers.CHARGEUP, calculateModifiers(caster, event.getSpell(), EBAttributes.CAST_CHARGEUP.get()));
        event.getModifiers().add(SpellModifiers.PROGRESSION, calculateModifiers(caster, event.getSpell(), EBAttributes.CAST_PROGRESSION.get()));
        event.getModifiers().add(SpellModifiers.DURATION, calculateModifiers(caster, event.getSpell(), EBAttributes.CAST_DURATION.get()));
        event.getModifiers().add(SpellModifiers.BLAST, calculateModifiers(caster, event.getSpell(), EBAttributes.CAST_BLAST.get()));
        event.getModifiers().add(SpellModifiers.RANGE, calculateModifiers(caster, event.getSpell(), EBAttributes.CAST_RANGE.get()));
        event.getModifiers().add(SpellModifiers.COOLDOWN, calculateModifiers(caster, event.getSpell(), EBAttributes.CAST_COOLDOWN.get()));
    }

    /**
     * Search and load the attribute modifiers saved in the living entity, first, organize the attributes based on the
     * operation order and then values the vanilla attribute modifiers and wizard attribute modifiers (modifiers with
     * conditions). In case the entity doesn't have the attribute instance it will return 0.
     *
     * @param entity    living entity that could have the given attribute
     * @param spell     result of the casting/logic made by the entity
     * @param attribute attribute that needs to be check in order to find its modifiers
     * @return the calculation result of all the modifiers, 0 if there wasn't any modifiers or the entity doesn't have the
     * attribute instance
     */
    public static float calculateModifiers(LivingEntity entity, Spell spell, Attribute attribute) {
        double value = 0;
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return (float) value;

        List<AttributeModifier> attributes = instance.getModifiers().stream().sorted(Comparator.comparingInt(m -> m.getOperation().toValue())).toList();
        for (AttributeModifier attributeModifier : attributes) {
            if (!(attributeModifier instanceof WizardryAttributeModifier wizardryModifier) || wizardryModifier.getCondition() == null || wizardryModifier.getCondition().isEmpty()) {
                switch (attributeModifier.getOperation()) {
                    case ADDITION -> value += attributeModifier.getAmount();
                    case MULTIPLY_BASE, MULTIPLY_TOTAL -> value *= attributeModifier.getAmount();
                }
                continue;
            }

            if (wizardryModifier.getCondition().test(spell)) {
                switch (wizardryModifier.getOperation()) {
                    case ADDITION -> value += wizardryModifier.getAmount();
                    case MULTIPLY_BASE, MULTIPLY_TOTAL -> value *= wizardryModifier.getAmount();
                }
            }
        }

        return (float) value;
    }

    /**
     * Search and load the attribute modifiers saved in the living entity, first, organize the attributes based on the
     * operation order and then values the vanilla attribute modifiers and wizard attribute modifiers (modifiers with
     * conditions). In case the entity doesn't have the attribute instance it will return 0.
     *
     * @param entity    living entity that could have the given attribute
     * @param condition filter that goes to test the modifiers result
     * @param attribute attribute that needs to be check in order to find its modifiers
     * @return the calculation result of all the modifiers, 0 if there wasn't any modifiers or the entity doesn't have the
     * attribute instance
     */
    public static float calculateModifiers(LivingEntity entity, SpellCondition condition, Attribute attribute) {
        double value = 0;
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return (float) value;

        List<AttributeModifier> attributes = instance.getModifiers().stream().sorted(Comparator.comparingInt(m -> m.getOperation().toValue())).toList();
        for (AttributeModifier attributeModifier : attributes) {
            if (!(attributeModifier instanceof WizardryAttributeModifier wizardryModifier) || wizardryModifier.getCondition() == null || wizardryModifier.getCondition().isEmpty()) {
                switch (attributeModifier.getOperation()) {
                    case ADDITION -> value += attributeModifier.getAmount();
                    case MULTIPLY_BASE, MULTIPLY_TOTAL -> value *= attributeModifier.getAmount();
                }
                continue;
            }

            if (wizardryModifier.getCondition().test(condition)) {
                switch (wizardryModifier.getOperation()) {
                    case ADDITION -> value += wizardryModifier.getAmount();
                    case MULTIPLY_BASE, MULTIPLY_TOTAL -> value *= wizardryModifier.getAmount();
                }
            }
        }

        return (float) value;
    }
}
