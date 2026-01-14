package com.binaris.wizardry.client.effect;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.data.ArcaneLockData;
import com.binaris.wizardry.api.content.util.GeometryUtil;
import com.binaris.wizardry.core.platform.Services;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX;

public final class ArcaneLockRender {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[8];
    private static final int RENDER_DISTANCE_CHUNKS = 8; // Adjust as needed
    private static final int MAX_RENDER_DISTANCE_SQ = 4096; // 64 blocks squared

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/block/arcane_lock_" + i + ".png");
        }
    }

    private ArcaneLockRender() {
    }

    public static void render(Camera camera, PoseStack poseStack, float partialTicks) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        Level world = Minecraft.getInstance().level;
        if (world == null) return;

        Vec3 origin = player.getEyePosition(partialTicks);
        Vec3 cameraPos = camera.getPosition();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        boolean renderStarted = false;

        // Get player's chunk position
        ChunkPos playerChunkPos = new ChunkPos(BlockPos.containing(origin));

        // Iterate through nearby chunks
        for (int chunkX = playerChunkPos.x - RENDER_DISTANCE_CHUNKS; chunkX <= playerChunkPos.x + RENDER_DISTANCE_CHUNKS; chunkX++) {
            for (int chunkZ = playerChunkPos.z - RENDER_DISTANCE_CHUNKS; chunkZ <= playerChunkPos.z + RENDER_DISTANCE_CHUNKS; chunkZ++) {

                if (!world.hasChunk(chunkX, chunkZ)) continue;

                LevelChunk chunk = world.getChunk(chunkX, chunkZ);

                // Iterate through all block entities in this chunk
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof BaseContainerBlockEntity containerBlock)) continue;

                    BlockPos pos = blockEntity.getBlockPos();
                    double distSq = pos.distToCenterSqr(origin.x, origin.y, origin.z);

                    // Check render distance
                    if (distSq > MAX_RENDER_DISTANCE_SQ) continue; // 64 blocks squared

                    ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(containerBlock);
                    if (data == null || !data.isArcaneLocked()) continue;

                    // Initialize rendering on first locked block
                    if (!renderStarted) {
                        renderStarted = true;

                        poseStack.pushPose();
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.disableCull();
                        RenderSystem.depthMask(false);

                        // Set shader and texture
                        RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        int textureIndex = (player.tickCount % (TEXTURES.length * 2)) / 2;
                        RenderSystem.setShaderTexture(0, TEXTURES[textureIndex]);
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

                        // Translate to camera-relative coordinates
                        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

                        buffer.begin(VertexFormat.Mode.QUADS, POSITION_TEX);
                    }

                    // Get the bounding box for this block
                    AABB boundingBox = world.getBlockState(pos)
                            .getShape(world, pos)
                            .bounds()
                            .inflate(0.05)
                            .move(pos);

                    Vec3[] vertices = GeometryUtil.getVertices(boundingBox);
                    Matrix4f matrix = poseStack.last().pose();

                    // Render all six faces
                    drawFace(buffer, matrix, vertices[0], vertices[1], vertices[3], vertices[2], 0, 0, 1, 1); // Bottom
                    drawFace(buffer, matrix, vertices[6], vertices[7], vertices[2], vertices[3], 0, 0, 1, 1); // South
                    drawFace(buffer, matrix, vertices[5], vertices[6], vertices[1], vertices[2], 0, 0, 1, 1); // East
                    drawFace(buffer, matrix, vertices[4], vertices[5], vertices[0], vertices[1], 0, 0, 1, 1); // North
                    drawFace(buffer, matrix, vertices[7], vertices[4], vertices[3], vertices[0], 0, 0, 1, 1); // West
                    drawFace(buffer, matrix, vertices[5], vertices[4], vertices[6], vertices[7], 0, 0, 1, 1); // Top
                }
            }
        }

        if (renderStarted) {
            BufferUploader.drawWithShader(buffer.end());

            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            poseStack.popPose();
        }
    }

    private static void drawFace(BufferBuilder buffer, Matrix4f matrix, Vec3 topLeft, Vec3 topRight, Vec3 bottomLeft, Vec3 bottomRight, float u1, float v1, float u2, float v2) {
        buffer.vertex(matrix, (float) topLeft.x, (float) topLeft.y, (float) topLeft.z)
                .uv(u1, v1)
                .endVertex();
        buffer.vertex(matrix, (float) topRight.x, (float) topRight.y, (float) topRight.z)
                .uv(u2, v1)
                .endVertex();
        buffer.vertex(matrix, (float) bottomRight.x, (float) bottomRight.y, (float) bottomRight.z)
                .uv(u2, v2)
                .endVertex();
        buffer.vertex(matrix, (float) bottomLeft.x, (float) bottomLeft.y, (float) bottomLeft.z)
                .uv(u1, v2)
                .endVertex();
    }
}