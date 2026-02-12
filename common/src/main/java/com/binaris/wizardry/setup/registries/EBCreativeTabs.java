package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegisterFunction;
import com.binaris.wizardry.api.content.util.SpellUtil;
import com.binaris.wizardry.client.NotImplementedItems;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class EBCreativeTabs {
    static Map<String, Supplier<CreativeModeTab>> CREATIVE_MODE_TABS = new LinkedHashMap<>();
    // All EBWizardry Items
    public static final Supplier<CreativeModeTab> ITEMS = creativeTab("ebwizardry",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(EBItems.MAGIC_CRYSTAL.get()))
                    .title(Component.translatable("creativetab.ebwizardry"))
                    .displayItems((parameters, output) ->
                            EBItems.GENERAL_ITEMS.forEach((item) -> output.accept(item.get())))
                    .build()
    );
    // All Wands
    public static final Supplier<CreativeModeTab> WANDS = creativeTab("ebwizardry_wands",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(EBItems.ADVANCED_WAND.get()))
                    .title(Component.translatable("creativetab.ebwizardry_wands"))
                    .displayItems((parameters, output) -> EBItems.WANDS.forEach(i -> output.accept(i.get())))
                    .build()
    );
    // All Armors
    public static final Supplier<CreativeModeTab> ARMORS = creativeTab("ebwizardry_armors",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(EBItems.WIZARD_HAT.get()))
                    .title(Component.translatable("creativetab.ebwizardry_armors"))
                    .displayItems((parameters, output) -> EBItems.ARMORS.forEach(i -> output.accept(i.get())))
                    .build()
    );
    // All Artifacts
    public static final Supplier<CreativeModeTab> ARTIFACTS = creativeTab("ebwizardry_artifacts",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(EBItems.AMULET_RESURRECTION.get()))
                    .title(Component.translatable("creativetab.ebwizardry_artifacts"))
                    .displayItems((parameters, output) -> EBItems.ARTIFACTS.forEach(i -> {
                        if (!NotImplementedItems.notImplemented(i.get())) output.accept(i.get());
                    }))
                    .build()
    );
    // All Spell Books and scrolls
    public static final Supplier<CreativeModeTab> SPELLS = creativeTab("ebwizardry_spells",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(EBItems.SPELL_BOOK.get()))
                    .title(Component.translatable("creativetab.ebwizardry_spells"))
                    .displayItems((parameters, output) -> {
                        createSpellBooks().forEach(output::accept);
                        createScrolls().forEach(output::accept);
                    })
                    .build()
    );
    public static final Supplier<CreativeModeTab> BLOCKS = creativeTab("ebwizardry_blocks",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .icon(() -> new ItemStack(EBBlocks.EARTH_CRYSTAL_BLOCK.get().asItem()))
                    .title(Component.translatable("creativetab.ebwizardry_blocks"))
                    .displayItems((parameters, output) -> {
                                EBBlocks.BLOCK_ITEMS.forEach((name, block) -> output.accept(block.get().asItem()));
                                output.accept(EBItems.RECEPTACLE.get());
                            }
                    )
                    .build()
    );

    private EBCreativeTabs() {
    }

    // ======= Registry =======
    public static void register(RegisterFunction<CreativeModeTab> function) {
        CREATIVE_MODE_TABS.forEach(((id, creativeModeTabSupplier) ->
                function.register(BuiltInRegistries.CREATIVE_MODE_TAB, WizardryMainMod.location(id), creativeModeTabSupplier.get())));
    }


    // ======= Helpers =======
    private static List<ItemStack> createSpellBooks() {
        List<ItemStack> list = new ArrayList<>();
        List<Element> elements = Services.REGISTRY_UTIL.getElements().stream().toList();
        Services.REGISTRY_UTIL.getSpells()
                .stream()
                .filter(spell -> spell != Spells.NONE)
                .sorted(Comparator.<Spell>comparingInt(s -> s.getTier().getLevel())
                        .thenComparingInt(s -> elements.indexOf(s.getElement())))
                .forEach(spell -> list.add(SpellUtil.setSpell(new ItemStack(EBItems.SPELL_BOOK.get()), spell)));
        return list;
    }

    private static List<ItemStack> createScrolls() {
        List<ItemStack> list = new ArrayList<>();
        List<Element> elements = Services.REGISTRY_UTIL.getElements().stream().toList();
        Services.REGISTRY_UTIL.getSpells()
                .stream().filter(spell -> spell != Spells.NONE)
                .sorted(Comparator.<Spell>comparingInt(s -> s.getTier().getLevel())
                        .thenComparingInt(s -> elements.indexOf(s.getElement())))
                .forEach(spell -> list.add(SpellUtil.setSpell(new ItemStack(EBItems.SCROLL.get()), spell)));
        return list;
    }

    static Supplier<CreativeModeTab> creativeTab(String name, Supplier<CreativeModeTab> creativeModeTab) {
        CREATIVE_MODE_TABS.put(name, creativeModeTab);
        return creativeModeTab;
    }
}
