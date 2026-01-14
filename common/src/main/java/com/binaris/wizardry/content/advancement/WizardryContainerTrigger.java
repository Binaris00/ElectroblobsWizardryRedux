package com.binaris.wizardry.content.advancement;

import com.binaris.wizardry.WizardryMainMod;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class WizardryContainerTrigger implements CriterionTrigger<WizardryContainerTrigger.TriggerInstance> {
    private final ResourceLocation ID;
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    public WizardryContainerTrigger(String name) {
        this.ID = WizardryMainMod.location(name);
    }

    public @NotNull ResourceLocation getId() {
        return this.ID;
    }

    public void addPlayerListener(@NotNull PlayerAdvancements advancements, @NotNull Listener<TriggerInstance> listener) {
        this.listeners.computeIfAbsent(advancements, Listeners::new).add(listener);
    }

    public void removePlayerListener(@NotNull PlayerAdvancements advancements, @NotNull Listener<TriggerInstance> listener) {
        Listeners li = this.listeners.get(advancements);
        if (li == null) return;

        li.remove(listener);
        if (li.isEmpty()) this.listeners.remove(advancements);
    }

    public void removePlayerListeners(@NotNull PlayerAdvancements advancements) {
        this.listeners.remove(advancements);
    }

    public @NotNull WizardryContainerTrigger.TriggerInstance createInstance(@NotNull JsonObject json, @NotNull DeserializationContext context) {
        return new TriggerInstance(this.ID, ItemPredicate.fromJson(json.get("item")), json, context);
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        Optional.ofNullable(this.listeners.get(player.getAdvancements())).ifPresent(li -> li.trigger(stack));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ItemPredicate item;

        public TriggerInstance(ResourceLocation criterionIn, ItemPredicate item, JsonObject json, DeserializationContext context) {
            super(criterionIn, EntityPredicate.fromJson(json, "player", context));
            this.item = item;
        }

        public TriggerInstance(ResourceLocation criterionIn, ItemPredicate item) {
            super(criterionIn, ContextAwarePredicate.ANY);
            this.item = item;
        }

        @Override
        public @NotNull JsonObject serializeToJson(@NotNull SerializationContext conditions) {
            JsonObject jsonobject = super.serializeToJson(conditions);
            jsonobject.add("item", this.item.serializeToJson());
            return jsonobject;
        }

        public boolean test(ItemStack stack) {
            return this.item.matches(stack);
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<TriggerInstance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements advancements) {
            this.playerAdvancements = advancements;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(Listener<TriggerInstance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<TriggerInstance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(ItemStack stack) {
            List<Listener<TriggerInstance>> list = this.listeners.stream()
                    .filter(li -> li.getTriggerInstance().test(stack)).toList();
            list.forEach(li -> li.run(this.playerAdvancements));
        }
    }
}
