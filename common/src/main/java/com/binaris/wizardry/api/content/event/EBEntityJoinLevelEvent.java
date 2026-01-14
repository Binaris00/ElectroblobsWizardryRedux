package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EBEntityJoinLevelEvent extends WizardryCancelableEvent {
    Entity entity;
    Level level;

    public EBEntityJoinLevelEvent(Entity entity, Level level) {
        this.entity = entity;
        this.level = level;
    }

    public Entity getEntity() {
        return entity;
    }

    public Level getLevel() {
        return level;
    }
}
