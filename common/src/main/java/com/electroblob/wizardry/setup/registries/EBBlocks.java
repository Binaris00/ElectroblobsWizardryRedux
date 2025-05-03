package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.block.ArcaneWorkbenchBlock;
import com.electroblob.wizardry.content.block.PermafrostBlock;
import com.electroblob.wizardry.content.block.VanishingCobwebBlock;
import com.electroblob.wizardry.setup.datagen.DataGenProcessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class EBBlocks {
    static final Map<String, DeferredObject<Block>> BLOCKS = new HashMap<>();
    static final Map<String, DeferredObject<Block>> BLOCK_ITEMS = new HashMap<>(); // <name, <block, item>>
    private EBBlocks() {}

    public static final DeferredObject<Block> MAGIC_CRYSTAL_BLOCK = crystalBlock("magic", MapColor.COLOR_PINK);
    public static final DeferredObject<Block> FIRE_CRYSTAL_BLOCK = crystalBlock("fire", MapColor.TERRACOTTA_ORANGE);
    public static final DeferredObject<Block> ICE_CRYSTAL_BLOCK = crystalBlock("ice", MapColor.COLOR_LIGHT_BLUE);
    public static final DeferredObject<Block> LIGHTNING_CRYSTAL_BLOCK = crystalBlock("lightning", MapColor.COLOR_CYAN);
    public static final DeferredObject<Block> NECROMANCY_CRYSTAL_BLOCK = crystalBlock("necromancy", MapColor.COLOR_PURPLE);
    public static final DeferredObject<Block> EARTH_CRYSTAL_BLOCK = crystalBlock("earth", MapColor.COLOR_GREEN);
    public static final DeferredObject<Block> SORCERY_CRYSTAL_BLOCK = crystalBlock("sorcery", MapColor.COLOR_LIGHT_GREEN);
    public static final DeferredObject<Block> HEALING_CRYSTAL_BLOCK = crystalBlock("healing", MapColor.COLOR_YELLOW);

    public static final DeferredObject<Block> PERMAFROST = block("permafrost", PermafrostBlock::new, false, false, false);
    public static final DeferredObject<Block> VANISHING_COBWEB = block("vanishing_cobweb", () -> new VanishingCobwebBlock(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, false, false);
    public static final DeferredObject<Block> ARCANE_WORKBENCH = block("arcane_workbench", () -> new ArcaneWorkbenchBlock(BlockBehaviour.Properties.copy(Blocks.STONE)),false, true, true);
    public static final DeferredObject<Block> METEOR = block("meteor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).lightLevel((state) -> 1)), false, false, false);


    // ======= Registry =======
    /** Registers all blocks for the mod, used inside each loader */
    public static void register(RegisterFunction<Block> function){
        BLOCKS.forEach((name, block) ->
                function.register(BuiltInRegistries.BLOCK, WizardryMainMod.location(name), block.get()));
    }

    // ======= Helpers =======
    /**Helps to create a crystal block internally for te mod, ask for the map color and the rest is just the defaults*/
    static DeferredObject<Block> crystalBlock(String elementName, MapColor color) {
        return block("crystal_block_" + elementName, () -> new DropExperienceBlock(BlockBehaviour.Properties.of()
                .mapColor(color).strength(5, 10)
                .sound(SoundType.AMETHYST).requiresCorrectToolForDrops(), UniformInt.of(0, 2)));
    }

    static DeferredObject<Block> block(String name) {
        return block(name, () -> new Block(Block.Properties.of()));
    }

    static DeferredObject<Block> block(String name, Supplier<Block> blockSupplier) {
        return block(name, blockSupplier, true, true, true);
    }

    static DeferredObject<Block> block(String name, boolean defaultModel, boolean defaultDrop, boolean item) {
        return block(name, () -> new Block(Block.Properties.of()), defaultModel, defaultDrop, item);
    }

    /**Helps to create a block internally for the mod, ask if it should add a default model, drop and a block item*/
    static DeferredObject<Block> block(String name, Supplier<Block> blockSupplier, boolean defaultModel, boolean defaultDrop, boolean item) {
        return block(name, blockSupplier,
                item ? (registeredBlock) ->
                        EBItems.item(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()), false, false)
                        : null,
                defaultModel, defaultDrop);
    }

    static DeferredObject<Block> block(String name, Supplier<Block> blockSupplier, @Nullable Consumer<DeferredObject<Block>> registerBlockItem, boolean defaultModel, boolean defaultDrop) {
        DeferredObject<Block> ret = new DeferredObject<>(blockSupplier);
        BLOCKS.put(name, ret);
        // Because not all the blocks needs a block item
        if(registerBlockItem != null) {
            registerBlockItem.accept(ret);
            BLOCK_ITEMS.put(name, ret);
        }

        if(defaultModel) DataGenProcessor.get().addDefaultBlockModel(name, ret);
        if(defaultDrop) DataGenProcessor.get().addDefaultBlockDrop(name, ret);
        return ret;
    }
}
