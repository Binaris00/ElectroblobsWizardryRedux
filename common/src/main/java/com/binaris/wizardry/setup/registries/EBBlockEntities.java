package com.binaris.wizardry.setup.registries;


import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.api.content.util.RegisterFunction;
import com.binaris.wizardry.content.blockentity.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EBBlockEntities {
    static Map<String, DeferredObject<BlockEntityType<BlockEntity>>> BLOCK_ENTITIES = new HashMap<>();

    private EBBlockEntities() {
    }

    // ======= Registry =======
    public static void register(RegisterFunction<BlockEntityType<?>> function) {
        BLOCK_ENTITIES.forEach((name, blockEntityType) ->
                function.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, WizardryMainMod.location(name), blockEntityType.get()));
    }

    // ======= Helpers =======
    @SuppressWarnings("unchecked")
    static <V extends BlockEntity, T extends BlockEntityType<V>> DeferredObject<T> blockEntity(String name, Supplier<T> beSupplier) {
        DeferredObject<T> ret = new DeferredObject<>(beSupplier);
        BLOCK_ENTITIES.put(name, (DeferredObject<BlockEntityType<BlockEntity>>) ret);
        return ret;
    }    public static final DeferredObject<BlockEntityType<VanishingCobwebBlockEntity>> VANISHING_COBWEB = blockEntity(
            "vanishing_cobweb", () -> BlockEntityType.Builder.of(VanishingCobwebBlockEntity::new, EBBlocks.VANISHING_COBWEB.get()).build(null)
    );



    public static final DeferredObject<BlockEntityType<ArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH = blockEntity(
            "arcane_workbench", () -> BlockEntityType.Builder.of(ArcaneWorkbenchBlockEntity::new, EBBlocks.ARCANE_WORKBENCH.get()).build(null)
    );

    public static final DeferredObject<BlockEntityType<ReceptacleBlockEntity>> RECEPTACLE = blockEntity(
            "receptacle", () -> BlockEntityType.Builder.of(ReceptacleBlockEntity::new, EBBlocks.RECEPTACLE.get(), EBBlocks.WALL_RECEPTACLE.get()).build(null)
    );

    public static final DeferredObject<BlockEntityType<ImbuementAltarBlockEntity>> IMBUEMENT_ALTAR = blockEntity(
            "imbuement_altar", () -> BlockEntityType.Builder.of(ImbuementAltarBlockEntity::new, EBBlocks.IMBUEMENT_ALTAR.get()).build(null)
    );

    public static final DeferredObject<BlockEntityType<BookshelfBlockEntity>> BOOKSHELF = blockEntity(
            "bookshelf", () -> BlockEntityType.Builder.of(BookshelfBlockEntity::new,
                            EBBlocks.OAK_BOOKSHELF.get(), EBBlocks.SPRUCE_BOOKSHELF.get(), EBBlocks.BIRCH_BOOKSHELF.get(),
                            EBBlocks.JUNGLE_BOOKSHELF.get(), EBBlocks.ACACIA_BOOKSHELF.get(), EBBlocks.DARK_OAK_BOOKSHELF.get())
                    .build(null));


    public static final DeferredObject<BlockEntityType<RunestonePedestalBlockEntity>> RUNESTONE_PEDESTAL = blockEntity(
            "runestone_pedestal", () -> BlockEntityType.Builder.of(RunestonePedestalBlockEntity::new,
                            EBBlocks.EARTH_RUNESTONE_PEDESTAL.get(), EBBlocks.FIRE_RUNESTONE_PEDESTAL.get(), EBBlocks.HEALING_RUNESTONE_PEDESTAL.get(),
                            EBBlocks.ICE_RUNESTONE_PEDESTAL.get(), EBBlocks.LIGHTNING_RUNESTONE_PEDESTAL.get(), EBBlocks.NECROMANCY_RUNESTONE_PEDESTAL.get()
                            , EBBlocks.SORCERY_RUNESTONE_PEDESTAL.get())
                    .build(null));

}
