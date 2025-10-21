package com.electroblob.wizardry.cca;

import com.electroblob.wizardry.api.ConjureItemData;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;

public interface ConjureItemComponent extends ComponentV3 {
    ConjureItemData getConjureItemData();
}
