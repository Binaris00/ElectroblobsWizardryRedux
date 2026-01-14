package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EBPlayerBreakBlockEvent extends WizardryCancelableEvent {
    private final Player player;
    private final Level level;
    private final BlockPos pos;

    public EBPlayerBreakBlockEvent(Player player, Level level, BlockPos pos) {
        this.player = player;
        this.level = level;
        this.pos = pos;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }
}

