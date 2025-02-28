package com.electroblob.wizardry.forge.registry;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class EntityRegistryForge {

    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Wizardry.MOD_ID);

    public static void register() {
        EBRegister.registerEntityTypes(
                entityTypes -> entityTypes
                        .forEach((name, entityType) -> ENTITY_TYPES.register(name, entityType))
        );
    }

    private EntityRegistryForge() {}
}
