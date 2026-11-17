package com.binaris.wizardry.content.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class PocketWorkbenchMenu extends CraftingMenu {
    public PocketWorkbenchMenu(int containerId, Inventory playerInventory) {
        super(containerId, playerInventory,
                ContainerLevelAccess.create(playerInventory.player.level(), playerInventory.player.blockPosition()));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
