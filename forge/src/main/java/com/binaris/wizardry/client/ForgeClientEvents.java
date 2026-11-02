package com.binaris.wizardry.client;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.client.effect.ArcaneLockRender;
import com.binaris.wizardry.client.effect.ContainmentFieldRender;
import com.binaris.wizardry.client.effect.WingsRender;
import com.binaris.wizardry.core.config.ConfigManager;
import com.binaris.wizardry.core.networking.c2s.RequestPlayerDataSyncC2S;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeClientEvents {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        ContainmentFieldRender.render(event.getCamera(), event.getPoseStack(), event.getPartialTick());
        ArcaneLockRender.render(event.getCamera(), event.getPoseStack(), event.getPartialTick());
        WingsRender.render(event.getCamera(), event.getPoseStack(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft.getInstance().execute(() ->
                Services.NETWORK_HELPER.sendToServer(new RequestPlayerDataSyncC2S()));
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ConfigManager.restoreLocalConfigs();
    }
}
