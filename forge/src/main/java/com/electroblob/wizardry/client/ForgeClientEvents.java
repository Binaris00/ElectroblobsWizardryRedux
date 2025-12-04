package com.electroblob.wizardry.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.client.effect.ArcaneLockRender;
import com.electroblob.wizardry.client.effect.ContainmentFieldRender;
import net.minecraftforge.api.distmarker.Dist;
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
    }
}
