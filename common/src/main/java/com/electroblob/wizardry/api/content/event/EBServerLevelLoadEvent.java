package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryEvent;
import net.minecraft.world.level.Level;

public class EBServerLevelLoadEvent extends WizardryEvent {
    Level level;

    public EBServerLevelLoadEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
