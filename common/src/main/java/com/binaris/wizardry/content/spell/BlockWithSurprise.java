package com.binaris.wizardry.content.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.core.networking.c2s.BlockUsePacketC2S;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BlockWithSurprise extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.world().isClientSide) {
            ctx.world().addParticle(ParticleTypes.EXPLOSION_EMITTER, ctx.caster().getX(), ctx.caster().getY() + ctx.caster().getBbHeight() / 2, ctx.caster().getZ(), 0, 0, 0);
            ctx.caster().sendSystemMessage(Component.literal("Surprise!"));
            Services.NETWORK_HELPER.sendToServer(new BlockUsePacketC2S(ctx.caster().getOnPos()));
        }

        return true;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.NECROMANCY, SpellType.BUFF, SpellAction.POINT, 0, 0, 0)
                .add(DefaultProperties.SENSIBLE, true)
                .build();
    }
}
