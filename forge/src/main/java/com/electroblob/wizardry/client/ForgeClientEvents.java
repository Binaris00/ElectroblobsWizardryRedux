package com.electroblob.wizardry.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.setup.registries.client.EBKeyBinding;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// TODO
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = WizardryMainMod.MOD_ID)
public class ForgeClientEvents {
    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelow(VanillaGuiOverlay.CROSSHAIR.id(), "charge_meter", (forgeGui, guiGraphics, partialTicks, width, height) -> {
            Player player = Minecraft.getInstance().player;

            ItemStack wand = player.getMainHandItem();
            boolean mainHand;

            if (!(wand.getItem() instanceof ISpellCastingItem castingItem && castingItem.showSpellHUD(player, wand))) {
                wand = player.getOffhandItem();
                mainHand = false;
                if (!(wand.getItem() instanceof ISpellCastingItem castingItem && castingItem.showSpellHUD(player, wand)))
                    return;
            } else {
                mainHand = true;
            }

            ItemStack finalWand = wand;
            SpellGUIDisplay.renderChargeMeter(guiGraphics.pose(), Minecraft.getInstance().player, finalWand, width, height, partialTicks);
        });
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "spell_hud", (forgeGui, guiGraphics, partialTicks, width, height) -> {
            Player player = Minecraft.getInstance().player;

            ItemStack wand = player.getMainHandItem();
            boolean mainHand;

            if (!(wand.getItem() instanceof ISpellCastingItem castingItem && castingItem.showSpellHUD(player, wand))) {
                wand = player.getOffhandItem();
                mainHand = false;
                if (!(wand.getItem() instanceof ISpellCastingItem castingItem && castingItem.showSpellHUD(player, wand)))
                    return;
            } else {
                mainHand = true;
            }

            ItemStack finalWand = wand;
            SpellGUIDisplay.renderSpellHUD(guiGraphics, guiGraphics.pose(), Minecraft.getInstance().player, finalWand, mainHand, width, height, partialTicks, true);
        });
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "spell_hud_text", (forgeGui, guiGraphics, partialTicks, width, height) -> {
            Player player = Minecraft.getInstance().player;

            ItemStack wand = player.getMainHandItem();
            boolean mainHand;

            if (!(wand.getItem() instanceof ISpellCastingItem castingItem && castingItem.showSpellHUD(player, wand))) {
                wand = player.getOffhandItem();
                mainHand = false;
                if (!(wand.getItem() instanceof ISpellCastingItem castingItem && castingItem.showSpellHUD(player, wand)))
                    return;
            } else {
                mainHand = true;
            }

            ItemStack finalWand = wand;
            SpellGUIDisplay.renderSpellHUD(guiGraphics, guiGraphics.pose(), Minecraft.getInstance().player, finalWand, mainHand, width, height, partialTicks, false);
        });
    }

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(EBKeyBinding.NEXT_SPELL);
        event.register(EBKeyBinding.PREVIOUS_SPELL);
        for (KeyMapping key : EBKeyBinding.SPELL_QUICK_ACCESS) event.register(key);
    }
}
