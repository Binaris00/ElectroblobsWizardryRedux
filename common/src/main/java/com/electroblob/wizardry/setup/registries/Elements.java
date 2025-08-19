package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class Elements {
    static Map<String, Element> ELEMENTS = new HashMap<>();
    private Elements() {}

    public static final Element MAGIC = element("magic", () -> new Element(ChatFormatting.GRAY));
    public static final Element FIRE = element("fire", () -> new Element(ChatFormatting.RED));
    public static final Element LIGHTNING = element("lightning", () -> new Element(ChatFormatting.DARK_AQUA));
    public static final Element NECROMANCY = element("necromancy", () -> new Element(ChatFormatting.DARK_PURPLE));
    public static final Element EARTH = element("earth", () -> new Element(ChatFormatting.DARK_GREEN));
    public static final Element SORCERY = element("sorcery", () -> new Element(ChatFormatting.GREEN));
    public static final Element HEALING = element("healing", () -> new Element(ChatFormatting.YELLOW));
    public static final Element ICE = element("ice", () -> new Element(ChatFormatting.AQUA));

    // ======= Registry =======
    // Thx forge
    public static void registerNull(RegisterFunction<Element> function){
        register(null, function);
    }

    @SuppressWarnings("unchecked")
    public static void register(Registry<?> registry, RegisterFunction<Element> function){
        ELEMENTS.forEach(((id, element) ->
                function.register((Registry<Element>) registry, WizardryMainMod.location(id), element)));
    }


    // ======= Helpers =======
    static Element element(String name, Supplier<Element> elementSupplier) {
        var element = elementSupplier.get();
        ELEMENTS.put(name, element);
        return element;
    }
}
