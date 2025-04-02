package com.electroblob.wizardry.api.content.util;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public final class Util {

    public static CompoundTag compoundTagFrom(CompoundTag parent, String key, Consumer<CompoundTag> action) {
        CompoundTag nestedTag = parent.contains(key) ? parent.getCompound(key) : new CompoundTag();
        action.accept(nestedTag);
        parent.put(key, nestedTag);
        return nestedTag;
    }


    public static WrapperCompoundTag wrapperTag(CompoundTag tag) {
        return new WrapperCompoundTag(tag);
    }

    public static class WrapperCompoundTag {
        private final CompoundTag tag;

        public CompoundTag put(String string, CompoundTag tag) {
            this.tag.put(string, tag);
            return this.tag;
        }

        private WrapperCompoundTag(CompoundTag tag) {
            this.tag = tag;
        }
    }
    private Util() {}
}
