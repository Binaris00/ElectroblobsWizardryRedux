package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.content.advancement.SpellDiscoveryTrigger;
import com.electroblob.wizardry.core.mixin.accessor.CriteriaAccessor;

public final class EBAdvancementTriggers {
    private EBAdvancementTriggers(){}

    public static final SpellDiscoveryTrigger DISCOVER_SPELL = new SpellDiscoveryTrigger();

    public static void register(){
        CriteriaAccessor.callRegister(DISCOVER_SPELL);
    }
}
