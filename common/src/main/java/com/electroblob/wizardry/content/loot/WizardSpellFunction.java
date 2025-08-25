package com.electroblob.wizardry.content.loot;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.entity.living.ISpellCaster;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.content.item.ScrollItem;
import com.electroblob.wizardry.content.item.SpellBookItem;
import com.electroblob.wizardry.setup.registries.EBLootFunctions;
import com.electroblob.wizardry.setup.registries.Spells;
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

public class WizardSpellFunction extends LootItemConditionalFunction {
    protected WizardSpellFunction(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return EBLootFunctions.WIZARD_SPELL.get();
    }

    @Override
    protected @NotNull ItemStack run(ItemStack stack, @NotNull LootContext context) {
        if (!(stack.getItem() instanceof SpellBookItem) && !(stack.getItem() instanceof ScrollItem))
            EBLogger.warn("Applying the wizard_spell loot function to an item that isn't a spell book or scroll.");

        if(!(context.getParam(LootContextParams.THIS_ENTITY) instanceof ISpellCaster)){
            EBLogger.warn("Applying the wizard_spell loot function to an entity that isn't a spell caster.");
            return stack;
        }

        List<Spell> spells = ((ISpellCaster) context.getParam(LootContextParams.THIS_ENTITY)).getSpells();
        spells.remove(Spells.MAGIC_MISSILE);
        if(spells.isEmpty()){
            EBLogger.warn("Tried to apply the wizard_spell loot function to an item, but none of the looted entity's spells were applicable for that item. This is probably a bug!");
            return stack;
        }

        SpellUtil.setSpell(stack, spells.get(context.getRandom().nextInt(spells.size())));
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<WizardSpellFunction> {
        public Serializer() {}

        @Override
        public void serialize(@NotNull JsonObject json, @NotNull WizardSpellFunction loot, @NotNull JsonSerializationContext context) {}

        @Override
        public @NotNull WizardSpellFunction deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context, LootItemCondition @NotNull [] conditions) {
            return new WizardSpellFunction(conditions);
        }
    }
}
