package com.binaris.wizardry.client.particle;

import com.binaris.wizardry.api.client.particle.ParticleWizardry;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import static com.binaris.wizardry.WizardryMainMod.MOD_ID;

/**
 * Particle Buff is the only particle in Electroblob's Wizardry (at least on Alpha release) that's using the linking
 * mechanism from ParticleWizardry to follow an entity. It represents the buff effect visually by floating upwards
 * from the entity it's linked to. The particle uses a custom texture and rendering method to achieve its appearance.
 */
// This render logic is one of the worst I've ever written. So any improvements would be greatly appreciated, specially
// regarding how to handle the linked entity position better, because I'm sure that part can be improved.
public class ParticleBuff extends ParticleWizardry {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/particle/buff.png");
    private final boolean mirror;

    public ParticleBuff(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.xd = 0;
        this.yd = 0.162;
        this.zd = 0;
        this.mirror = random.nextBoolean();
        this.setLifetime(15);
        this.setGravity(false);
        this.hasPhysics = false;
        this.setColor(1, 1, 1);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > this.lifetime / 2) {
            this.alpha = 2f - 2f * (float) this.age / (float) this.lifetime;
        }
    }

    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        updateEntityLinking(partialTicks);

        RenderSystem.enableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        Vec3 cameraPos = camera.getPosition();
        float x, y, z;

        if (entity != null) {
            Vec3 entityPos = entity.getPosition(partialTicks);
            x = (float) (entityPos.x + relativeX - cameraPos.x);
            y = (float) (entityPos.y + relativeY - cameraPos.y);
            z = (float) (entityPos.z + relativeZ - cameraPos.z);
        } else {
            x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x);
            y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y);
            z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z);
        }

        float ageProgress = (float) this.age / (float) this.lifetime;
        float f = 0.875f - 0.125f * Mth.floor(ageProgress * 8 - 0.000001f);
        float g = f + 0.125f;

        float textureOffset = (this.age + partialTicks) / (float) this.lifetime * -2;

        float scale = 0.6f;
        float yScale = 0.7f * scale;
        float dx = mirror ? -scale : scale;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);

        vertex(buffer, x - dx, y - yScale, z - scale, 0 + textureOffset, g);
        vertex(buffer, x - dx, y + yScale, z - scale, 0 + textureOffset, f);
        vertex(buffer, x + dx, y - yScale, z - scale, 0.25f + textureOffset, g);
        vertex(buffer, x + dx, y + yScale, z - scale, 0.25f + textureOffset, f);
        vertex(buffer, x + dx, y - yScale, z + scale, 0.5f + textureOffset, g);
        vertex(buffer, x + dx, y + yScale, z + scale, 0.5f + textureOffset, f);
        vertex(buffer, x - dx, y - yScale, z + scale, 0.75f + textureOffset, g);
        vertex(buffer, x - dx, y + yScale, z + scale, 0.75f + textureOffset, f);
        vertex(buffer, x - dx, y - yScale, z - scale, 1.0f + textureOffset, g);
        vertex(buffer, x - dx, y + yScale, z - scale, 1.0f + textureOffset, f);

        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void vertex(BufferBuilder buffer, float x, float y, float z, float u, float v) {
        buffer.vertex(x, y, z)
                .uv(u, v)
                .color(rCol, gCol, bCol, alpha)
                .endVertex();
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 15728880; // Full brightness
    }

    public static class BuffProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public BuffProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleBuff(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleBuff(world, x, y, z, spriteProvider);
        }
    }
}