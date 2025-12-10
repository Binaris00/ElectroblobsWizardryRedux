package com.electroblob.wizardry.client;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;

/**
 * Client-only class that handles actual particle spawning.
 * This class should only be loaded on the client side to avoid classloading issues.
 */
public class ParticleSpawner {

    /**
     * Spawns a particle on the client side using the provided data.
     * This method should only be called from client-side code.
     */
    public static void spawnClientParticle(ParticleBuilder.ParticleData data) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        SimpleParticleType particle = (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(data.particleType);

        BiFunction<ClientLevel, Vec3, ParticleWizardry> factory = ParticleWizardry.PROVIDERS.get(particle);
        ParticleWizardry particleWizardry = factory == null ? null : factory.apply(clientLevel, new Vec3(data.x, data.y, data.z));

        if (particleWizardry == null) {
            EBLogger.error("Failed to spawn particle of type - {} - are you sure it exists?", data.particleType);
            return;
        }

        // Set the properties
        if (!Double.isNaN(data.vx) && !Double.isNaN(data.vy) && !Double.isNaN(data.vz))
            particleWizardry.setParticleSpeed(data.vx, data.vy, data.vz);
        if (data.r >= 0 && data.g >= 0 && data.b >= 0)
            particleWizardry.setColor(data.r, data.g, data.b);
        if (data.fr >= 0 && data.fg >= 0 && data.fb >= 0)
            particleWizardry.setFadeColour(data.fr, data.fg, data.fb);
        if (data.lifetime >= 0)
            particleWizardry.setLifetime(data.lifetime);
        if (data.radius > 0)
            particleWizardry.setSpin(data.radius, data.rpt);
        if (!Float.isNaN(data.yaw) && !Float.isNaN(data.pitch))
            particleWizardry.setFacing(data.yaw, data.pitch);
        if (data.seed != 0)
            particleWizardry.setSeed(data.seed);
        if (!Double.isNaN(data.tvx) && !Double.isNaN(data.tvy) && !Double.isNaN(data.tvz))
            particleWizardry.setTargetVelocity(data.tvx, data.tvy, data.tvz);
        if (data.length > 0)
            particleWizardry.setLength(data.length);

        particleWizardry.scale(data.scale);
        particleWizardry.setGravity(data.gravity);
        particleWizardry.setShaded(data.shaded);
        particleWizardry.setCollisions(data.collide);

        // Handle entity references
        if (data.entityId != null) {
            Entity entity = clientLevel.getEntity(data.entityId);
            if (entity == null) {
                EBLogger.error("Failed to set entity for particle of type - {} - entity ID {} not found",
                        data.particleType, data.entityId);
            }
            particleWizardry.setEntity(entity);
        }

        particleWizardry.setTargetPosition(data.tx, data.ty, data.tz);

        if (data.targetId != null) {
            Entity target = clientLevel.getEntity(data.targetId);
            particleWizardry.setTargetEntity(target);
        }

        Minecraft.getInstance().particleEngine.add(particleWizardry);
    }
}
