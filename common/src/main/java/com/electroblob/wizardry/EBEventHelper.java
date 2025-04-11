package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.effect.MagicMobEffect;
import com.electroblob.wizardry.api.content.event.EBClientTickEvent;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.client.sound.SoundLoop;
import com.electroblob.wizardry.content.effect.FireSkinMobEffect;
import com.electroblob.wizardry.content.effect.StaticAuraMobEffect;
import com.electroblob.wizardry.content.effect.WardMobEffect;
import com.electroblob.wizardry.content.entity.construct.BubbleConstruct;
import com.electroblob.wizardry.core.event.WizardryEventBus;

/**
 * Simple class to save all the event helper methods
 * This is internal use, you're not supposed to use this
 * */
public final class EBEventHelper {
    private EBEventHelper() {}

    public static void register(){
        WizardryEventBus bus = WizardryEventBus.getInstance();
        onLivingHurtEvent(bus);
        onLivingTickEvent(bus);
        onClientTick(bus);
    }

    private static void onLivingHurtEvent(WizardryEventBus bus) {
        bus.register(EBLivingHurtEvent.class, StaticAuraMobEffect::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, FireSkinMobEffect::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, WardMobEffect::onLivingHurt);
        bus.register(EBLivingHurtEvent.class, BubbleConstruct::onLivingHurt);
    }

    private static void onLivingTickEvent(WizardryEventBus bus) {
        bus.register(EBLivingTick.class, MagicMobEffect::onLivingTick);
    }

    private static void onClientTick(WizardryEventBus bus){
        bus.register(EBClientTickEvent.class, SoundLoop::onClientTick);
    }
}
