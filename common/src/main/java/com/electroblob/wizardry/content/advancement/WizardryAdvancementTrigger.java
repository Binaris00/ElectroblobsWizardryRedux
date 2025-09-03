package com.electroblob.wizardry.content.advancement;

import com.electroblob.wizardry.WizardryMainMod;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * General version of a {@code SimpleCriterionTrigger} for the mod, we use this to simple call "advancement events"
 * inside the mod code, no need to get/save data for it
 */
public class WizardryAdvancementTrigger implements CriterionTrigger<WizardryAdvancementTrigger.TriggerInstance> {
    private final ResourceLocation ID;
    private final SetMultimap<PlayerAdvancements, Listener<? extends CriterionTriggerInstance>> listeners = HashMultimap.create();

    public WizardryAdvancementTrigger(String name) {
        super();
        ID = WizardryMainMod.location(name);
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addPlayerListener(@NotNull PlayerAdvancements playerAdvancementsIn, @NotNull Listener<TriggerInstance> listener) {
        listeners.put(playerAdvancementsIn, listener);
    }

    @Override
    public void removePlayerListener(@NotNull PlayerAdvancements playerAdvancementsIn, @NotNull Listener<TriggerInstance> listener) {
        listeners.remove(playerAdvancementsIn, listener);
    }

    @Override
    public void removePlayerListeners(@NotNull PlayerAdvancements playerAdvancementsIn) {
        listeners.removeAll(playerAdvancementsIn);
    }

    @Override
    public @NotNull WizardryAdvancementTrigger.TriggerInstance createInstance(@NotNull JsonObject json, @NotNull DeserializationContext context) {
        return new TriggerInstance(ID, json, context);
    }

    public void triggerFor(Player player) {
        if (player instanceof ServerPlayer) {
            final PlayerAdvancements advances = ((ServerPlayer) player).getAdvancements();
            listeners.get(advances).forEach((listener) -> listener.run(advances));
        }
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(ResourceLocation triggerId, JsonObject json, DeserializationContext context) {
            super(triggerId, EntityPredicate.fromJson(json, "player", context));
        }

        public TriggerInstance(ResourceLocation triggerId){
            super(triggerId, ContextAwarePredicate.ANY);
        }
    }
}
