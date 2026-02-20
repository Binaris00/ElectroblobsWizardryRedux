package com.binaris.wizardry.core;

import com.binaris.wizardry.api.content.data.WizardData;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.core.config.EBConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class AllyDesignation {
    private AllyDesignation() {
    }

    /**
     * Covers both {@link AllyDesignation#isPlayerAlly(Player, Player)} and {@link AllyDesignation#isOwnerAlly(Player, OwnableEntity)},
     * returning true if the second entity is either owned by the first entity, an ally of the first entity, or owned by
     * an ally of the first entity. This is generally used to determine targets for healing or other group buffs.
     */
    public static boolean isAllied(LivingEntity allyOf, Entity possibleAlly) {
        if (allyOf instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (owner instanceof LivingEntity livingOwner && (owner == possibleAlly || isAllied(livingOwner, possibleAlly)))
                return true;
        }

        if (allyOf instanceof Player allyPlayer && possibleAlly instanceof Player possibleAllyPlayer && isPlayerAlly(allyPlayer, possibleAllyPlayer)) {
            return true;
        }

        if (possibleAlly instanceof OwnableEntity pet) {
            if (pet.getOwner() == allyOf) return true;
            return allyOf instanceof Player playerAlly && isOwnerAlly(playerAlly, pet);
        }

        return false;
    }

    /**
     * Returns whether the given target can be damaged by the given attacker. It is up to the caller of this method to
     * work out what this means; it doesn't necessarily mean the target is completely immune (for example, revenge
     * targeting might reasonably bypass this). This method is intended for use where the damage is indirect and/or
     * unavoidable, such as from spells or environmental hazards.
     */
    public static boolean isValidTarget(@Nullable Entity attacker, @Nullable Entity target) {
        if (target == null || target == attacker) return false;
        if (attacker == null) return true;
        if (attacker instanceof OwnableEntity ownable && !isValidTarget(ownable.getOwner(), target)) return false;
        if (EBConfig.PASSIVE_MOBS_ARE_ALLIES.get() && target.getType().getCategory().isFriendly()) return false;

        if (target instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (owner == attacker || (attacker instanceof LivingEntity living && living.getLastAttacker() != owner)) {
                return false;
            }
        }

        if (attacker instanceof Player attackerPlayer) {
            if (target instanceof Player playerTarget)
                return !Services.OBJECT_DATA.getWizardData(attackerPlayer).isPlayerAlly(playerTarget);
            else
                return !(target instanceof OwnableEntity ownable) || !isOwnerAlly(attackerPlayer, ownable);
        }

        return true;
    }

    /**
     * Called whenever an entity is hurt. Cancels the event if the source is magic damage from a player and the damaged
     * entity is an ally of that player, according to the config settings. This includes owned entities.
     */
    public static void onLivingHurt(EBLivingHurtEvent event) {
        if (event.getSource() == null) return;

        Entity directEntity = event.getSource().getDirectEntity();
        if (!(directEntity instanceof Player playerDirect) || !EBDamageSources.isMagic(event.getSource())) return;

        Entity damagedEntity = event.getDamagedEntity();
        if (damagedEntity instanceof Player playerSource) {
            if (EBConfig.BLOCK_PLAYERS_ALLIES_DAMAGE.get() && isPlayerAlly(playerDirect, playerSource)) event.setCanceled(true);
        } else if (EBConfig.BLOCK_OWNED_ALLIES_DAMAGE.get() && isAllied(playerDirect, damagedEntity)) {
            event.setCanceled(true);
        }
    }

    /**
     * Helper method for testing if two players are allies of each other according to the given player's data.
     */
    public static boolean isPlayerAlly(Player allyOf, Player possibleAlly) {
        WizardData data = Services.OBJECT_DATA.getWizardData(allyOf);
        return data.isPlayerAlly(possibleAlly);
    }

    /**
     * Helper method for testing if the given {@link OwnableEntity}'s owner is an ally of the
     * given player. This works even when the owner is not logged in.
     */
    public static boolean isOwnerAlly(Player allyOf, OwnableEntity ownable) {
        WizardData data = Services.OBJECT_DATA.getWizardData(allyOf);
        Entity owner = ownable.getOwner();
        return owner instanceof Player target ? data.isPlayerAlly(target) : data.isPlayerAlly(ownable.getOwnerUUID());
    }
}
