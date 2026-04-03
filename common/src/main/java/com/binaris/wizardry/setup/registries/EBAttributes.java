package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.api.content.util.RegisterFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class EBAttributes {
    static Map<String, DeferredObject<Attribute>> ATTRIBUTES = new HashMap<>();

    public static final DeferredObject<Attribute> CAST_POTENCY = attribute("cast.potency");
    public static final DeferredObject<Attribute> CAST_COST = attribute("cast.cost");
    public static final DeferredObject<Attribute> CAST_CHARGEUP = attribute("cast.chargeup");
    public static final DeferredObject<Attribute> CAST_PROGRESSION = attribute("cast.progression");
    public static final DeferredObject<Attribute> CAST_DURATION = attribute("cast.duration");
    public static final DeferredObject<Attribute> CAST_BLAST = attribute("cast.blast");
    public static final DeferredObject<Attribute> CAST_RANGE = attribute("cast.range");
    public static final DeferredObject<Attribute> CAST_COOLDOWN = attribute("cast.cooldown");

    public static void register(RegisterFunction<Attribute> function) {
        ATTRIBUTES.forEach(((id, attribute) ->
                function.register(BuiltInRegistries.ATTRIBUTE, WizardryMainMod.location(id), attribute.get())));
    }

    public static Collection<DeferredObject<Attribute>> getAttributes() {
        return ATTRIBUTES.values();
    }

    static DeferredObject<Attribute> attribute(String name) {
        return attribute(name, 0, -100, 100, true);
    }

    static DeferredObject<Attribute> attribute(String name, float defaultValue, float minValue, float maxValue, boolean sync) {
        Attribute attribute = new RangedAttribute("attribute.name." + name, defaultValue, minValue, maxValue).setSyncable(sync);
        DeferredObject<Attribute> ret = new DeferredObject<>(() -> attribute);
        ATTRIBUTES.put(name, ret);
        return ret;
    }

    private EBAttributes() {
    }
}