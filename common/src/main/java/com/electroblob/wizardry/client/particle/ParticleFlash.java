package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParticleFlash extends ParticleWizardry {
    public ParticleFlash(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.quadSize = 0.6f;
        this.lifetime = 6;
    }

    @Override
    protected void drawParticle(VertexConsumer buffer, Camera camera, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        //not sure necessary
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        Vec3 vec3 = camera.getPosition();
        float f4 = 0.1f * quadSize * Mth.sin(((float) this.age + partialTicks - 1.0F) / lifetime * (float) Math.PI);
        this.setAlpha(0.6F - ((float) this.age + partialTicks - 1.0F) / lifetime * 0.5F);

        float f5 = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f6 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f7 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());

        int i = this.getLightColor(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        buffer.vertex(f5 - rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 - rotationYZ * f4 - rotationXZ * f4).uv(0.5F, 0.375F).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j, k).endVertex();
        buffer.vertex(f5 - rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 - rotationYZ * f4 + rotationXZ * f4).uv(0.5F, 0.125F).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j, k).endVertex();
        buffer.vertex(f5 + rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 + rotationYZ * f4 + rotationXZ * f4).uv(0.25F, 0.125F).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j, k).endVertex();
        buffer.vertex(f5 + rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 + rotationYZ * f4 - rotationXZ * f4).uv(0.25F, 0.375F).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j, k).endVertex();
    }


    public static class FlashProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public FlashProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleFlash(world, x, y, z, spriteProvider);
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleFlash(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }
    }
}
