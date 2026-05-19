package com.binaris.wizardry.core;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility class used for quickly add artifacts effect to items without having to create a whole new effect class.
 *
 * @see IArtifactEffect
 */
public class QuickArtifactEffect implements IArtifactEffect {

    /**
     * Used for artifacts that always modify {@link SpellModifiers} when cast. Loaded in the pre-cast event.
     *
     * @param modifierKey The key of the modifier to modify.
     * @param value       The value to add or multiply to the modifier.
     * @param operation   The operation to apply to the modifier.
     * @return An instance of {@link IArtifactEffect} (used to be registered with the {@link com.binaris.wizardry.api.content.item.ArtifactItem}
     */
    public static IArtifactEffect changeModifiers(String modifierKey, float value, SpellModifiers.Operation operation) {
        return new QuickArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                event.getModifiers().operate(modifierKey, value, operation);
            }
        };
    }

    /**
     * Used for artifacts that changes {@link SpellModifiers} when the player cast a specific spell.
     *
     * @param spell       The spell to check.
     * @param modifierKey The key of the modifier to modify.
     * @param value       The value to add or multiply to the modifier.
     * @param operation   The operation to apply to the modifier.
     * @return An instance of {@link IArtifactEffect} (used to be registered with the {@link com.binaris.wizardry.api.content.item.ArtifactItem}
     */
    public static IArtifactEffect changeModifiersIfCasting(Supplier<Spell> spell, String modifierKey, float value, SpellModifiers.Operation operation) {
        return new QuickArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                if (event.getSpell() == spell.get()) event.getModifiers().operate(modifierKey, value, operation);
            }
        };
    }

    /**
     * Used for artifacts that changes {@link SpellModifiers} when te player cast a spell of the selected spell-type
     *
     * @param type        The spell-type to check.
     * @param modifierKey The key of the modifier to modify.
     * @param value       The value to add or multiply to the modifier.
     * @param operation   The operation to apply to the modifier.
     * @return An instance of {@link IArtifactEffect} (used to be registered with the {@link com.binaris.wizardry.api.content.item.ArtifactItem}
     */
    public static IArtifactEffect changeModifiersIfCastingType(SpellType type, String modifierKey, float value, SpellModifiers.Operation operation) {
        return new QuickArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                if (event.getSpell().getType() == type) event.getModifiers().operate(modifierKey, value, operation);
            }
        };
    }

    /**
     * Used for artifacts that checks if the player attacks a living entity with a wand of the selected element, in the
     * case that's true it will give the affected entity a selected mob effect.
     *
     * @param element The element of the wand to check.
     * @param effect  The mob effect to give to the affected entity. If null, the entity will be set on fire.
     * @param level   The level of the mob effect.
     * @param ticks   The duration of the mob effect.
     * @return An instance of {@link IArtifactEffect} (used to be registered with the {@link com.binaris.wizardry.api.content.item.ArtifactItem}
     */
    public static IArtifactEffect meleeRing(Element element, @Nullable MobEffect effect, int level, int ticks) {
        return new QuickArtifactEffect() {
            @Override
            public void onHurtEntity(Player player, LivingEntity damagedEntity, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
                if (!source.isIndirect() && source.getEntity() instanceof LivingEntity living &&
                        living.getMainHandItem().getItem() instanceof WandItem wand && wand.getElement() == element) {
                    if (effect != null) damagedEntity.addEffect(new MobEffectInstance(effect, ticks, level));
                    else damagedEntity.setSecondsOnFire(ticks / 20);
                }
            }
        };
    }

    /**
     * Based on the cast spell element and if the biome matches the predicate, it will change the {@link SpellModifiers}
     *
     * @param element The element of the wand to check
     * @param predicate The predicate to check the biome
     * @param modifierKey The key of the modifier to modify
     * @param value The value to add or multiply to the modifier
     * @param operation The operation to apply to the modifier
     * @return An instance of {@link IArtifactEffect} (used to be registered with the {@link com.binaris.wizardry.api.content.item.ArtifactItem}
     */
    public static IArtifactEffect changeModifiersByBiomeElement(Element element, Predicate<Holder<Biome>> predicate, String modifierKey, float value, SpellModifiers.Operation operation) {
        return new QuickArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                if (predicate.test(event.getLevel().getBiome(event.getCaster().blockPosition())) && event.getSpell().getElement() == element) {
                    event.getModifiers().operate(modifierKey, value, operation);
                }
            }
        };
    }

    /**
     *  Used for artifacts that changes the incoming damage if the damage type matches the specified one
     *
     *  @param damageType The damage type to check
     *  @param value The value to add or multiply to the modifier
     *  @param operation The operation to apply to the modifier
     *  @return An instance of {@link IArtifactEffect} (used to be registered with the {@link com.binaris.wizardry.api.content.item.ArtifactItem}
     */
    public static IArtifactEffect changeDamageIfSource(@Nullable ResourceKey<DamageType> damageType, float value, SpellModifiers.Operation operation) {
        return new QuickArtifactEffect() {
            @Override
            public void onPlayerHurt(Player player, DamageSource s, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
                if (damageType == null && !EBDamageSources.isMagic(s)) return;
                if (damageType != null && !s.is(damageType)) return;

                switch (operation) {
                    case ADD -> amount.set(amount.get() + value);
                    case MULTIPLY -> amount.set(amount.get() * value);
                    case SUBTRACT -> amount.set(amount.get() - value);
                    case DIVIDE -> amount.set(amount.get() / value);
                    case SET -> amount.set(value);
                }
            }
        };
    }

    public QuickArtifactEffect() {
    }
}
