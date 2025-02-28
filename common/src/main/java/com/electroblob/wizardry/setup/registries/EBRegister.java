package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.common.DeferredObject;
import com.electroblob.wizardry.api.common.spell.Spell;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class EBRegister {

    public static void registerItems(Consumer<Set<Map.Entry<String, DeferredObject<? extends Item>>>> handler) {
        EBItems.handleRegistration(handler);
    }

    public static void registerSpells(Consumer<Set<Map.Entry<String, Spell>>> handler) {
        Spells.handleRegistration(handler);
    }

    public static void registerCreativeTabs(Consumer<Map<String, Supplier<CreativeModeTab>>> handler) {
        EBCreativeTabs.handleRegistration(handler);
    }

    public static void registerBlocks(Consumer<Set<Map.Entry<String, DeferredObject<Block>>>> handler) {
        EBBlocks.handleRegistration(handler);
    }

    public static void registerEntityTypes(Consumer<Map<String, DeferredObject<EntityType<? extends Entity>>>> handler) {
        EBEntities.handleRegistration(handler);
    }

    public static void registerMobEffects(Consumer<Map<String, DeferredObject<MobEffect>>> handler) {
        EBMobEffects.handleRegistration(handler);
    }

    private static boolean registrationFinished;

    // TODO: Use this by making it invoke from finish server/client loading (Mixin required)
    public static void finish() {
        if(!registrationFinished) {

            // Free memory from these collections
            EBItems.Register.ITEMS = null;
            EBCreativeTabs.Register.CREATIVE_MODE_TABS = null;
            Spells.Register.SPELLS = null;
            EBEntities.Register.ENTITY_TYPES = null;
            EBMobEffects.mobEffects = null;

            registrationFinished = true;
        }
    }

    public static boolean isRegistrationFinished() {
        return registrationFinished;
    }

    private EBRegister() {}
}
