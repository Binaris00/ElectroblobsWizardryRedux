package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EBPlayerUseBlockEvent extends WizardryCancelableEvent {
    private final Player player;
    private final Level level;
    private final BlockPos pos;
    private final InteractionHand hand;

    public EBPlayerUseBlockEvent(Player player, Level level, BlockPos pos, InteractionHand hand) {
        this.player = player;
        this.level = level;
        this.pos = pos;
        this.hand = hand;
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

    public InteractionHand getHand() {
        return hand;
    }
}

