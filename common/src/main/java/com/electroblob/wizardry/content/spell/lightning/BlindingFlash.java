package com.electroblob.wizardry.content.spell.lightning;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BlindingFlash extends AreaEffectSpell {
    public BlindingFlash() {
        this.alwaysSucceed(true);
    }

    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        if (target instanceof LivingEntity) {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,
                    (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())), 0));
        }

        return true;
    }

    @Override
    protected void spawnParticleEffect(CastContext ctx, Vec3 origin, double radius) {
        if (ctx.caster() != null) origin = origin.add(0, ctx.caster().getBbHeight() + 1, 0);
        ParticleBuilder.create(EBParticles.SPHERE).pos(origin).scale((float) radius * 0.8f).spawn(ctx.world());
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.LIGHTNING, SpellType.ATTACK, SpellAction.POINT_UP, 20, 0, 60)
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.EFFECT_DURATION, 300)
                .build();
    }
}
