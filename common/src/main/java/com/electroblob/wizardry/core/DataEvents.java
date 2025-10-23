package com.electroblob.wizardry.core;


import com.electroblob.wizardry.api.content.data.CastCommandData;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.world.entity.player.Player;

/**
 * This class is used to save all the custom data events used in Electroblob's Wizardry, normally just including player
 * tick and spell cast events.
 */
public final class DataEvents {
    private DataEvents() {
    }

    public static void onPlayerTick(EBLivingTick event){
        if(!(event.getEntity() instanceof Player player)) return;

        castCommandTick(player);
        conjureItemTick(player);
    }

    private static void castCommandTick(Player player){
        CastCommandData castData = Services.WIZARD_DATA.getCastCommandData(player);
        castData.tick();
    }

    private static void conjureItemTick(Player player){
        player.getInventory().offhand.stream().filter(ConjureItemSpell::isSupportedItem)
                .forEach((s) -> Services.WIZARD_DATA.getConjureData(s).tick());
        player.getInventory().items.stream().filter(ConjureItemSpell::isSupportedItem)
                .forEach((s) -> Services.WIZARD_DATA.getConjureData(s).tick());
        player.getInventory().armor.stream().filter(ConjureItemSpell::isSupportedItem)
                .forEach((s) -> Services.WIZARD_DATA.getConjureData(s).tick());
    }
}
