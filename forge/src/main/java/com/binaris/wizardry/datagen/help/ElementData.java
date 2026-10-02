package com.binaris.wizardry.datagen.help;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public record ElementData(TagKey<Item> elementalDust, ArmorData normal, ArmorData sage, ArmorData warlock, ArmorData battleMage) {}
