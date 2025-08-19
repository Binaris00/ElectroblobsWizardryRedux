package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class InvigoratingPresence extends AreaEffectSpell {
    public InvigoratingPresence(){
        this.soundValues(0.7f, 1.2f, 0.4f);
        this.alwaysSucceed(true);
        this.targetAllies(true);
    }

    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        int bonusAmplifier = BuffSpell.getStandardBonusAmplifier(ctx.modifiers().get(SpellModifiers.POTENCY));

        target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())),
                property(DefaultProperties.EFFECT_STRENGTH) + bonusAmplifier));

        return false;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z){
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.03, 0).time(50)
                .color(1, 0.2f, 0.2f).spawn(world);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.HEALING, SpellType.BUFF, SpellAction.POINT_UP, 30, 0, 60)
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.EFFECT_DURATION, 900)
                .add(DefaultProperties.EFFECT_STRENGTH, 1)
                .build();
    }
}
