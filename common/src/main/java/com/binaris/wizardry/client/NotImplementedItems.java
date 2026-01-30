package com.binaris.wizardry.client;

import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.world.item.Item;

import java.util.List;

public final class NotImplementedItems {
    static List<Item> ITEMS = List.of(
            EBItems.AMULET_AUTO_SHIELD.get(),
            EBItems.CHARM_ABSEILING.get(),
            EBItems.CHARM_BLACK_HOLE.get(),
            EBItems.CHARM_LAVA_WALKING.get(),
            EBItems.CHARM_LIGHT.get(),
            EBItems.CHARM_MINION_HEALTH.get(),
            EBItems.CHARM_SILK_TOUCH.get(),
            EBItems.CHARM_SIXTH_SENSE.get(),
            EBItems.CHARM_STORM.get(),
            EBItems.CHARM_TRANSPORTATION.get(),
            EBItems.CHARM_UNDEAD_HELMETS.get(),
            EBItems.AMULET_ANCHORING.get(),
            EBItems.AMULET_RECOVERY.get(),
            EBItems.AMULET_RESURRECTION.get(),
            EBItems.RING_DEFENDER.get(),
            EBItems.RING_DISINTEGRATION.get(),
            EBItems.RING_HAMMER.get(),
            EBItems.RING_INTERDICTION.get(),
            EBItems.RING_MIND_CONTROL.get(),
            EBItems.RING_STORMCLOUD.get(),
            EBItems.WIZARD_HANDBOOK.get(),
            EBItems.LIGHTNING_HAMMER.get(),
            EBItems.AMULET_FIRE_CLOAKING.get(),
            EBItems.AMULET_BANISHING.get(),
            EBItems.CHARM_AUTO_SMELT.get(),
            EBItems.RING_SOULBINDING.get()
    );

    public static void init() {
    }

    public static boolean notImplemented(Item item) {
        return ITEMS.contains(item);
    }

    private NotImplementedItems() {
    }
}
