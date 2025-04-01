package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class BlockRegistryForge {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WizardryMainMod.MOD_ID);

    public static void register() {
        EBRegister.registerBlocks((blockCollection) -> blockCollection.forEach((block) ->
                BLOCKS.register(block.getKey(), block.getValue()))
        );
    }

    private BlockRegistryForge() {}
}
