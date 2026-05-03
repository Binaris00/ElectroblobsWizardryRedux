package com.binaris.wizardry.client.effect;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.data.ArcaneLockData;
import com.binaris.wizardry.api.content.event.EBEntityJoinLevelEvent;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX;

public final class ArcaneLockRender {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[8];
    private static final int RENDER_DISTANCE_CHUNKS = 8;
    private static final int MAX_RENDER_DISTANCE_SQ = 4096;

    private static final Map<BlockPos, AABB> cache = new HashMap<>();
    private static boolean isDirty = true;
    private static ChunkPos lastPlayerChunkPos = null;

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/block/arcane_lock_" + i + ".png");
        }
    }


    public static void markDirty() {
        isDirty = true;
    }

    public static void render(Camera camera, PoseStack poseStack, float partialTicks) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Level world = Minecraft.getInstance().level;
        if (world == null) return;

        Vec3 origin = player.getEyePosition(partialTicks);
        Vec3 cameraPos = camera.getPosition();
        ChunkPos playerChunkPos = new ChunkPos(BlockPos.containing(origin));

        boolean chunkChanged = !playerChunkPos.equals(lastPlayerChunkPos);
        if (isDirty || chunkChanged) {
            rebuildCache(world, origin, playerChunkPos);
            isDirty = false;
            lastPlayerChunkPos = playerChunkPos;
        }

        if (cache.isEmpty()) return;

        int textureIndex = (player.tickCount % (TEXTURES.length * 2)) / 2;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURES[textureIndex]);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        buffer.begin(VertexFormat.Mode.QUADS, POSITION_TEX);

        Matrix4f matrix = poseStack.last().pose();

        for (AABB bb : cache.values()) {
            Vec3[] vertices = GeometryUtil.getVertices(bb);
            drawFace(buffer, matrix, vertices[0], vertices[1], vertices[3], vertices[2]);
            drawFace(buffer, matrix, vertices[6], vertices[7], vertices[2], vertices[3]);
            drawFace(buffer, matrix, vertices[5], vertices[6], vertices[1], vertices[2]);
            drawFace(buffer, matrix, vertices[4], vertices[5], vertices[0], vertices[1]);
            drawFace(buffer, matrix, vertices[7], vertices[4], vertices[3], vertices[0]);
            drawFace(buffer, matrix, vertices[5], vertices[4], vertices[6], vertices[7]);
        }

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }

    private static void rebuildCache(Level world, Vec3 origin, ChunkPos playerChunkPos) {
        cache.clear();

        for (int cx = playerChunkPos.x - RENDER_DISTANCE_CHUNKS; cx <= playerChunkPos.x + RENDER_DISTANCE_CHUNKS; cx++) {
            for (int cz = playerChunkPos.z - RENDER_DISTANCE_CHUNKS; cz <= playerChunkPos.z + RENDER_DISTANCE_CHUNKS; cz++) {
                if (!world.hasChunk(cx, cz)) continue;

                LevelChunk chunk = world.getChunk(cx, cz);
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (!(be instanceof BaseContainerBlockEntity container)) continue;

                    BlockPos pos = be.getBlockPos();
                    if (pos.distToCenterSqr(origin.x, origin.y, origin.z) > MAX_RENDER_DISTANCE_SQ) continue;

                    ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(container);
                    if (data == null || !data.isArcaneLocked()) continue;

                    // Cachear la AABB pre-calculada
                    AABB bb = world.getBlockState(pos)
                            .getShape(world, pos)
                            .bounds()
                            .inflate(0.05)
                            .move(pos);

                    cache.put(pos, bb);
                }
            }
        }
    }

    private static void drawFace(BufferBuilder buffer, Matrix4f matrix, Vec3 topLeft, Vec3 topRight, Vec3 bottomLeft, Vec3 bottomRight) {
        buffer.vertex(matrix, (float) topLeft.x, (float) topLeft.y, (float) topLeft.z)
                .uv((float) 0, (float) 0)
                .endVertex();
        buffer.vertex(matrix, (float) topRight.x, (float) topRight.y, (float) topRight.z)
                .uv((float) 1, (float) 0)
                .endVertex();
        buffer.vertex(matrix, (float) bottomRight.x, (float) bottomRight.y, (float) bottomRight.z)
                .uv((float) 1, (float) 1)
                .endVertex();
        buffer.vertex(matrix, (float) bottomLeft.x, (float) bottomLeft.y, (float) bottomLeft.z)
                .uv((float) 0, (float) 1)
                .endVertex();
    }

    public static void onJoin(EBEntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player) {
            markDirty();
        }
    }
}