package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.block.ArcaneWorkbenchBlock;
import com.electroblob.wizardry.content.block.PermafrostBlock;
import com.electroblob.wizardry.content.block.VanishingCobwebBlock;
import com.electroblob.wizardry.setup.datagen.DataGenProcessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

// TODO: Not all the blocks needs an item... ; ;
@SuppressWarnings("unused")
public final class EBBlocks {
    private EBBlocks() {}

    static final Map<String, DeferredObject<Block>> BLOCKS = new HashMap<>();

    public static final DeferredObject<Block> MAGIC_CRYSTAL_BLOCK = crystalBlock("magic");
    public static final DeferredObject<Block> FIRE_CRYSTAL_BLOCK = crystalBlock("fire");
    public static final DeferredObject<Block> ICE_CRYSTAL_BLOCK = crystalBlock("ice");
    public static final DeferredObject<Block> LIGHTNING_CRYSTAL_BLOCK = crystalBlock("lightning");
    public static final DeferredObject<Block> NECROMANCY_CRYSTAL_BLOCK = crystalBlock("necromancy");
    public static final DeferredObject<Block> EARTH_CRYSTAL_BLOCK = crystalBlock("earth");
    public static final DeferredObject<Block> SORCERY_CRYSTAL_BLOCK = crystalBlock("sorcery");
    public static final DeferredObject<Block> HEALING_CRYSTAL_BLOCK = crystalBlock("healing");

    public static final DeferredObject<Block> PERMAFROST = block("permafrost", PermafrostBlock::new, false, false, false);
    public static final DeferredObject<Block> VANISHING_COBWEB = block("vanishing_cobweb", () -> new VanishingCobwebBlock(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, false, false);
    public static final DeferredObject<Block> ARCANE_WORK_BENCH = block("arcane_workbench", () -> new ArcaneWorkbenchBlock(BlockBehaviour.Properties.copy(Blocks.STONE)),false, true, true);
    public static final DeferredObject<Block> METEOR = block("meteor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).lightLevel((state) -> 1)), false, false, false);


    // ======= Registry =======
    public static void register(RegisterFunction<Block> function){
        BLOCKS.forEach((name, block) ->
                function.register(BuiltInRegistries.BLOCK, WizardryMainMod.location(name), block.get()));
    }


    // ======= Helpers =======
    static DeferredObject<Block> crystalBlock(String elementName) {
        return block("crystal_block_" + elementName, () -> new Block(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_PURPLE) // TODO: Need to do an actual correct map color
                        .strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops()));
    }

    static DeferredObject<Block> block(String name) {
        return block(name, () -> new Block(Block.Properties.of()));
    }

    static DeferredObject<Block> block(String name, boolean defaultModel, boolean defaultDrop, boolean item) {
        return block(name, () -> new Block(Block.Properties.of()), defaultModel, defaultDrop, item);
    }

    static DeferredObject<Block> block(String name, Supplier<Block> blockSupplier) {
        return block(name, blockSupplier, true, true, true);
    }

    static DeferredObject<Block> block(String name, Supplier<Block> blockSupplier, boolean defaultModel, boolean defaultDrop, boolean item) {
        return block(name, blockSupplier,
                (registeredBlock) -> EBItems.item(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()), false),
                defaultModel, defaultDrop);
    }

    static DeferredObject<Block> block(String name, Supplier<Block> blockSupplier, Consumer<DeferredObject<Block>> registerBlockItem, boolean defaultModel, boolean defaultDrop) {
        DeferredObject<Block> ret = new DeferredObject<>(blockSupplier);
        BLOCKS.put(name, ret);
        registerBlockItem.accept(ret); // We can register block items straight away after the block is registered on forge
        if(defaultModel) DataGenProcessor.get().addDefaultBlockModel(name, ret);
        if(defaultDrop) DataGenProcessor.get().addDefaultBlockDrop(name, ret);
        return ret;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (p_50763_) -> p_50763_.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }
}
