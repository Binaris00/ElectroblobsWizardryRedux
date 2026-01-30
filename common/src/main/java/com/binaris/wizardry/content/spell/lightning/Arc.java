package com.binaris.wizardry.content.spell.lightning;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Arc extends RaySpell {

    public Arc() {
        this.aimAssist(0.6f);
        this.soundValues(1, 1.7f, 0.2f);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) {
            return false;
        }

        if (ctx.world().isClientSide) {
            ParticleBuilder.create(EBParticles.LIGHTNING).entity(ctx.caster())
                    .pos(ctx.caster() != null ? origin.subtract(ctx.caster().position()) : origin).target(target).spawn(ctx.world());
            ParticleBuilder.spawnShockParticles(ctx.world(), target.xo, target.yo + target.getBbHeight() / 2, target.zo);
        }

        if (MagicDamageSource.isEntityImmune(EBDamageSources.SHOCK, target)) {
            if (!ctx.world().isClientSide && ctx.caster() instanceof Player player)
                player.displayClientMessage(Component.translatable("spell.resist", target.getName(), this.getDescriptionFormatted()), true);
        } else {
            target.hurt(MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.SHOCK),
                    property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
        }

        return true;

    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.LIGHTNING, SpellType.ATTACK, SpellAction.POINT, 5, 0, 15)
                .add(DefaultProperties.RANGE, 3F)
                .add(DefaultProperties.DAMAGE, 8F)
                .add(DefaultProperties.SENSIBLE, true)
                .build();
    }
}
