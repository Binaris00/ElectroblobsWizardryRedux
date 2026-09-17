package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.entity.player.Player;

public class EBAdvancementEvent extends WizardryEvent {
    private final Player player;
    private final Advancement advancement;

    public EBAdvancementEvent(Player player, Advancement advancement) {
        this.player = player;
        this.advancement = advancement;
    }

    public Player player() {
        return player;
    }

    public Advancement advancement() {
        return advancement;
    }
}
