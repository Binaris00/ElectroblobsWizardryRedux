package com.electroblob.wizardry.cca;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * Cardinal Components Entry Point, we use this for entity data and item data
 */
public class EBComponents implements EntityComponentInitializer, ItemComponentInitializer {
    public static final ComponentKey<PlayerWizardDataHolder> WIZARD_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("wizard_data"), PlayerWizardDataHolder.class);
    public static final ComponentKey<MinionDataHolder> MINION_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("minion_data"), MinionDataHolder.class);

    public static final ComponentKey<ConjureDataHolder> CONJURE = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("conjure"), ConjureDataHolder.class);


    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(WIZARD_DATA, PlayerWizardDataHolder::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerFor(Mob.class, MINION_DATA, MinionDataHolder::new);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.register(ConjureItemSpell::isSupportedItem, CONJURE, ConjureDataHolder::new);
    }
}
