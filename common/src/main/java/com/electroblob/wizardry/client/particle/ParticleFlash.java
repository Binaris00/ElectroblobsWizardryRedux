package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleFlash extends ParticleWizardry {
    public ParticleFlash(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.quadSize = 0.6f;
        this.lifetime = 4;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.setAlpha(0.6F - ((float) this.age + tickDelta - 1.0F) * 0.25F * 0.5F);
        super.render(vertexConsumer, camera, tickDelta);
    }


    public static class FlashProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public FlashProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleFlash(world, x, y, z, spriteProvider);
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleFlash(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }
    }
}
