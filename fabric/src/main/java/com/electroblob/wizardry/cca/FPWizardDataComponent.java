package com.electroblob.wizardry.cca;

import com.electroblob.wizardry.api.PlayerWizardData;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;

public interface FPWizardDataComponent extends ComponentV3 {
    PlayerWizardData getWizardData();
}
