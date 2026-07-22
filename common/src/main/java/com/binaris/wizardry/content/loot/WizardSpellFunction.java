package com.binaris.wizardry.content.loot;

import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.api.content.entity.living.ISpellCaster;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.content.item.ScrollItem;
import com.binaris.wizardry.content.item.SpellBookItem;
import com.binaris.wizardry.setup.registries.EBLootFunctions;
import com.binaris.wizardry.setup.registries.Spells;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// A loot table function that assigns a spell to a spell book or scroll based on the spells
/// known by the killed entity that dropped it.
///
/// Used exclusively in the {@code evil_wizard} entity loot table. When the killed entity
/// implements {@code ISpellCaster}, this function randomly picks one of that entity's spells
/// (excluding {@code Spells.MAGIC_MISSILE}) and writes it onto the looted item via
/// {@code RegistryUtils.setSpell}. If the entity is not a spell caster, or the item is
/// neither a spell book nor a scroll, the function logs a warning and returns the stack
/// unchanged.
///
/// The serializer reads and writes no additional JSON fields — the function has no
/// configurable parameters.
public class WizardSpellFunction extends LootItemConditionalFunction {
    protected WizardSpellFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return EBLootFunctions.WIZARD_SPELL;
    }

    @Override
    protected @NotNull ItemStack run(ItemStack stack, @NotNull LootContext context) {
        if (!(stack.getItem() instanceof SpellBookItem) && !(stack.getItem() instanceof ScrollItem))
            EBLogger.warn("Applying the wizard_spell loot function to an item that isn't a spell book or scroll.");

        if (!(context.getParam(LootContextParams.THIS_ENTITY) instanceof ISpellCaster)) {
            EBLogger.warn("Applying the wizard_spell loot function to an entity that isn't a spell caster.");
            return stack;
        }

        List<Spell> spells = ((ISpellCaster) context.getParam(LootContextParams.THIS_ENTITY)).getSpells();
        spells.remove(Spells.MAGIC_MISSILE);
        if (spells.isEmpty()) {
            EBLogger.warn("Tried to apply the wizard_spell loot function to an item, but none of the looted entity's spells were applicable for that item. This is probably a bug!");
            return stack;
        }

        RegistryUtils.setSpell(stack, spells.get(context.getRandom().nextInt(spells.size())));
        return stack;
    }

    /// Serializer for {@code WizardSpellFunction} that handles JSON (de)serialization
    /// and is registered as {@code EBLootFunctions.WIZARD_SPELL}.
    ///
    /// This function has no configurable JSON parameters, so {@code serialize} writes
    /// nothing and {@code deserialize} merely instantiates the function with the given
    /// conditions.
    public static class Serializer extends LootItemConditionalFunction.Serializer<WizardSpellFunction> {
        public Serializer() {
        }

        /// Writes nothing — this function has no configurable JSON data.
        ///
        /// @param json the JSON object to write to.
        /// @param loot the WizardSpellFunction instance being serialized.
        /// @param context the serialization context.
        @Override
        public void serialize(@NotNull JsonObject json, @NotNull WizardSpellFunction loot, @NotNull JsonSerializationContext context) {
        }

        /// Deserializes a {@code WizardSpellFunction} from JSON, ignoring the object's
        /// contents since this function has no configurable parameters.
        ///
        /// @param object the JSON object, ignored.
        /// @param context the deserialization context.
        /// @param conditions the loot conditions applied to this function.
        /// @return a new WizardSpellFunction with the given conditions.
        @Override
        public @NotNull WizardSpellFunction deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, LootItemCondition @NotNull [] conditions) {
            return new WizardSpellFunction(conditions);
        }
    }
}
