package com.electroblob.wizardry.content.loot;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.content.item.ScrollItem;
import com.electroblob.wizardry.content.item.SpellBookItem;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBLootFunctions;
import com.electroblob.wizardry.setup.registries.Spells;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RandomSpellFunction extends LootItemConditionalFunction {
    private final @Nullable List<Spell> spells;
    private final @Nullable List<Element> elements;
    private final @Nullable List<SpellTier> tiers;
    private final boolean ignoreWeighting;
    private final float undiscoveredBias;

    protected RandomSpellFunction(LootItemCondition[] conditions, @Nullable List<Spell> spells, boolean ignoreWeighting, float undiscoveredBias, @Nullable List<SpellTier> tiers, @Nullable List<Element> elements) {
        super(conditions);
        this.spells = spells;
        this.ignoreWeighting = ignoreWeighting;
        this.undiscoveredBias = undiscoveredBias;
        this.tiers = tiers;
        this.elements = elements;
    }

    public static LootItemConditionalFunction.Builder<?> setRandomSpell(List<Spell> spells, boolean ignoreWeighting, float undiscoveredBias, List<SpellTier> tiers, List<Element> elements) {
        return simpleBuilder((conditions) ->
                new RandomSpellFunction(conditions, spells, ignoreWeighting, undiscoveredBias, tiers, elements));
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return EBLootFunctions.RANDOM_SPELL.get();
    }

    @Override
    protected @NotNull ItemStack run(ItemStack stack, @NotNull LootContext context) {
        if (!(stack.getItem() instanceof SpellBookItem) && !(stack.getItem() instanceof ScrollItem))
            EBLogger.warn("Applying the random_spell loot function to an item that isn't a spell book or scroll.");

        Player player = context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);
        Spell spell = pickRandomSpell(stack, context.getRandom(), player);

        if (spell == Spells.NONE) {
            EBLogger.warn("Tried to apply the random_spell loot function to an item, but no enabled spells matched the criteria specified. Using Magic Missile as a fallback.");
            return SpellUtil.setSpell(stack, Spells.MAGIC_MISSILE);
        }

        return SpellUtil.setSpell(stack, spell);
    }

    private Spell pickRandomSpell(ItemStack stack, RandomSource random, Player player) {
        ArrayList<Spell> possibleSpells = new ArrayList<>(Services.REGISTRY_UTIL.getSpells());

        EBLogger.info("Picking random spell for loot with {} possible spells before filtering.", possibleSpells.size());

        // Checking spells, if the spells list is specified
        if (spells != null && !spells.isEmpty()) possibleSpells.retainAll(spells);

        EBLogger.info("{} possible spells after spell filtering.", possibleSpells.size());

        // Checking tiers, if the tiers list is specified
        if (tiers != null && !tiers.isEmpty()) {
            EBLogger.info("Filtering by tiers: {}", tiers);
            EBLogger.info("Sample spell tiers before filtering: {}",
                    possibleSpells.stream().limit(5).map(s -> s.getLocation() + "=" + s.getTier()).collect(Collectors.toList()));
            possibleSpells.removeIf(possibleSpell -> !tiers.contains(possibleSpell.getTier()));
        }

        EBLogger.info("{} possible spells after tier filtering.", possibleSpells.size());

        // Checking elements, if the elements list is specified
        if (elements != null && !elements.isEmpty()) {
            EBLogger.info("Filtering by elements: {}", elements);
            if (!possibleSpells.isEmpty()) {
                EBLogger.info("Sample spell elements before filtering: {}",
                        possibleSpells.stream().limit(5).map(s -> s.getLocation() + "=" + s.getElement()).collect(Collectors.toList()));
            }
            possibleSpells.removeIf(possibleSpell -> !elements.contains(possibleSpell.getElement()));
        }

        EBLogger.info("{} possible spells after element filtering.", possibleSpells.size());

        if (player != null && undiscoveredBias > 0) {
            float bias = undiscoveredBias;
            if (EBAccessoriesIntegration.isEquipped(player, EBItems.CHARM_SPELL_DISCOVERY.get()))
                bias = Math.min(bias + 0.4f, 0.9f);
            if (bias > 0) {
                SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);
                int discoveredCount = (int) possibleSpells.stream().filter(data::hasSpellBeenDiscovered).count();
                if (discoveredCount > 0 && discoveredCount < possibleSpells.size()) {
                    boolean keepDiscovered = random.nextFloat() > 0.5f + 0.5f * undiscoveredBias;
                    possibleSpells.removeIf(s -> keepDiscovered != data.hasSpellBeenDiscovered(s));
                }
            }
        }

        if (possibleSpells.isEmpty()) {
            EBLogger.error("No spells matched the loot criteria after filtering.");
            return Spells.NONE;
        }

        return possibleSpells.get(random.nextInt(possibleSpells.size()));
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<RandomSpellFunction> {
        public Serializer() {
        }

        @Override
        public void serialize(@NotNull JsonObject object, @NotNull RandomSpellFunction function, @NotNull JsonSerializationContext serializationContext) {
            if (function.spells != null && !function.spells.isEmpty()) {
                DataResult<JsonElement> result = ResourceLocation.CODEC.listOf().encodeStart(JsonOps.INSTANCE, function.spells.stream().map(Spell::getLocation).collect(Collectors.toList()));
                result.result().ifPresent((jsonElement -> object.add("spells", jsonElement)));
            }

            object.addProperty("ignore_weighting", function.ignoreWeighting);
            object.addProperty("undiscovered_bias", function.undiscoveredBias);

            if (function.tiers != null && !function.tiers.isEmpty()) {
                DataResult<JsonElement> result = ResourceLocation.CODEC.listOf().encodeStart(JsonOps.INSTANCE, function.tiers.stream().map(SpellTier::getOrCreateLocation).collect(Collectors.toList()));
                result.result().ifPresent((jsonElement -> object.add("tiers", jsonElement)));
            }

            if (function.elements != null && !function.elements.isEmpty()) {
                DataResult<JsonElement> result = ResourceLocation.CODEC.listOf().encodeStart(JsonOps.INSTANCE, function.elements.stream().map(Element::getLocation).collect(Collectors.toList()));
                result.result().ifPresent((jsonElement -> object.add("elements", jsonElement)));
            }
        }

        @Override
        public @NotNull RandomSpellFunction deserialize(JsonObject object, @NotNull JsonDeserializationContext deserializationContext, LootItemCondition @NotNull [] conditions) {
            List<Spell> spells = null;
            List<SpellTier> tiers = null;
            List<Element> elements = null;

            if (object.has("spells")) {
                DataResult<List<ResourceLocation>> result = ResourceLocation.CODEC.listOf().parse(JsonOps.INSTANCE, object.get("spells"));
                if (result.result().isPresent())
                    spells = result.result().get().stream().map(Services.REGISTRY_UTIL::getSpell).collect(Collectors.toList());
            }

            boolean ignoreWeighting = GsonHelper.getAsBoolean(object, "ignore_weighting", false);
            float undiscoveredBias = GsonHelper.getAsFloat(object, "undiscovered_bias", 0);

            if (object.has("tiers")) {
                DataResult<List<ResourceLocation>> result = ResourceLocation.CODEC.listOf().parse(JsonOps.INSTANCE, object.get("tiers"));
                if (result.result().isPresent()) {
                    tiers = result.result().get().stream().map(Services.REGISTRY_UTIL::getTier).collect(Collectors.toList());
                    if (tiers.contains(null)) {
                        EBLogger.warn("One or more invalid spell tiers found when deserializing random_spell loot function from " + object.toString());
                        tiers.removeIf(Objects::isNull);
                    }
                }
            }

            if (object.has("elements")) {
                DataResult<List<ResourceLocation>> result = ResourceLocation.CODEC.listOf().parse(JsonOps.INSTANCE, object.get("elements"));
                if (result.result().isPresent()) {
                    elements = result.result().get().stream().map(Services.REGISTRY_UTIL::getElement).collect(Collectors.toList());
                    if (elements.contains(null)) {
                        EBLogger.warn("One or more invalid elements found when deserializing random_spell loot function from " + object.toString());
                        elements.removeIf(Objects::isNull);
                    }
                }
            }

            return new RandomSpellFunction(conditions, spells, ignoreWeighting, undiscoveredBias, tiers, elements);
        }
    }
}
