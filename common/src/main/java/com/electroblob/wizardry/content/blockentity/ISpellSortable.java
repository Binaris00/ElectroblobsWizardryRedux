package com.electroblob.wizardry.content.blockentity;

import com.electroblob.wizardry.api.content.spell.Spell;

import java.util.Comparator;

public interface ISpellSortable {
    SortType getSortType();

    boolean isSortDescending();

    enum SortType {
        TIER("tier", null),
        ELEMENT("element", null),
        ALPHABETICAL("alphabetical", null);

        public String name;
        public Comparator<? super Spell> comparator;

        SortType(String name, Comparator<? super Spell> comparator) {
            this.name = name;
            this.comparator = comparator;
        }
    }
}
