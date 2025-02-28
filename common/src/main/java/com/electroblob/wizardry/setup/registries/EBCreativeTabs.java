package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.common.spell.SpellRegistry;
import com.electroblob.wizardry.api.common.util.SpellUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.electroblob.wizardry.setup.registries.EBCreativeTabs.Register.*;

public final class EBCreativeTabs {

    static {
        Register.init();
    }

    // All EBWizardry Items
    public static final Supplier<CreativeModeTab> ITEMS =
            register(
                    "ebwizardry",
                    () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                            .icon(() -> new ItemStack(EBItems.MAGIC_CRYSTAL.get()))
                            .title(Component.translatable("creativetab.ebwizardry"))
                            .displayItems((parameters, output) -> EBItems.Register.ITEMS.forEach((i, item) -> output.accept(item.get())))
                            .build()
            );

    // All Wands
    public static final Supplier<CreativeModeTab> WANDS =
            register(
                    "ebwizardry_wands",
                    () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                            .icon(() -> new ItemStack(EBItems.ADVANCED_WAND.get()))
                            .title(Component.translatable("creativetab.ebwizardry_wands"))
                            .displayItems((parameters, output) -> EBItems.Register.WANDS.forEach(i -> output.accept(i.get())))
                            .build()
            );

    // All Armors
    public static final Supplier<CreativeModeTab> ARMORS =
            register(
                    "ebwizardry_armors",
                    () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                            .icon(() -> new ItemStack(EBItems.WIZARD_HAT.get()))
                            .title(Component.translatable("creativetab.ebwizardry_armors"))
                            .displayItems((parameters, output) -> EBItems.Register.ARMORS.forEach(i -> output.accept(i.get())))
                            .build()
            );

    // All Artifacts
    public static final Supplier<CreativeModeTab> ARTIFACTS =
            register(
                    "ebwizardry_artifacts",
                    () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                            .icon(() -> new ItemStack(EBItems.AMULET_RESURRECTION.get()))
                            .title(Component.translatable("creativetab.ebwizardry_artifacts"))
                            .displayItems((parameters, output) -> EBItems.Register.ARTIFACTS.forEach(i -> output.accept(i.get())))
                            .build()
            );


    // All Spell Books and scrolls
    public static final Supplier<CreativeModeTab> SPELLS =
                register(
                        "ebwizardry_spells",
                        () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                                .icon(() -> new ItemStack(EBItems.SPELL_BOOK.get()))
                                .title(Component.translatable("creativetab.ebwizardry_spells"))
                                .displayItems((parameters, output) -> {
                                    Register.createSpellBooks().forEach(output::accept);
                                    Register.createScrolls().forEach(output::accept);
                                })
                                .build()
                );

    public static final Supplier<CreativeModeTab> BLOCKS = register(
            "ebwizardry_blocks",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(EBBlocks.EARTH_CRYSTAL_BLOCK.get().asItem()))
                    .title(Component.translatable("creativetab.ebwizardry_blocks"))
                    .displayItems((parameters, output) -> EBBlocks.Register.BLOCKS.forEach((name, block) -> output.accept(block.get())))
                    .build()
    );


    static void handleRegistration(Consumer<Map<String, Supplier<CreativeModeTab>>> handler) {
        handler.accept(CREATIVE_MODE_TABS);
    }

    static class Register {

        static Map<String, Supplier<CreativeModeTab>> CREATIVE_MODE_TABS = new HashMap<>();

        static Supplier<CreativeModeTab> register(String name, Supplier<CreativeModeTab> creativeModeTab) {
            CREATIVE_MODE_TABS.put(name, creativeModeTab);
            return creativeModeTab;
        }

        private static List<ItemStack> createSpellBooks() {
            List<ItemStack> list = new ArrayList<>();
            SpellRegistry.entrySet()
                    .forEach(entry -> list.add(
                            SpellUtil.setSpell(new ItemStack(EBItems.SPELL_BOOK.get()), entry.getKey().location())
                    ));
            return list;
        }

        private static List<ItemStack> createScrolls() {
            List<ItemStack> list = new ArrayList<>();
            SpellRegistry.entrySet()
                    .forEach(entry -> list.add(
                            SpellUtil.setSpell(new ItemStack(EBItems.SCROLL.get()), entry.getKey().location())
                    ));
            return list;
        }

        static void load() {}

        private static void init(){}

    }

    static void load(){}

    private EBCreativeTabs() {
    }
}
