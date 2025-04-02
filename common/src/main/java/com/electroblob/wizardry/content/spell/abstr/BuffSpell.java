package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class BuffSpell extends Spell {

    protected final Supplier<MobEffect>[] effects;
    protected Set<MobEffect> potionSet = new java.util.HashSet<>();
    protected final float r, g, b;
    protected float particleCount = 10;

    @SafeVarargs
    public BuffSpell(float r, float g, float b, Supplier<MobEffect>... effects){
        this.effects = effects;
        this.r = r;
        this.g = g;
        this.b = b;
        for(Supplier<MobEffect> effect : effects){
            potionSet.add(effect.get());
        }
    }

    public BuffSpell particleCount(int particleCount){
        this.particleCount = particleCount;
        return this;
    }

    public Set<MobEffect> getPotionSet(){
        return Collections.unmodifiableSet(potionSet);
    }

    @Override
    protected void perform(Caster caster) {
        if(caster instanceof Player player){
            if(applyEffects(player) && player.level().isClientSide){
                spawnParticles(player.getCommandSenderWorld(), player);
            }
        }
    }


    protected boolean applyEffects(LivingEntity caster){
        for(MobEffect effect : potionSet){
            caster.addEffect(new MobEffectInstance(effect, effect.isInstantenous() ? this.property(getEffectStrengthProperty(effect)) :
                    this.property(getEffectDurationProperty(effect)),
                    1, false, true
            ));
        }

        return true;
    }

    protected void spawnParticles(Level world, LivingEntity caster){
        for(int i = 0; i < particleCount; i++){
            double x = caster.xo + world.random.nextDouble() * 2 - 1;
            double y = caster.yo + caster.getEyeHeight() - 0.5 + world.random.nextDouble();
            double z = caster.zo + world.random.nextDouble() * 2 - 1;
            ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(0, 0.1, 0).color(r, g, b).spawn(world);
        }
        ParticleBuilder.create(EBParticles.BUFF).entity(caster).color(r, g, b).spawn(world);
    }
    
    public static SpellProperty<Integer> getEffectDurationProperty(MobEffect effect){
        return SpellProperty.intProperty(effect.getDescriptionId() + "_duration");
    }

    public static SpellProperty<Integer> getEffectStrengthProperty(MobEffect effect){
        return SpellProperty.intProperty(effect.getDescriptionId() + "_strength");
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.empty();
    }
}
