package com.electroblob.wizardry.setup.registries;


import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.block.entity.VanishingCobwebBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EBBlockEntities {
    static Map<String, DeferredObject<BlockEntityType<BlockEntity>>> BLOCK_ENTITIES = new HashMap<>();
    private EBBlockEntities() {}


    public static final DeferredObject<BlockEntityType<VanishingCobwebBlockEntity>> VANISHING_COBWEB = blockEntity(
            "vanishing_cobweb", () -> BlockEntityType.Builder.of(VanishingCobwebBlockEntity::new, EBBlocks.VANISHING_COBWEB.get()).build(null)
    );

    // ======= Registry =======
    public static void register(RegisterFunction<BlockEntityType<?>> function){
        BLOCK_ENTITIES.forEach((name, blockEntityType) ->
                function.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, WizardryMainMod.location(name), blockEntityType.get()));
    }

    // ======= Helpers =======
    @SuppressWarnings("unchecked")
    static <V extends BlockEntity, T extends BlockEntityType<V>> DeferredObject<T> blockEntity(String name, Supplier<T> beSupplier) {
        DeferredObject<T> ret = new DeferredObject<>(beSupplier);
        BLOCK_ENTITIES.put(name, (DeferredObject<BlockEntityType<BlockEntity>>) ret);
        return ret;
    }
}
