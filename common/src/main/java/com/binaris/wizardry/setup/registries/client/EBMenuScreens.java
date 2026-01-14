package com.binaris.wizardry.setup.registries.client;

import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.client.gui.screens.ArcaneWorkbenchScreen;
import com.binaris.wizardry.client.gui.screens.BookshelfScreen;
import com.binaris.wizardry.setup.registries.EBMenus;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Handles the registration of menu screens. Check {@code EBMenus}
 */
public final class EBMenuScreens {
    private static final Map<DeferredObject<? extends MenuType<? extends AbstractContainerMenu>>, ScreenFactory<?, ?>> screens = Maps.newHashMap();

    private EBMenuScreens() {
    }

    public static void init() {
        registerScreen(EBMenus.ARCANE_WORKBENCH_MENU, ArcaneWorkbenchScreen::new);
        registerScreen(EBMenus.BOOKSHELF_MENU, BookshelfScreen::new);
    }

    private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(
            DeferredObject<? extends MenuType<M>> menuType,
            ScreenFactory<M, U> screenFactory) {
        screens.put(menuType, screenFactory);
    }

    @SuppressWarnings("unchecked")
    public static void register(BiConsumer<MenuType<? extends AbstractContainerMenu>, ScreenFactory<AbstractContainerMenu, AbstractContainerScreen<AbstractContainerMenu>>> consumer) {
        screens.forEach((menuType, screenFactory) ->
                consumer.accept(menuType.get(), (ScreenFactory<AbstractContainerMenu, AbstractContainerScreen<AbstractContainerMenu>>) screenFactory));
    }

    @FunctionalInterface
    public interface ScreenFactory<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T menu, Inventory inventory, Component title);
    }
}
