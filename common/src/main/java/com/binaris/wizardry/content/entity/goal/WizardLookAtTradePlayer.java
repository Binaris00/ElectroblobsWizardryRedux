package com.binaris.wizardry.content.entity.goal;

import com.binaris.wizardry.content.entity.living.Wizard;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;

/** Makes the wizard look at the player it is trading with. */
public class WizardLookAtTradePlayer extends LookAtPlayerGoal {
    private final Wizard wizard;

    public WizardLookAtTradePlayer(Wizard wizard) {
        super(wizard, Player.class, 8.0F);
        this.wizard = wizard;
    }

    @Override
    public boolean canUse() {
        if (this.wizard.isTrading()) {
            this.lookAt = this.wizard.getTradingPlayer();
            return true;
        }

        return false;
    }
}
