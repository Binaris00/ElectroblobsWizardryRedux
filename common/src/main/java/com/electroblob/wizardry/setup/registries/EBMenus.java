package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.HashMap;
import java.util.Map;

public final class EBMenus {
    static Map<String, DeferredObject<MenuType<?>>> MENUS = new HashMap<>();
    private EBMenus() {}

    public static final DeferredObject<MenuType<ArcaneWorkbenchMenu>> ARCANE_WORKBENCH_MENU =
            menu("arcane_workbench_menu", new MenuType<>(ArcaneWorkbenchMenu::new, FeatureFlagSet.of()));

    // ======= Registry =======
    public static void register(RegisterFunction<MenuType<?>> function){
        MENUS.forEach(((id, menu) ->
                function.register(BuiltInRegistries.MENU, WizardryMainMod.location(id), menu.get())));
    }

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
