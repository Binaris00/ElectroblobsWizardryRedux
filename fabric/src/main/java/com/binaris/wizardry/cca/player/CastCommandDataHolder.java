package com.binaris.wizardry.cca.player;

import com.binaris.wizardry.api.content.data.CastCommandData;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.NoneSpell;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.cca.EBComponents;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class CastCommandDataHolder implements CastCommandData, ComponentV3, AutoSyncedComponent {
    private final Player provider;

    private Spell castCommandSpell = Spells.NONE;
    private int castCommandTick;
    private SpellModifiers castCommandModifiers = new SpellModifiers();
    private int castCommandDuration;

    public CastCommandDataHolder(Player provider) {
        this.provider = provider;
    }

    private void sync() {
        EBComponents.CAST_COMMAND_DATA.sync(provider);
    }

    @Override
    public void startCastingContinuousSpell(Spell spell, SpellModifiers modifiers, int duration) {
        this.castCommandSpell = spell;
        this.castCommandModifiers = modifiers;
        this.castCommandDuration = duration;
        sync();
        // TODO PACKET SENDING FOR CONTINUOUS SPELL SYNC TO CLIENT
    }

    @Override
    public void stopCastingContinuousSpell() {
        this.castCommandSpell = Spells.NONE;
        this.castCommandTick = 0;
        this.castCommandModifiers.reset();
        sync();
        // TODO PACKET SENDING FOR CONTINUOUS SPELL SYNC TO CLIENT
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
    public void readFromNbt(@NotNull CompoundTag tag) {
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
            this.castCommandModifiers = SpellModifiers.fromTag((CompoundTag) modifiersTag);
        } else {
            this.castCommandModifiers = new SpellModifiers();
        }
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        tag.put("castCommandSpell", StringTag.valueOf(castCommandSpell.getLocation().toString()));
        tag.putInt("castCommandDuration", castCommandDuration);
        tag.putInt("castCommandTick", castCommandTick);
        tag.put("castCommandModifiers", castCommandModifiers.toTag());
    }
}
