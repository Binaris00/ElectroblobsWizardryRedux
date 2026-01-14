package com.binaris.wizardry.client;

import com.binaris.wizardry.api.content.event.EBClientTickEvent;
import com.binaris.wizardry.api.content.event.EBLivingTick;
import com.binaris.wizardry.client.sound.SoundLoop;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.setup.registries.client.EBKeyBinding;

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
