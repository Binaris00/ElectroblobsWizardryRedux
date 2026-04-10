package com.binaris.wizardry.client;

import com.binaris.wizardry.api.client.particle.ParticleTargeted;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;

/**
 * Client-only class that handles actual particle spawning. This class should only be loaded on the client side to
 * avoid classloading issues.
 */
public final class ParticleSpawner {

    /**
     * Spawns a particle on the client side using the provided data. This method should only be called from client-side
     * code.
     */
    public static void spawnClientParticle(ParticleBuilder.ParticleData data) {
        ClientLevel level = Minecraft.getInstance().level;
        SimpleParticleType type = (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(data.particleType);

        BiFunction<ClientLevel, Vec3, ParticleWizardry> factory = ParticleWizardry.PROVIDERS.get(type);
        if (factory == null) {
            EBLogger.error("Failed to spawn particle: {} (type not found)", data.particleType);
            return;
        }

        ParticleWizardry p = factory.apply(level, new Vec3(data.x, data.y, data.z));

        if (!Double.isNaN(data.vx + data.vy + data.vz)) p.setParticleSpeed(data.vx, data.vy, data.vz);
        if (data.r >= 0) p.setColor(data.r, data.g, data.b);
        if (data.fr >= 0) p.setFadeColour(data.fr, data.fg, data.fb);
        if (data.lifetime >= 0) p.setLifetime(data.lifetime);
        if (data.radius > 0) p.setSpin(data.radius, data.rpt);
        if (!Float.isNaN(data.yaw + data.pitch)) p.setFacing(data.yaw, data.pitch);

        p.scale(data.scale);
        p.setGravity(data.gravity);
        p.setShaded(data.shaded);
        p.setCollisions(data.collide);
        if (data.seed != 0) p.setSeed(data.seed);

        if (data.entityId != null) p.setEntity(level.getEntity(data.entityId));

        if (p instanceof ParticleTargeted pTarget) {
            if (!Double.isNaN(data.tvx + data.tvy + data.tvz)) pTarget.setTargetVelocity(data.tvx, data.tvy, data.tvz);
            if (data.targetId != null) pTarget.setTargetEntity(level.getEntity(data.targetId));
            if (data.length > 0) pTarget.setLength(data.length);
            pTarget.setTargetPosition(data.tx, data.ty, data.tz);
        }

        Minecraft.getInstance().particleEngine.add(p);
    }

    private ParticleSpawner() {}
}
