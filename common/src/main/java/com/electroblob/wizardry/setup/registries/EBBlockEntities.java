package com.electroblob.wizardry.setup.registries;



public final class EBBlockEntities {

//    public static final DeferredObject<BlockEntityType<VanishingCobwebBlockEntity>> ARCANE_WORKBENCH_BE;
//
//    static {
//        ARCANE_WORKBENCH_BE = blockEntity(
//                "arcane_workbench_be", () -> BlockEntityType.Builder.
//                        of(VanishingCobwebBlockEntity::new, EBBlocks.VANISHING_COBWEB).build(null)
//        );
//    }
//
//    static void handleRegistration(Consumer<Set<Map.Entry<String, DeferredObject<BlockEntityType<BlockEntity>>>>> handler) {
//        handler.accept(Register.BLOCK_ENTITIES.entrySet());
//    }
//
//    static class Register {
//        static Map<String, DeferredObject<BlockEntityType<BlockEntity>>> BLOCK_ENTITIES = new HashMap<>();
//
//        @SuppressWarnings("unchecked")
//        static <V extends BlockEntity, T extends BlockEntityType<V>> DeferredObject<T> blockEntity(String name, Supplier<T> beSupplier) {
//            DeferredObject<T> ret = new DeferredObject<>(beSupplier);
//            BLOCK_ENTITIES.put(name, (DeferredObject<BlockEntityType<BlockEntity>>) ret);
//            return ret;
//        }
//
//    }

    private EBBlockEntities() {}
}
