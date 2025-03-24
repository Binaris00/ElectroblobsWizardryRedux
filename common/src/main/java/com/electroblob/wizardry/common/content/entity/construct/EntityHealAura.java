package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;

import java.util.List;

public class EntityHealAura extends ScaledConstructEntity {
    public EntityHealAura(EntityType<?> type, Level world) {
        super(type, world);
    }

    public EntityHealAura(Level world){
        super(EBEntities.HEAL_AURA.get(), world);
        // TODO SET SIZE SCALED CONSTRUCT
        //setSize(Spells.healing_aura.getProperty(Spell.EFFECT_RADIUS).floatValue() * 2, 1);
    }

    @Override
    public void tick() {
        super.tick();

        if(this.tickCount % 25 == 1){
            this.playSound(EBSounds.ENTITY_HEAL_AURA_AMBIENT.get(), 0.1f, 1.0f);
        }

        if(this.level().isClientSide){
            for(int i=1; i<3; i++){
                float brightness = 0.5f + (random.nextFloat() * 0.5f);
                double radius = random.nextDouble() * (getBbWidth()/2);
                float angle = random.nextFloat() * (float)Math.PI * 2;
                ParticleBuilder.create(EBParticles.SPARKLE)
                        .pos(this.xo + radius * Mth.cos(angle), this.yo, this.zo + radius * Mth.sin(angle))
                        .velocity(0, 0.05, 0)
                        .time(48 + this.random.nextInt(12))
                        .color(1.0f, 1.0f, brightness)
                        .spawn(level());
            }
            return;
        }

        List<LivingEntity> targets = EntityUtil.getLivingWithinCylinder(getBbWidth()/2, xo, yo, zo, getBbHeight(), level());
        for(LivingEntity target : targets){
            if(!this.isValidTarget(target)) {
                if(target.getHealth() < target.getMaxHealth() && target.tickCount % 5 == 0){
                    target.heal(1 * damageMultiplier);
                }
                continue;
            }

            if(target.getMobType() == MobType.UNDEAD && this.tickCount % 10 == 1) {
                EntityUtil.attackEntityWithoutKnockback(target, this.getCaster() != null ?
                                this.damageSources().indirectMagic(this, getCaster()) : this.damageSources().magic(),
                        1 * damageMultiplier);
            }
        }
    }
}