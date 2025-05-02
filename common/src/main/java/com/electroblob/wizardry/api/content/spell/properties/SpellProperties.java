package com.electroblob.wizardry.api.content.spell.properties;


import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

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

    public SpellTier getTier(){
        String s = get(DefaultProperties.TIER);
        for(SpellTier tier : Services.REGISTRY_UTIL.getTiers()){
            if(tier.getLocation().toString().equals(s)) return tier;
        }
        return SpellTiers.NOVICE; // Default
    }

    public Element getElement(){
        String s = get(DefaultProperties.ELEMENT);
        for(Element element : Services.REGISTRY_UTIL.getElements()){
            if(element.getLocation().toString().equals(s)) {
                return element;
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

        public <T> Builder assignBaseProperties(SpellTier tier, Element element, SpellType type, SpellAction action, int cost, int charge, int cooldown) {
            add(DefaultProperties.TIER, tier.getLocation().toString());
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
