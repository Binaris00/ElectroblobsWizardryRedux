package com.electroblob.wizardry.api.content.spell.properties;


import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.Tier;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.registry.ElementRegistry;
import com.electroblob.wizardry.core.registry.TierRegistry;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Tiers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpellProperties {
    private final List<SpellProperty<?>> properties;

    private SpellProperties(List<SpellProperty<?>> properties) {
        this.properties = properties;
    }

    public boolean isEmpty() {
        return this.properties.isEmpty();
    }

    public static SpellProperties empty() {
        return new SpellProperties(new ArrayList<>());
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(SpellProperty<T> property) {
        for (SpellProperty<?> prop : properties) {
            if(prop.equals(property)) {
                EBLogger.debug("Getting Spell property... Id: %s Default: %s Value: %s", prop.identifier, prop.defaultValue, prop.value);
                return (T) prop.get();
            }
        }
        return property.getDefaultValue();
    }

    // Spell Base properties helpers

    public int getCooldown(){
        return get(DefaultProperties.COOLDOWN);
    }

    public int getCost(){
        return get(DefaultProperties.COST);
    }

    public int getCharge(){
        return get(DefaultProperties.CHARGE);
    }

    public SpellType getType(){
        String type = get(DefaultProperties.SPELL_TYPE);
        return SpellType.fromName(type);
    }

    public Tier getTier(){
        String tier = get(DefaultProperties.TIER);
        for(Map.Entry<ResourceKey<Tier>, Tier> t : TierRegistry.entrySet()){
            if(t.getValue().getUnlocalisedName().toString().equals(tier)) return t.getValue();
        }
        return Tiers.NOVICE; // Default
    }

    public Element getElement(){
        String element = get(DefaultProperties.ELEMENT);
        for(Map.Entry<ResourceKey<Element>, Element> e : ElementRegistry.entrySet()){
            if(e.getValue().getLocation().toString().equals(element)) {
                return e.getValue();
            }
        }
        return Elements.MAGIC; // Default
    }

    public SpellAction getAction(){
        String action = get(DefaultProperties.SPELL_ACTION);
        SpellAction spellAction = SpellAction.get(ResourceLocation.tryParse(action));
        return spellAction != null ? spellAction : SpellAction.NONE;
    }

    public static class Builder {
        private boolean isEmpty = true;
        private final List<SpellProperty<?>> builder = new ArrayList<>();

        public <T> Builder assignBaseProperties(Tier tier, Element element, SpellType type, SpellAction action, int cost, int charge, int cooldown) {
            add(DefaultProperties.TIER, tier.getUnlocalisedName().toString());
            add(DefaultProperties.ELEMENT, element.getLocation().toString());
            add(DefaultProperties.SPELL_TYPE, type.getUnlocalisedName());
            add(DefaultProperties.SPELL_ACTION, action.location.toString());
            add(DefaultProperties.COST, cost);
            add(DefaultProperties.COOLDOWN, cooldown);
            add(DefaultProperties.CHARGE, charge);

            return this;
        }

        public <T> Builder add(SpellProperty<T> property) {
            if(property != null){
                SpellProperty<T> cloned = property.copyOf();
                builder.add(cloned);

                if(isEmpty) isEmpty = false;
            }
            return this;
        }

        public <T> Builder add(SpellProperty<T> property, T defaultValue) {
            if(property != null){
                SpellProperty<T> cloned = property.copyOf();
                cloned.defaultValue(defaultValue);
                builder.add(cloned);
                if(isEmpty) isEmpty = false;
            }
            return this;
        }

        public SpellProperties build() {
            return new SpellProperties(new ArrayList<>(builder));
        }

        private Builder() {}
    }

}
