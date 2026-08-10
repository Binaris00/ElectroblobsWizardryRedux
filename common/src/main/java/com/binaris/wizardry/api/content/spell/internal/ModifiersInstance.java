package com.binaris.wizardry.api.content.spell.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class ModifiersInstance {
    private float baseValue = 1.0f;
    private final List<SpellModifiers.ModifierEntry> modifiers = new ArrayList<>();

    public void setBaseValue(float value) {
        this.baseValue = value;
    }

    public void addModifier(SpellModifiers.Operation op, float amount) {
        modifiers.add(new SpellModifiers.ModifierEntry(op, amount));
    }

    public float get() {
        float v = baseValue;
        for (var e : modifiers)
            switch (e.operation()) {
                case ADDITION -> v += e.amount();
                case MULTIPLY_BASE -> v += baseValue * e.amount();
                case MULTIPLY_TOTAL -> v *= 1.0f + e.amount();
            }
        return v;
    }

    public float get(float value) {
        return value * get();
    }

    // ModifiersInstance
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Base", baseValue);
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
