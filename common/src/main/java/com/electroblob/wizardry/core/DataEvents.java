package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.content.data.CastCommandData;
import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.data.WizardData;
import com.electroblob.wizardry.api.content.event.*;
import com.electroblob.wizardry.api.content.util.ImbuementLoader;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.core.platform.Services;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

/**
 * This class is used to save all the custom data events used in Electroblob's Wizardry, normally just including player
 * tick and spell cast events.
 *
 * @see ConjureData
 * @see SpellManagerData
 * @see WizardData
 * @see CastCommandData
 */
public final class DataEvents {
    private static final int CONJURE_CHECK_INTERVAL = 20;

    private DataEvents() {
    }

    // Called externally from the EBEventHelper class
    public static void onMinionTick(EBLivingTick event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (!Services.OBJECT_DATA.isMinion(mob)) return;
        Services.OBJECT_DATA.getMinionData(mob).tick();
    }

    public static void onConjureToss(EBItemTossEvent event) {
        ItemStack stack = event.getStack();
        if (ConjureItemSpell.isSummoned(stack)) {
            ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
            if (data != null && data.isSummoned()) event.setCanceled(true);
        }
    }

    public static void onConjureEntityDeath(EBLivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return; // only players can conjure items so...

        player.getInventory().armor.stream().filter(ConjureItemSpell::isSummoned)
                .forEach(stack -> stack.shrink(stack.getCount()));
        player.getInventory().offhand.stream().filter(ConjureItemSpell::isSummoned)
                .forEach(stack -> stack.shrink(stack.getCount()));
        player.getInventory().items.stream().filter(ConjureItemSpell::isSummoned)
                .forEach(stack -> stack.shrink(stack.getCount()));
    }

    public static void onConjureItemPlaceInContainer(EBItemPlaceInContainerEvent event) {
        ItemStack stack = event.getStack();
        if (ConjureItemSpell.isSummoned(stack) && !(event.getContainer() instanceof Inventory)) event.setCanceled(true);
    }

    public static void onPlayerTick(EBLivingTick event) {
        if (!(event.getEntity() instanceof Player player)) return;

        spellDataTick(player);
        castCommandTick(player);
        conjureItemTick(player);
        imbuementTick(player);
        recentSpells(player);
    }

    private static void spellDataTick(Player player) {
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);
        var spellData = data.getSpellData();

        spellData.replaceAll((k, v) -> k.update(player, v));
        spellData.entrySet().removeIf(entry -> entry.getKey().canPurge(player, entry.getValue()));
    }

    public static void onMinionJoinLevel(EBEntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Mob mob && Services.OBJECT_DATA.isMinion(mob)) {
            Services.OBJECT_DATA.getMinionData(mob).markGoalRestart(true);
        }
    }

    public static void onPlayerInteractMinion(EBPlayerInteractEntityEvent event) {
        if (event.getTarget() instanceof Mob mob && Services.OBJECT_DATA.isMinion(mob)) {
            event.setCanceled(true);
        }
    }

    private static void castCommandTick(Player player) {
        CastCommandData castData = Services.OBJECT_DATA.getCastCommandData(player);
        castData.tick();
    }

    private static void conjureItemTick(Player player) {
        if (player.tickCount % CONJURE_CHECK_INTERVAL != 0) return;

        long currentGameTime = player.level().getGameTime();

        player.getInventory().offhand.stream()
                .filter(ConjureItemSpell::isSummoned)
                .forEach(stack -> checkAndExpireItem(stack, currentGameTime));
        player.getInventory().items.stream()
                .filter(ConjureItemSpell::isSummoned)
                .forEach(stack -> checkAndExpireItem(stack, currentGameTime));
        player.getInventory().armor.stream()
                .filter(ConjureItemSpell::isSummoned)
                .forEach(stack -> checkAndExpireItem(stack, currentGameTime));
    }

    private static void checkAndExpireItem(ItemStack stack, long currentGameTime) {
        ConjureData data = Services.OBJECT_DATA.getConjureData(stack);
        if (data != null && data.hasExpired(currentGameTime)) {
            stack.shrink(1);
            data.setSummoned(false);
        }
    }

    private static void imbuementTick(Player player) {
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);

        Iterator<ImbuementLoader> iterator = data.getImbuementLoaders().iterator();
        while (iterator.hasNext()) {
            ImbuementLoader loader = iterator.next();
            boolean result = loader.hasReachedLimit();
            if (result) {
                data.removeImbuement(loader);
                iterator.remove();
            }
        }
        data.sync();
    }

    private static void recentSpells(Player player) {
        WizardData data = Services.OBJECT_DATA.getWizardData(player);
        if (player.tickCount % 60 == 0) {
            long currentTime = player.level().getGameTime();
            data.removeRecentCasts((entry) -> currentTime - entry.getValue() >= EBConfig.RECENT_SPELL_EXPIRY_TICKS);
        }
    }
}
