package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.api.common.DeferredObject;
import com.electroblob.wizardry.setup.datagen.DataGenProcessor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.electroblob.wizardry.setup.registries.EBBlocks.Register.*;


@SuppressWarnings("unused")
public final class EBBlocks {
    public static final DeferredObject<Block> MAGIC_CRYSTAL_BLOCK = crystalBlock("magic");
    public static final DeferredObject<Block> FIRE_CRYSTAL_BLOCK = crystalBlock("fire");
    public static final DeferredObject<Block> ICE_CRYSTAL_BLOCK = crystalBlock("ice");
    public static final DeferredObject<Block> LIGHTNING_CRYSTAL_BLOCK = crystalBlock("lightning");
    public static final DeferredObject<Block> NECROMANCY_CRYSTAL_BLOCK = crystalBlock("necromancy");
    public static final DeferredObject<Block> EARTH_CRYSTAL_BLOCK = crystalBlock("earth");
    public static final DeferredObject<Block> SORCERY_CRYSTAL_BLOCK = crystalBlock("sorcery");
    public static final DeferredObject<Block> HEALING_CRYSTAL_BLOCK = crystalBlock("healing");

    public static final DeferredObject<Block> ARCANE_WORK_BENCH = block("arcane_workbench", false, true);

    static void handleRegistration(Consumer<Set<Map.Entry<String, DeferredObject<Block>>>> handler) {
        handler.accept(Collections.unmodifiableSet(BLOCKS.entrySet()));
    }

    static class Register {
        static final Map<String, DeferredObject<Block>> BLOCKS = new HashMap<>();

        /**
         * Registers a crystal block and block item.
         * @param elementName Only the element name should be provided
         */
        static DeferredObject<Block> crystalBlock(String elementName) {
            return block(
                    "crystal_block_" + elementName,
                    () -> new Block(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE) // TODO: Need to do an actual correct map color
                            .strength(1.5F)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops()
                    )
            );
        }

        static DeferredObject<Block> block(String name) {
            return block(name, () -> new Block(Block.Properties.of()));
        }

        static DeferredObject<Block> block(String name, boolean defaultModel, boolean defaultDrop) {
            return block(name, () -> new Block(Block.Properties.of()), defaultModel, defaultDrop);
        }

        static DeferredObject<Block> block(String name, Supplier<Block> blockSupplier) {
            return block(name, blockSupplier, true, true);
        }

        static DeferredObject<Block> block(String name, Supplier<Block> blockSupplier, boolean defaultModel, boolean defaultDrop) {
            return block(
                    name,
                    blockSupplier,
                    (registeredBlock) -> EBItems.Register.item(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()), false),
                    defaultModel,
                    defaultDrop
            );
        }

        static DeferredObject<Block> block(String name, Supplier<Block> blockSupplier, Consumer<DeferredObject<Block>> registerBlockItem, boolean defaultModel, boolean defaultDrop) {
            DeferredObject<Block> ret = new DeferredObject<>(blockSupplier);
            BLOCKS.put(name, ret);
            registerBlockItem.accept(ret); // We can register block items straight away after the block is registered on forge
            if(defaultModel) DataGenProcessor.get().addDefaultBlockModel(name, ret);
            if(defaultDrop) DataGenProcessor.get().addDefaultBlockDrop(name, ret);
            return ret;
        }

    }


    static void load(){}

    private EBBlocks() {}

}
