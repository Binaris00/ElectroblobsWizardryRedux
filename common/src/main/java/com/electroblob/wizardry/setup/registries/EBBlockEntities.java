package com.electroblob.wizardry.setup.registries;


import com.electroblob.wizardry.api.common.DeferredObject;
import com.electroblob.wizardry.common.content.block.entity.VanishingCobwebBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class EBBlockEntities {

    public static final DeferredObject<BlockEntityType<VanishingCobwebBlockEntity>> ARCANE_WORKBENCH_BE;

    static {
        ARCANE_WORKBENCH_BE = Register.blockEntity(
                "arcane_workbench_be", () -> BlockEntityType.Builder.of(VanishingCobwebBlockEntity::new, EBBlocks.VANISHING_COBWEB.get()).build(null)
        );
    }

    static void handleRegistration(Consumer<Set<Map.Entry<String, DeferredObject<BlockEntityType<BlockEntity>>>>> handler) {
        handler.accept(Register.BLOCK_ENTITIES.entrySet());
    }

    static class Register {
        static Map<String, DeferredObject<BlockEntityType<BlockEntity>>> BLOCK_ENTITIES = new HashMap<>();

        @SuppressWarnings("unchecked")
        static <V extends BlockEntity, T extends BlockEntityType<V>> DeferredObject<T> blockEntity(String name, Supplier<T> beSupplier) {
            DeferredObject<T> ret = new DeferredObject<>(beSupplier);
            BLOCK_ENTITIES.put(name, (DeferredObject<BlockEntityType<BlockEntity>>) ret);
            return ret;
        }

    }

    private EBBlockEntities() {}
}
