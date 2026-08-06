package com.binaris.wizardry.core;

import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.api.content.data.WizardData;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// Central utility that determines whether two entities are allies based on ownership
/// chains, [WizardData], and minion ownership, plus blocks friendly-fire damage events.
///
/// The core concept is a recursive ownership check: a pet's owner's allies are also
/// allies of the pet, and minion owners propagate ally status the same way. Player-to-player
/// ally status is stored in each player's {@code WizardData} and persisted to NBT.
/// The class also provides {@code isValidTarget} used by projectile and construct
/// entities to prevent friendly fire, and {@code onLivingHurt} to cancel magic damage
/// between allies based on server config.
public final class AllyDesignation {
    /// Checks whether {@code possibleAlly} is considered an ally of {@code allyOf}.
    ///
    /// Recursively follows ownership chains: if the entity is an {@code OwnableEntity},
    /// its owner is checked first. Then checks player-to-player ally data and whether the
    /// target is an owned entity whose owner is allied.
    ///
    /// @param allyOf the entity to check the ally relationship from.
    /// @param possibleAlly the entity to check against.
    /// @return true if the entities are allied through ownership or ally data.
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

    /// Checks whether the given mob is a minion whose owner is allied with {@code allyOf}.
    ///
    /// Reads {@code MinionData} from platform services and falls back to the recursive
    /// {@code isAllied} check on the minion's owner. Returns false if the mob has no
    /// minion data or no owner.
    ///
    /// @param allyOf the entity to check the ally relationship from.
    /// @param possibleAlly the mob to check.
    /// @return true if the mob is a minion and its owner is allied.
    public static boolean isMinionAlly(LivingEntity allyOf, Mob possibleAlly) {
        MinionData data = Services.OBJECT_DATA.getMinionData(possibleAlly);
        if (data == null || data.getOwner() == null) return false;

        LivingEntity owner = data.getOwner();
        if (owner == allyOf) return true;
        return isAllied(allyOf, owner);
    }

    /// Determines whether {@code attacker} can validly target {@code target} without
    /// hitting an ally.
    ///
    /// Returns false if the target is null or the attacker itself. Recursively follows
    /// ownership: if the attacker is an owned entity, the owner's validity is checked
    /// first. Respects the {@code PASSIVE_MOBS_ARE_ALLIES} config for passive creatures.
    /// For player attackers, checks player ally data and owned entity ownership against
    /// the attacker's wizard data.
    ///
    /// @param attacker the attacking entity, may be null.
    /// @param target the targeted entity, may be null.
    /// @return true if targeting is valid (not an ally), false if it would be friendly fire.
    public static boolean isValidTarget(@Nullable Entity attacker, @Nullable Entity target) {
        if (target == null || target == attacker) return false;
        if (attacker == null) return true;
        if (attacker instanceof OwnableEntity ownable && !isValidTarget(ownable.getOwner(), target)) return false;
        if (EBServerConfig.PASSIVE_MOBS_ARE_ALLIES.get() && target.getType().getCategory().isFriendly()) return false;

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

    /// Cancels magic damage events when the attacker and damaged entity are allies.
    ///
    /// Registered via {@code EBEventHelper} as a listener for {@code EBLivingHurtEvent}.
    /// Only applies when the direct damage source is a player using magic damage. Respects
    /// two config options: {@code BLOCK_PLAYERS_ALLIES_DAMAGE} (player vs player ally)
    /// and {@code BLOCK_OWNED_ALLIES_DAMAGE} (player vs owned entity ally).
    ///
    /// @param event the living hurt event to potentially cancel.
    public static void onLivingHurt(EBLivingHurtEvent event) {
        if (event.getSource() == null) return;

        Entity directEntity = event.getSource().getDirectEntity();
        if (!(directEntity instanceof Player playerDirect) || !EBDamageSources.isMagic(event.getSource())) return;

        Entity damagedEntity = event.getDamagedEntity();
        if (damagedEntity instanceof Player playerSource) {
            if (EBServerConfig.BLOCK_PLAYERS_ALLIES_DAMAGE.get() && isPlayerAlly(playerDirect, playerSource)) event.setCanceled(true);
        } else if (EBServerConfig.BLOCK_OWNED_ALLIES_DAMAGE.get() && isAllied(playerDirect, damagedEntity)) {
            event.setCanceled(true);
        }
    }

    /// Checks whether two players are allies based on the first player's {@code WizardData}.
    ///
    /// @param allyOf the player whose ally list is checked.
    /// @param possibleAlly the player to look for in the ally list.
    /// @return true if {@code possibleAlly} is in {@code allyOf}'s ally list.
    public static boolean isPlayerAlly(Player allyOf, Player possibleAlly) {
        WizardData data = Services.OBJECT_DATA.getWizardData(allyOf);
        return data.isPlayerAlly(possibleAlly);
    }

    /// Checks whether a player considers another player (by UUID) an ally.
    ///
    /// @param allyOf the player whose ally list is checked.
    /// @param possibleAlly the UUID to look for in the ally list.
    /// @return true if the UUID is in {@code allyOf}'s ally list.
    public static boolean isPlayerAlly(Player allyOf, UUID possibleAlly) {
        WizardData data = Services.OBJECT_DATA.getWizardData(allyOf);
        return data.isPlayerAlly(possibleAlly);
    }

    /// Checks whether a player is allied with the owner of an {@code OwnableEntity}.
    ///
    /// Retrieves the owner from the ownable entity. If the owner is a player, delegates
    /// to {@code isPlayerAlly(Player, Player)}; otherwise delegates to the UUID-based
    /// {@code isPlayerAlly(Player, UUID)}.
    ///
    /// @param allyOf the player to check against.
    /// @param ownable the ownable entity whose owner is checked.
    /// @return true if the player is allied with the entity's owner.
    public static boolean isOwnerAlly(Player allyOf, OwnableEntity ownable) {
        WizardData data = Services.OBJECT_DATA.getWizardData(allyOf);
        Entity owner = ownable.getOwner();
        return owner instanceof Player target ? data.isPlayerAlly(target) : data.isPlayerAlly(ownable.getOwnerUUID());
    }

    private AllyDesignation() {
    }
}