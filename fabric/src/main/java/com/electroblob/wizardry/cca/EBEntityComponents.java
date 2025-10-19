package com.electroblob.wizardry.cca;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.ConjureItemData;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/** Cardinal Components Entry Point */
public class EBEntityComponents implements EntityComponentInitializer, ItemComponentInitializer {
    public static final ComponentKey<FabricPlayerWizardDataHolder> WIZARD_DATA =
            ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("wizard_data"), FabricPlayerWizardDataHolder.class);

    public static final ComponentKey<FabricMinionDataHolder> MINION_DATA =
            ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("minion_data"), FabricMinionDataHolder.class);
    public static final ComponentKey<FabricConjureItemDataHolder> CONJURE_ITEM =
            ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("conjure_item"), FabricConjureItemDataHolder.class);


    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(WIZARD_DATA, FabricPlayerWizardDataHolder::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerFor(Mob.class, MINION_DATA, FabricMinionDataHolder::new);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.register(ConjureItemData::applyItem, CONJURE_ITEM, FabricConjureItemDataHolder::new);
    }
}
