package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.api.EBLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public final class NBTExtras {
    private NBTExtras() {
    }

    public static void storeTagSafely(CompoundTag compound, String key, Tag tag) {
        if (compound == tag || deepContains(tag, compound)) {
            EBLogger.error("Cannot store tag of type %s under key '{}' as it would result in a circular reference! Please report this (including your full log) to wizardry's issue tracker.", getTagTypeName(tag.getId()), key);
        } else {
            compound.put(key, tag);
        }
    }

    public static boolean deepContains(Tag toSearch, Tag searchFor) {
        if (toSearch instanceof CompoundTag) {
            for (String subKey : ((CompoundTag) toSearch).getAllKeys()) {
                Tag subTag = ((CompoundTag) toSearch).get(subKey);
                if (subTag == searchFor || deepContains(subTag, searchFor)) return true;
            }

        } else if (toSearch instanceof ListTag) {
            for (Tag subTag : (ListTag) toSearch) {
                if (subTag == searchFor || deepContains(subTag, searchFor)) return true;
            }
        }

        return false;
    }

    public static String getTagTypeName(int id) {
        return switch (id) {
            case 0 -> "TAG_End";
            case 1 -> "TAG_Byte";
            case 2 -> "TAG_Short";
            case 3 -> "TAG_Int";
            case 4 -> "TAG_Long";
            case 5 -> "TAG_Float";
            case 6 -> "TAG_Double";
            case 7 -> "TAG_Byte_Array";
            case 8 -> "TAG_String";
            case 9 -> "TAG_List";
            case 10 -> "TAG_Compound";
            case 11 -> "TAG_Int_Array";
            case 12 -> "TAG_Long_Array";
            case 99 -> "Any Numeric Tag";
            default -> "UNKNOWN";
        };
    }

    public static <E, T extends Tag> ListTag listToTag(Collection<E> list, Function<E, T> mapper) {
        ListTag tagList = new ListTag();

        for (E element : list) {
            tagList.add(mapper.apply(element));
        }

        return tagList;
    }

    @SuppressWarnings("unchecked")
    public static <E, T extends Tag> Collection<E> tagToList(ListTag tagList, Function<T, E> function) {
        Collection<E> list = new ArrayList<>();

        ListTag tagList2 = tagList.copy();

        while (!tagList2.isEmpty()) {
            Tag tag = tagList2.remove(0);

            try {
                list.add(function.apply((T) tag));
            } catch (ClassCastException e) {
                EBLogger.error(
                        "Error when reading list from NBT: unexpected tag type " + getTagTypeName(tag.getId()), e);
            }
        }

        return list;
    }
}
