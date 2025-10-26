package com.electroblob.wizardry.core;


import com.electroblob.wizardry.api.content.data.CastCommandData;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.event.EBEntityJoinLevelEvent;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.api.content.event.EBPlayerInteractEntityEvent;
import com.electroblob.wizardry.api.content.util.ImbuementLoader;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.Iterator;

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
        imbuementTick(player);
    }

    public static void onMinionTick(EBLivingTick event){
        if(!(event.getEntity() instanceof Mob mob)) return;
        if(!Services.OBJECT_DATA.isMinion(mob)) return;
        Services.OBJECT_DATA.getMinionData(mob).tick();
    }

    public static void onMinionJoinLevel(EBEntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Mob mob && Services.OBJECT_DATA.isMinion(mob))
            Services.OBJECT_DATA.getMinionData(mob).updateGoals();
    }

    public static void onPlayerInteractMinion(EBPlayerInteractEntityEvent event) {
        if (event.getTarget() instanceof Mob mob && Services.OBJECT_DATA.isMinion(mob)) {
            event.setCanceled(true);
        }
    }

    private static void castCommandTick(Player player){
        CastCommandData castData = Services.OBJECT_DATA.getCastCommandData(player);
        castData.tick();
    }

    private static void conjureItemTick(Player player){
        player.getInventory().offhand.stream().filter(ConjureItemSpell::isSupportedItem)
                .forEach((s) -> Services.OBJECT_DATA.getConjureData(s).tick());
        player.getInventory().items.stream().filter(ConjureItemSpell::isSupportedItem)
                .forEach((s) -> Services.OBJECT_DATA.getConjureData(s).tick());
        player.getInventory().armor.stream().filter(ConjureItemSpell::isSupportedItem)
                .forEach((s) -> Services.OBJECT_DATA.getConjureData(s).tick());
    }

    private static void imbuementTick(Player player){
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);

        Iterator<ImbuementLoader> iterator = data.getImbuementLoaders().iterator();
        while(iterator.hasNext()) {
            ImbuementLoader loader = iterator.next();
            boolean result = loader.hasReachedLimit();
            if(result){
                data.removeImbuement(loader);
                iterator.remove();
            }
        }
        data.sync();
    }
}
