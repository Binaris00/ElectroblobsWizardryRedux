package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.content.spell.Element;
import net.minecraft.ChatFormatting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.electroblob.wizardry.setup.registries.Elements.Register.element;

public class Elements {
    private Elements() {}

    public static final Element MAGIC = element("magic", () -> new Element(ChatFormatting.GRAY, "magic"));
    public static final Element FIRE = element("fire", () -> new Element(ChatFormatting.RED, "fire"));
    public static final Element LIGHTNING = element("lightning", () -> new Element(ChatFormatting.DARK_AQUA, "lightning"));
    public static final Element NECROMANCY = element("necromancy", () -> new Element(ChatFormatting.DARK_PURPLE, "necromancy"));
    public static final Element EARTH = element("earth", () -> new Element(ChatFormatting.DARK_GREEN, "earth"));
    public static final Element SORCERY = element("sorcery", () -> new Element(ChatFormatting.GREEN, "sorcery"));
    public static final Element HEALING = element("healing", () -> new Element(ChatFormatting.YELLOW, "healing"));
    public static final Element ICE = element("ice", () -> new Element(ChatFormatting.AQUA, "ice"));

    static void handleRegistration(Consumer<Set<Map.Entry<String, Element>>> handler) {
        handler.accept(Collections.unmodifiableSet(Register.ELEMENTS.entrySet()));
    }

    public static class Register {
        public static Map<String, Element> ELEMENTS = new HashMap<>();

        static Element element(String name, Supplier<Element> elementSupplier) {
            var element = elementSupplier.get();
            ELEMENTS.put(name, element);
            return element;
        }

        private static void init() {}
    }

    static void load() {}
}
