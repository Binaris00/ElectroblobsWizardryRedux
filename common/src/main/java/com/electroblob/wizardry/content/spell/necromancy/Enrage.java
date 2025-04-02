package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Enrage extends AreaEffectSpell {
    @Override
    protected boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster, LivingEntity target, int targetCount, int ticksInUse) {
        if (caster instanceof Player player && target instanceof PathfinderMob) {
            target.setLastHurtByMob(player);
        }

        return true;
    }

    @Override
    protected void spawnParticleEffect(Level world, Vec3 origin, double radius, @Nullable LivingEntity caster) {
        if (caster != null) origin = caster.getEyePosition(1);

        for (int i = 0; i < 30; i++) {
            double x = origin.x - 1 + world.random.nextDouble() * 2;
            double y = origin.y - 0.25 + world.random.nextDouble() * 0.5;
            double z = origin.z - 1 + world.random.nextDouble() * 2;
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.9f, 0.1f, 0).spawn(world);
        }
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder().add(DefaultProperties.EFFECT_RADIUS, 8).build();
    }
}
