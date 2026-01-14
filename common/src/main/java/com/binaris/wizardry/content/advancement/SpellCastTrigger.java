package com.binaris.wizardry.content.advancement;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.Spell;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// todo legacy code, needed for advancement of "all spell casted"
public class SpellCastTrigger implements CriterionTrigger<SpellCastTrigger.TriggerInstance> {
    private final ResourceLocation ID;
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    public SpellCastTrigger() {
        this.ID = WizardryMainMod.location("cast_spell");
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

    public @NotNull SpellCastTrigger.TriggerInstance createInstance(@NotNull JsonObject json, @NotNull DeserializationContext context) {
        return new TriggerInstance(this.ID, SpellPredicate.deserialize(json.get("spell")), ItemPredicate.fromJson(json.get("item")), json, context);
    }

    public void trigger(ServerPlayer player, Spell spell, ItemStack stack) {
        Optional.ofNullable(this.listeners.get(player.getAdvancements())).ifPresent(li -> li.trigger(spell, stack));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final SpellPredicate spell;
        private final ItemPredicate item;

        public TriggerInstance(ResourceLocation criterion, SpellPredicate spell, ItemPredicate item, JsonObject json, DeserializationContext context) {
            super(criterion, EntityPredicate.fromJson(json, "spell", context));
            this.spell = spell;
            this.item = item;
        }

        public boolean test(Spell spell, ItemStack stack) {
            return this.spell.test(spell) && item.matches(stack);
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

        public void trigger(Spell spell, ItemStack stack) {
            List<Listener<TriggerInstance>> list = this.listeners.stream()
                    .filter(li -> li.getTriggerInstance().test(spell, stack)).toList();
            list.forEach(li -> li.run(this.playerAdvancements));
        }
    }
}
