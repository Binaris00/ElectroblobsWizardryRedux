package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.util.RegisterFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class Elements {
    static Map<String, Element> ELEMENTS = new HashMap<>();
    public static final Element MAGIC = element("magic", () -> new Element(ChatFormatting.GRAY, 0xe4c7cd, 0xfeffbe, 0x9d2cf3));
    public static final Element FIRE = element("fire", () -> new Element(ChatFormatting.RED, 0xff9600, 0xfffe67, 0xd02700));
    public static final Element LIGHTNING = element("lightning", () -> new Element(ChatFormatting.DARK_AQUA, 0x409ee1, 0xf5f0ff, 0x225474));
    public static final Element NECROMANCY = element("necromancy", () -> new Element(ChatFormatting.DARK_PURPLE, 0xa811ce, 0xf575f5, 0x382366));
    public static final Element EARTH = element("earth", () -> new Element(ChatFormatting.DARK_GREEN, 0xa8f408, 0xc8ffb2, 0x795c28));
    public static final Element SORCERY = element("sorcery", () -> new Element(ChatFormatting.GREEN, 0x56e8e3, 0xe8fcfc, 0x16a64d));
    public static final Element HEALING = element("healing", () -> new Element(ChatFormatting.YELLOW, 0xfff69e, 0xfffff6, 0xa18200));
    public static final Element ICE = element("ice", () -> new Element(ChatFormatting.AQUA, 0xa3e8f4, 0xe9f9fc, 0x138397));

    private Elements() {
    }

    // ======= Registry =======
    // Thx forge
    public static void registerNull(RegisterFunction<Element> function) {
        register(null, function);
    }

    @SuppressWarnings("unchecked")
    public static void register(Registry<?> registry, RegisterFunction<Element> function) {
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
