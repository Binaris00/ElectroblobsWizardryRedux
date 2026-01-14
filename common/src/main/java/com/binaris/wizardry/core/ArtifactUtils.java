package com.binaris.wizardry.core;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Various utility methods for use by artefacts. These are all static methods, so there is no need to ever instantiate
 * this class. Normally we use these methods to make more good-looking {@link QuickArtefactEffect} lambdas and avoid
 * creating loads of classes with the same logic (specially referred to predicates and similar).
 */
public final class ArtifactUtils {
    private ArtifactUtils() {
    }

    /**
     * Check if the source isn't melee (like a projectile, explosion or something like that), the direct entity isn't
     * null and if it's a living one, and finally check the main hand for seeing the wand element. Quite weird but this
     * logic is shared for a lot of artifacts
     */
    public static boolean meleeRing(DamageSource source, Element element) {
        return !source.isIndirect() && source.getEntity() instanceof LivingEntity living && living.getMainHandItem().getItem() instanceof WandItem wand && wand.getElement() == element;
    }

    /**
     * Helper method that scans through all wands on the given player's hotbar and offhand and casts the given spell if
     * it is bound to any of them. This is a useful code pattern for artefact effects.
     */
    public static boolean findMatchingWandAndCast(Player player, Spell spell) {
        return findMatchingWandAndExecute(player, spell, wand -> {
            SpellModifiers modifiers = new SpellModifiers();
            if (((ISpellCastingItem) wand.getItem()).canCast(wand, spell, new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, modifiers))) {
                ((ISpellCastingItem) wand.getItem()).cast(wand, spell, new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, modifiers));
            }
        });
    }

    /**
     * Helper method that scans through all wands on the given player's hotbar and offhand and executes the given action
     * if any of them have the given spell bound to them. This is a useful code pattern for artefact effects.
     */
    public static boolean findMatchingWandAndExecute(Player player, Spell spell, Consumer<? super ItemStack> action) {
        List<ItemStack> hotbar = InventoryUtil.getHotBarAndOffhand(player);

        Optional<ItemStack> stack = hotbar.stream().filter(s -> s.getItem() instanceof ISpellCastingItem && Arrays.asList(((ISpellCastingItem) s.getItem()).getSpells(s)).contains(spell)).findFirst();

        stack.ifPresent(action);
        return stack.isPresent();
    }

    public static void handleLightningEffect(Entity player, LivingEntity target, EBLivingHurtEvent event) {
        if (player.level().isClientSide) {
            ParticleBuilder.create(EBParticles.LIGHTNING).entity(event.getDamagedEntity()).pos(0, event.getDamagedEntity().getBbHeight() / 2, 0).target(target).spawn(player.level());
            ParticleBuilder.spawnShockParticles(player.level(), target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());
        }

        target.hurt(MagicDamageSource.causeDirectMagicDamage(player, EBDamageSources.SHOCK), Spells.STATIC_AURA.property(DefaultProperties.DAMAGE));
        target.playSound(EBSounds.SPELL_STATIC_AURA_RETALIATE.get(), 1.0F, player.level().random.nextFloat() * 0.4F + 1.5F);
    }
}
