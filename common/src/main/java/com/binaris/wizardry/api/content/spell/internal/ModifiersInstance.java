package com.binaris.wizardry.api.content.spell.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModifiersInstance {
    private boolean hasBase;
    private float baseValue;
    private final List<SpellModifiers.ModifierEntry> modifiers = new ArrayList<>();

    public void setBaseValue(float value) {
        hasBase = true;
        baseValue = value;
    }

    public void addModifier(SpellModifiers.Operation op, float amount) {
        modifiers.add(new SpellModifiers.ModifierEntry(op, amount));
    }

    public float get(float value) {
        float v = hasBase ? baseValue : value;

        List<SpellModifiers.ModifierEntry> attributes = modifiers.stream().sorted(Comparator.comparingInt(e -> e.operation().ordinal())).toList();

        for (var e : attributes)
            switch (e.operation()) {
                case ADDITION -> v += e.amount();
                case MULTIPLY_BASE, MULTIPLY_TOTAL -> v *= e.amount();
            }
        return v;
    }

    void merge(ModifiersInstance other) {
        if (other == null) return;
        if (other.hasBase && !hasBase) {
            hasBase = true;
            baseValue = other.baseValue;
        }
        modifiers.addAll(other.modifiers);
    }

    void sortByOperation() {
        modifiers.sort(Comparator.comparingInt(e -> e.operation().ordinal()));
    }

    // ModifiersInstance
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("HasBase", hasBase);
        if (hasBase) tag.putFloat("Base", baseValue);
        if (!modifiers.isEmpty()) {
            ListTag list = new ListTag();
            for (SpellModifiers.ModifierEntry e : modifiers) {
                CompoundTag entry = new CompoundTag();
                entry.putInt("Op", e.operation().ordinal());
                entry.putFloat("Amount", e.amount());
                list.add(entry);
            }
            tag.put("Modifiers", list);
        }
        return tag;
    }

    public static ModifiersInstance load(CompoundTag tag) {
        ModifiersInstance inst = new ModifiersInstance();
        inst.hasBase = tag.getBoolean("HasBase");
        inst.baseValue = tag.getFloat("Base");
        if (tag.contains("Modifiers")) {
            ListTag list = tag.getList("Modifiers", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag e = list.getCompound(i);
                inst.modifiers.add(new SpellModifiers.ModifierEntry(
                        SpellModifiers.Operation.values()[e.getInt("Op")], e.getFloat("Amount")));
            }
        }
        return inst;
    }
}
