package com.electroblob.wizardry.content.advancement;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.event.EBDiscoverSpellEvent;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SpellDiscoveryTrigger implements CriterionTrigger<SpellDiscoveryTrigger.TriggerInstance> {
    static final ResourceLocation ID = WizardryMainMod.location("discover_spell");
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    public SpellDiscoveryTrigger() {
    }

    public @NotNull ResourceLocation getId() {
        return ID;
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

    public @NotNull SpellDiscoveryTrigger.TriggerInstance createInstance(@NotNull JsonObject json, @NotNull DeserializationContext context) {
        String s = GsonHelper.getAsString(json, "source");

        EBDiscoverSpellEvent.Source source = EBDiscoverSpellEvent.Source.byName(s);
        if (source == null) throw new JsonSyntaxException("No such spell discovery source: " + s);
        return new TriggerInstance(SpellPredicate.deserialize(json.get("spell")), source, json, context);
    }

    public void trigger(ServerPlayer player, Spell spell, EBDiscoverSpellEvent.Source source) {
        Optional.ofNullable(this.listeners.get(player.getAdvancements())).ifPresent(listeners -> listeners.trigger(spell, source));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final SpellPredicate spell;
        private final EBDiscoverSpellEvent.Source source;

        public TriggerInstance(SpellPredicate spell, EBDiscoverSpellEvent.Source source, JsonObject json, DeserializationContext context) {
            super(ID, EntityPredicate.fromJson(json, "player", context));
            this.spell = spell;
            this.source = source;
        }

        public TriggerInstance(SpellPredicate spell, EBDiscoverSpellEvent.Source source) {
            super(ID, ContextAwarePredicate.ANY);
            this.spell = spell;
            this.source = source;
        }

        public static TriggerInstance discoverSpell(EBDiscoverSpellEvent.Source source) {
            return new TriggerInstance(SpellPredicate.any(), source);
        }

        public boolean test(Spell spell, EBDiscoverSpellEvent.Source source) {
            return this.spell.test(spell) && source == this.source;
        }

        @Override
        public @NotNull JsonObject serializeToJson(@NotNull SerializationContext conditions) {
            JsonObject jsonobject = super.serializeToJson(conditions);
            jsonobject.addProperty("source", this.source.name().toLowerCase());
            JsonElement spellJson = this.spell.serialize();
            if (spellJson != null) jsonobject.add("spell", spellJson);
            return jsonobject;
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

        public void trigger(Spell spell, EBDiscoverSpellEvent.Source source) {
            List<Listener<TriggerInstance>> list = this.listeners.stream()
                    .filter(li -> li.getTriggerInstance().test(spell, source)).toList();
            list.forEach(li -> li.run(this.playerAdvancements));
        }
    }
}
