package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GroupHeal extends AreaEffectSpell {
    public GroupHeal(){
        this.targetAllies(true);
    }

    @Override
    protected boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount) {
        if(target.getHealth() < target.getMaxHealth() && target.getHealth() > 0){
            Heal.heal(target, property(DefaultProperties.HEALTH) * ctx.modifiers().get(SpellModifiers.POTENCY));

            if(ctx.world().isClientSide) ParticleBuilder.spawnHealParticles(ctx.world(), target);
            playSound(ctx.world(), target, 0, -1);
            return true;
        }

        return false;
    }


    @Override
    protected void spawnParticleEffect(CastContext ctx, Vec3 origin, double radius) {

    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellType.DEFENCE, SpellAction.POINT_UP, 35, 10, 150)
                .add(DefaultProperties.EFFECT_RADIUS, 5)
                .add(DefaultProperties.HEALTH, 6F)
                .build();
    }
}
