package com.electroblob.wizardry.cca;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.cca.blockentity.ArcaneLockDataHolder;
import com.electroblob.wizardry.cca.entity.MinionDataHolder;
import com.electroblob.wizardry.cca.player.CastCommandDataHolder;
import com.electroblob.wizardry.cca.player.SpellManagerDataHolder;
import com.electroblob.wizardry.cca.player.WizardDataHolder;
import com.electroblob.wizardry.cca.stack.ConjureDataHolder;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.cca.stack.ImbuementEnchantDataHolder;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

/**
 * Cardinal Components Entry Point, we use this for entity data and item data
 */
public class EBComponents implements EntityComponentInitializer, ItemComponentInitializer, BlockComponentInitializer {
    public static final ComponentKey<WizardDataHolder> WIZARD_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("wizard_data"), WizardDataHolder.class);
    public static final ComponentKey<CastCommandDataHolder> CAST_COMMAND_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("cast_command_data"), CastCommandDataHolder.class);
    public static final ComponentKey<SpellManagerDataHolder> SPELL_MANAGER_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("spell_manager_data"), SpellManagerDataHolder.class);

    public static final ComponentKey<MinionDataHolder> MINION_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("minion_data"), MinionDataHolder.class);
    public static final ComponentKey<ConjureDataHolder> CONJURE = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("conjure"), ConjureDataHolder.class);
    public static final ComponentKey<ImbuementEnchantDataHolder> IMBUEMENT_ENCHANTS = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("imbuement_enchants"), ImbuementEnchantDataHolder.class);

    public static final ComponentKey<ArcaneLockDataHolder> ARCANE_LOCK_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(WizardryMainMod.location("arcane_lock"), ArcaneLockDataHolder.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(WIZARD_DATA, WizardDataHolder::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(CAST_COMMAND_DATA, CastCommandDataHolder::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(SPELL_MANAGER_DATA, SpellManagerDataHolder::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerFor(Mob.class, MINION_DATA, MinionDataHolder::new);

    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
        registry.register(ConjureItemSpell::isSummonableItem, CONJURE, ConjureDataHolder::new);
        registry.register(itemStack -> true, IMBUEMENT_ENCHANTS, ImbuementEnchantDataHolder::new);
    }

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry blockComponentFactoryRegistry) {
        blockComponentFactoryRegistry.registerFor(BaseContainerBlockEntity.class, ARCANE_LOCK_DATA, ArcaneLockDataHolder::new);
    }
}
