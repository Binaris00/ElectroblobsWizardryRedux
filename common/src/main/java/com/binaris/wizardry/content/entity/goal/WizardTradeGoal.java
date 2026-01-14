package com.binaris.wizardry.content.entity.goal;

import com.binaris.wizardry.content.entity.living.Wizard;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/** Stops the wizard from moving when trading. */
public class WizardTradeGoal extends Goal {
    private final Wizard wizard;

    public WizardTradeGoal(Wizard wizard) {
        this.wizard = wizard;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.wizard.isAlive()) return false;
        else if (this.wizard.isInWater()) return false;
        else if (!this.wizard.onGround()) return false;
        else if (this.wizard.hurtMarked) return false;
        Player player = this.wizard.getTradingPlayer();

        if (player == null) return false;
        else return !(this.wizard.distanceToSqr(player) > 16.0D);

    }

    @Override
    public void start() {
        this.wizard.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.wizard.setTradingPlayer(null);
    }
}
