package com.electroblob.wizardry.cca;

import com.electroblob.wizardry.WizardryMainMod;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import org.jetbrains.annotations.NotNull;

/** Cardinal Components Entry Point */
public class EBFabricComponents implements EntityComponentInitializer {
    public static final ComponentKey<FabricPlayerWizardDataHolder> WIZARD_DATA =
            ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("wizard_data"), FabricPlayerWizardDataHolder.class);

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(WIZARD_DATA, FabricPlayerWizardDataHolder::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
