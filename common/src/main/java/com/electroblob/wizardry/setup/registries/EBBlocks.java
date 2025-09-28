package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.block.*;
import com.electroblob.wizardry.setup.datagen.EBDataGenProcessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class EBBlocks {
    public static final Map<String, DeferredObject<Block>> BLOCKS = new HashMap<>();
    public static final Map<String, DeferredObject<Block>> BLOCK_ITEMS = new HashMap<>();
    private EBBlocks() {}

    public static final DeferredObject<Block> MAGIC_CRYSTAL_BLOCK = crystalBlock("magic", MapColor.COLOR_PINK);
    public static final DeferredObject<Block> FIRE_CRYSTAL_BLOCK = crystalBlock("fire", MapColor.TERRACOTTA_ORANGE);
    public static final DeferredObject<Block> ICE_CRYSTAL_BLOCK = crystalBlock("ice", MapColor.COLOR_LIGHT_BLUE);
    public static final DeferredObject<Block> LIGHTNING_CRYSTAL_BLOCK = crystalBlock("lightning", MapColor.COLOR_CYAN);
    public static final DeferredObject<Block> NECROMANCY_CRYSTAL_BLOCK = crystalBlock("necromancy", MapColor.COLOR_PURPLE);
    public static final DeferredObject<Block> EARTH_CRYSTAL_BLOCK = crystalBlock("earth", MapColor.COLOR_GREEN);
    public static final DeferredObject<Block> SORCERY_CRYSTAL_BLOCK = crystalBlock("sorcery", MapColor.COLOR_LIGHT_GREEN);
    public static final DeferredObject<Block> HEALING_CRYSTAL_BLOCK = crystalBlock("healing", MapColor.COLOR_YELLOW);

    public static final DeferredObject<Block> CRYSTAL_ORE = block("crystal_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2, 5).sound(SoundType.STONE), UniformInt.of(4, 8)), true, false, true);
    public static final DeferredObject<Block> DEEPSLATE_CRYSTAL_ORE = block("deepslate_crystal_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2, 5).sound(SoundType.STONE), UniformInt.of(4, 8)), true, true, true);

    public static final DeferredObject<Block> CRYSTAL_FLOWER = block("crystal_flower", () -> new CrystalFlowerBlock(BlockBehaviour.Properties.copy(Blocks.SUNFLOWER).noCollission().lightLevel((state) -> 15)), false, false, true);
    public static final DeferredObject<Block> POTTED_CRYSTAL_FLOWER = block("potted_crystal_flower", () -> new FlowerPotBlock(CRYSTAL_FLOWER.get(), BlockBehaviour.Properties.of().instabreak().noOcclusion().lightLevel((state) -> 15).pushReaction(PushReaction.DESTROY)), false, false, false);

    public static final DeferredObject<Block> PERMAFROST = block("permafrost", PermafrostBlock::new, false, false, false);
    public static final DeferredObject<Block> VANISHING_COBWEB = block("vanishing_cobweb", () -> new VanishingCobwebBlock(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, false, false);
    public static final DeferredObject<Block> ARCANE_WORKBENCH = block("arcane_workbench", () -> new ArcaneWorkbenchBlock(BlockBehaviour.Properties.copy(Blocks.STONE)),false, true, true);
    public static final DeferredObject<Block> METEOR = block("meteor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).lightLevel((state) -> 1)), false, false, false);

    // TODO Temp blocks because I will work on crafts, not on adding misc stuff
    public static final DeferredObject<Block> GILDED_OAK_WOOD = block("gilded_oak_wood", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> GILDED_SPRUCE_WOOD = block("gilded_spruce_wood", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> GILDED_BIRCH_WOOD = block("gilded_birch_wood", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> GILDED_JUNGLE_WOOD = block("gilded_jungle_wood", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> GILDED_ACACIA_WOOD = block("gilded_acacia_wood", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> GILDED_DARK_OAK_WOOD = block("gilded_dark_oak_wood", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);

    public static final DeferredObject<Block> OAK_BOOKSHELF = block("oak_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> SPRUCE_BOOKSHELF = block("spruce_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> BIRCH_BOOKSHELF = block("birch_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> JUNGLE_BOOKSHELF = block("jungle_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> ACACIA_BOOKSHELF = block("acacia_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> DARK_OAK_BOOKSHELF = block("dark_oak_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);

    public static final DeferredObject<Block> OAK_LECTERN = block("oak_lectern", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> SPRUCE_LECTERN = block("spruce_lectern", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> BIRCH_LECTERN = block("birch_lectern", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> JUNGLE_LECTERN = block("jungle_lectern", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> ACACIA_LECTERN = block("acacia_lectern", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> DARK_OAK_LECTERN = block("dark_oak_lectern", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);

    public static final DeferredObject<Block> FIRE_RUNESTONE = block("fire_runestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> ICE_RUNESTONE = block("ice_runestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> LIGHTNING_RUNESTONE = block("lightning_runestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> NECROMANCY_RUNESTONE = block("necromancy_runestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> EARTH_RUNESTONE = block("earth_runestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> SORCERY_RUNESTONE = block("sorcery_runestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> HEALING_RUNESTONE = block("healing_runestone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);

    public static final DeferredObject<Block> FIRE_RUNESTONE_PEDESTAL = block("fire_runestone_pedestal", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> ICE_RUNESTONE_PEDESTAL = block("ice_runestone_pedestal", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> LIGHTNING_RUNESTONE_PEDESTAL = block("lightning_runestone_pedestal", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> NECROMANCY_RUNESTONE_PEDESTAL = block("necromancy_runestone_pedestal", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> EARTH_RUNESTONE_PEDESTAL = block("earth_runestone_pedestal", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> SORCERY_RUNESTONE_PEDESTAL = block("sorcery_runestone_pedestal", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> HEALING_RUNESTONE_PEDESTAL = block("healing_runestone_pedestal", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);


    public static final DeferredObject<Block> TRANSPORTATION_STONE = block("transportation_stone", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBWEB).noCollission().strength(4)), false, true, true);
    public static final DeferredObject<Block> RECEPTACLE = block("receptacle", ReceptacleBlock::new, false, true, true);




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

        if(defaultModel) EBDataGenProcessor.addDefaultBlockModel(name, ret);
        if(defaultDrop) EBDataGenProcessor.addDefaultBlockDrop(name, ret);
        return ret;
    }
}
