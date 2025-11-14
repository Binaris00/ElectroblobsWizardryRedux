package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleTargeted;
import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleGuardianBeam extends ParticleTargeted {
    private static final float THICKNESS = 0.15f;

    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft:textures/entity/guardian_beam.png");

    public ParticleGuardianBeam(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.setLifetime(3);
        this.quadSize = 1;
    }

    @Override
    protected void draw(PoseStack stack, Tesselator tessellator, float length, float tickDelta) {
        float scale = this.quadSize;

        RenderSystem.setShaderTexture(0, TEXTURE);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        stack.pushPose();

        stack.mulPose(Axis.ZP.rotationDegrees(Minecraft.getInstance().player.tickCount + tickDelta));

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float t = THICKNESS * scale;
        float v1 = 2 * (age + tickDelta) / lifetime;
        float v2 = v1 + length * 2;

        buffer.vertex(stack.last().pose(), -t, 0, 0).uv(0, v1).color(rCol, gCol, bCol, 1).endVertex();
        buffer.vertex(stack.last().pose(), t, 0, 0).uv(0.5f, v1).color(rCol, gCol, bCol, 1).endVertex();
        buffer.vertex(stack.last().pose(), t, 0, length).uv(0.5f, v2).color(rCol, gCol, bCol, 1).endVertex();
        buffer.vertex(stack.last().pose(), -t, 0, length).uv(0, v2).color(rCol, gCol, bCol, 1).endVertex();

        buffer.vertex(stack.last().pose(), 0, -t, 0).uv(0, v1).color(rCol, gCol, bCol, 1).endVertex();
        buffer.vertex(stack.last().pose(), 0, t, 0).uv(0.5f, v1).color(rCol, gCol, bCol, 1).endVertex();
        buffer.vertex(stack.last().pose(), 0, t, length).uv(0.5f, v2).color(rCol, gCol, bCol, 1).endVertex();
        buffer.vertex(stack.last().pose(), 0, -t, length).uv(0, v2).color(rCol, gCol, bCol, 1).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        stack.popPose();
    }

    @Deprecated
    public static class GuardianBeamProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public GuardianBeamProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleGuardianBeam(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleGuardianBeam(world, x, y, z, spriteProvider);
        }
    }
}
