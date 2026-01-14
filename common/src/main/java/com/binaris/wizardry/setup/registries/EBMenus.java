package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.api.content.util.RegisterFunction;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.content.menu.BookshelfMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the registration of menus. Check {@code EBMenuScreens}
 */
public final class EBMenus {
    static Map<String, DeferredObject<MenuType<?>>> MENUS = new HashMap<>();

    private EBMenus() {
    }    public static final DeferredObject<MenuType<ArcaneWorkbenchMenu>> ARCANE_WORKBENCH_MENU = menu("arcane_workbench_menu", new MenuType<>(ArcaneWorkbenchMenu::new, FeatureFlagSet.of()));

    // ======= Registry =======
    public static void register(RegisterFunction<MenuType<?>> function) {
        MENUS.forEach(((id, menu) ->
                function.register(BuiltInRegistries.MENU, WizardryMainMod.location(id), menu.get())));
    }    public static final DeferredObject<MenuType<BookshelfMenu>> BOOKSHELF_MENU = menu("bookshelf_menu", new MenuType<>(BookshelfMenu::new, FeatureFlagSet.of()));

    // ======= Helpers =======
    static <T extends AbstractContainerMenu> DeferredObject<MenuType<T>> menu(String name, MenuType<T> menuType) {
        DeferredObject<MenuType<T>> deferredMenu = new DeferredObject<>(() -> menuType);
        putMenu(name, deferredMenu);
        return deferredMenu;
    }

    @SuppressWarnings("unchecked")
    static void putMenu(String name, DeferredObject<? extends MenuType<?>> menu) {
        MENUS.put(name, (DeferredObject<MenuType<?>>) menu);
    }





}
