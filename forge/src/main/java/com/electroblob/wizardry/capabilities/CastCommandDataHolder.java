package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.CastCommandData;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.network.PlayerCapabilitySyncPacketS2C;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CastCommandDataHolder implements INBTSerializable<CompoundTag>, CastCommandData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("cast_command_data");
    public static final Capability<CastCommandDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    private Spell castCommandSpell = Spells.NONE;
    private int castCommandTick;
    private SpellModifiers castCommandModifiers = new SpellModifiers();
    private int castCommandDuration;

    private final Player provider;

    public CastCommandDataHolder(Player player) {
        this.provider = player;
    }

    public void sync(){
        if (!this.provider.level().isClientSide()) {
            CompoundTag tag = this.serializeNBT();

            PlayerCapabilitySyncPacketS2C packet = new PlayerCapabilitySyncPacketS2C(PlayerCapabilitySyncPacketS2C.CapabilityType.CAST_COMMAND, tag);
            Services.NETWORK_HELPER.sendTo((ServerPlayer) this.provider, packet);
        }
    }

    @Override
    public void startCastingContinuousSpell(Spell spell, SpellModifiers modifiers, int duration) {
        this.castCommandSpell = spell;
        this.castCommandModifiers = modifiers;
        this.castCommandDuration = duration;
        sync();
    }

    @Override
    public void stopCastingContinuousSpell() {
        this.castCommandSpell = Spells.NONE;
        this.castCommandTick = 0;
        this.castCommandModifiers.reset();
        sync();
    }

    @Override
    public void tick() {
        if (!isCommandCasting()) return;

        if ((this.castCommandSpell == null || this.castCommandSpell instanceof NoneSpell) || this.castCommandSpell.isInstantCast()) {
            this.castCommandTick = 0;
        }

        if (castCommandTick >= castCommandDuration) {
            this.stopCastingContinuousSpell();
            return;
        }

        if (WizardryEventBus.getInstance().fire(new SpellCastEvent.Tick(SpellCastEvent.Source.COMMAND, castCommandSpell, provider, castCommandModifiers, castCommandTick))) {
            this.stopCastingContinuousSpell();
            return;
        }

        if (this.castCommandSpell.cast(new PlayerCastContext(provider.level(), provider, InteractionHand.MAIN_HAND, this.castCommandTick, this.castCommandModifiers)) && this.castCommandTick == 0) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(SpellCastEvent.Source.COMMAND, castCommandSpell, provider, castCommandModifiers));
        }

        castCommandTick++;
        sync();
    }

    @Override
    public boolean isCommandCasting() {
        return this.castCommandSpell != null && this.castCommandSpell != Spells.NONE;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("castCommandSpell", StringTag.valueOf(castCommandSpell.getLocation().toString()));
        tag.putInt("castCommandDuration", castCommandDuration);
        tag.putInt("castCommandTick", castCommandTick);
        tag.put("castCommandModifiers", castCommandModifiers.toNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ResourceLocation spellLocation = ResourceLocation.tryParse(tag.getString("castCommandSpell"));
        Spell spell = Services.REGISTRY_UTIL.getSpell(spellLocation);
        if (spell != null) {
            this.castCommandSpell = spell;
        } else {
            this.castCommandSpell = Spells.NONE;
        }
        this.castCommandDuration = tag.getInt("castCommandDuration");
        this.castCommandTick = tag.getInt("castCommandTick");
        Tag modifiersTag = tag.get("castCommandModifiers");
        if (modifiersTag instanceof CompoundTag) {
            this.castCommandModifiers = SpellModifiers.fromNBT((CompoundTag) modifiersTag);
        } else {
            this.castCommandModifiers = new SpellModifiers();
        }
    }

    public void copyFrom(@NotNull CastCommandDataHolder old) {
        this.castCommandSpell = old.castCommandSpell;
        this.castCommandTick = old.castCommandTick;
        this.castCommandModifiers = old.castCommandModifiers;
        this.castCommandDuration = old.castCommandDuration;
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<CastCommandDataHolder> dataHolder;

        public Provider(Player player) {
            this.dataHolder = LazyOptional.of(() -> new CastCommandDataHolder(player));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return CastCommandDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return dataHolder.orElseThrow(NullPointerException::new).serializeNBT();
        }
        @Override
        public void deserializeNBT(CompoundTag arg) {
            dataHolder.orElseThrow(NullPointerException::new).deserializeNBT(arg);
        }
    }
}
