package com.electroblob.wizardry.datagen.help;

import net.minecraft.world.item.Item;

public record ElementData(Item dust, ArmorData normal, ArmorData sage, ArmorData warlock,
                          ArmorData battleMage) {
}
