package com.electroblob.wizardry.cca;

import com.electroblob.wizardry.api.PlayerWizardData;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/** Check {@link PlayerWizardData}, this class is just the implementation to load-save the player's wizard data */
public class FabricPlayerWizardDataHolder implements FPWizardDataComponent, AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    PlayerWizardData wizardData = new PlayerWizardData();
    private final Player provider;

    public FabricPlayerWizardDataHolder(Player provider) {
        this.provider = provider;
    }

    // Quick example to show how to sync
    public void onSync(PlayerWizardData newWizardData){
        this.wizardData = newWizardData;
        EBFabricComponents.WIZARD_DATA.sync(this.provider);
    }

    @Override
    public void clientTick() {
        this.wizardData.updateContinuousSpellCasting(provider);
    }

    @Override
    public void serverTick() {
        this.wizardData.updateContinuousSpellCasting(provider);
    }

    @Override public PlayerWizardData getWizardData() {
        return wizardData;
    }

    @Override public void readFromNbt(@NotNull CompoundTag tag) {
        this.wizardData = wizardData.deserializeNBT(tag);
    }

    @Override public void writeToNbt(@NotNull CompoundTag tag) {
        wizardData.serializeNBT(tag);
    }
}
