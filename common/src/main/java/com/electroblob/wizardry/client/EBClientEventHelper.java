package com.electroblob.wizardry.client;

import com.electroblob.wizardry.api.content.event.EBClientTickEvent;
import com.electroblob.wizardry.api.content.event.EBLivingTick;
import com.electroblob.wizardry.client.sound.SoundLoop;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.setup.registries.client.EBKeyBinding;

public final class EBClientEventHelper {
    private EBClientEventHelper() {
    }

    public static void register() {
        WizardryEventBus bus = WizardryEventBus.getInstance();
        onLivingTickEvent(bus);
        onClientTick(bus);
    }

    private static void onLivingTickEvent(WizardryEventBus bus) {
        bus.register(EBLivingTick.class, SpellGUIDisplay::onLivingTickEvent);
    }

    private static void onClientTick(WizardryEventBus bus) {
        bus.register(EBClientTickEvent.class, SoundLoop::onClientTick);
        bus.register(EBClientTickEvent.class, EBKeyBinding::onClientTick);
    }

}
