package com.binaris.wizardry.client.renderer.blockentity;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.block.BookShelfBlock;
import com.binaris.wizardry.content.blockentity.BookshelfBlockEntity;
import com.binaris.wizardry.setup.registries.EBItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;


public class BookshelfRenderer implements BlockEntityRenderer<BookshelfBlockEntity> {
    private static final ResourceLocation BOOKS_BROWN = texture("books_brown");
    private static final ResourceLocation BOOKS_ENCHANTED = texture("books_enchanted");
    private static final ResourceLocation BOOKS_RED = texture("books_red");
    private static final ResourceLocation BOOKS_BLUE = texture("books_blue");
    private static final ResourceLocation BOOKS_PURPLE = texture("books_purple");
    private static final ResourceLocation SCROLLS_BLUE = texture("scrolls_blue");
    private static final ResourceLocation SCROLLS_PURPLE = texture("scrolls_purple");
    private static final ResourceLocation SCROLLS_WOODEN = texture("scrolls_wooden");

    // Exact 1.12 model positions and texture rows, in slot order.
    private static final int[] SLOT_X = {13, 11, 8, 5, 3, 1, 13, 11, 9, 6, 4, 1};
    private static final int[] SLOT_TEXTURE_ROW = {0, 1, 2, 0, 1, 1, 2, 1, 1, 0, 2, 0};

    public BookshelfRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BookshelfBlockEntity entity, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction facing = entity.getBlockState().getValue(BookShelfBlock.FACING);

        // The light at the block position is 0 because the shelf is opaque. The books are visible through the open
        // front and back, so light each row from the air on its own side.
        int frontLight = packedLight;
        int backLight = packedLight;
        if (entity.getLevel() != null) {
            BlockPos pos = entity.getBlockPos();
            frontLight = maxLight(frontLight,
                    LevelRenderer.getLightColor(entity.getLevel(), pos.relative(facing)));
            backLight = maxLight(backLight,
                    LevelRenderer.getLightColor(entity.getLevel(), pos.relative(facing.getOpposite())));
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5, 0, -0.5);

        for (int slot = 0; slot < entity.getContainerSize(); slot++) {
            ItemStack stack = entity.getItem(slot);
            if (stack.isEmpty()) continue;

            int row = slot / 6;
            float minX = SLOT_X[slot] / 16.0F;
            float maxX = (SLOT_X[slot] + 2) / 16.0F;
            float baseY = (row == 0 ? 9 : 1) / 16.0F;

            ResourceLocation texture = textureFor(stack);
            // Scrolls are rolled up, so they use roughly half the height of a book.
            boolean scroll = texture == SCROLLS_BLUE || texture == SCROLLS_PURPLE || texture == SCROLLS_WOODEN;
            float height = (scroll ? 2.5F : 5.0F) / 16.0F;
            float minY = baseY;
            float maxY = baseY + height;
            int textureRow = SLOT_TEXTURE_ROW[slot];

            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
            renderBook(poseStack, consumer, minX, minY, 2 / 16.0F, maxX, maxY, 6 / 16.0F,
                    textureRow, false, frontLight);
            renderBook(poseStack, consumer, 1.0F - maxX, minY, 10 / 16.0F, 1.0F - minX, maxY,
                    14 / 16.0F, textureRow, true, backLight);
        }

        poseStack.popPose();
    }

    private static int maxLight(int first, int second) {
        return LightTexture.pack(
                Math.max(LightTexture.block(first), LightTexture.block(second)),
                Math.max(LightTexture.sky(first), LightTexture.sky(second)));
    }

    private static ResourceLocation texture(String name) {
        return WizardryMainMod.location("textures/block/" + name + ".png");
    }

    private static ResourceLocation textureFor(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.ENCHANTED_BOOK) return BOOKS_ENCHANTED;
        if (item == EBItems.SPELL_BOOK.get()) return BOOKS_RED;
        if (item == EBItems.WIZARD_HANDBOOK.get()) return BOOKS_BLUE;
        if (item == EBItems.ARCANE_TOME.get() || item == EBItems.APPRENTICE_ARCANE_TOME.get()
                || item == EBItems.ADVANCED_ARCANE_TOME.get() || item == EBItems.MASTER_ARCANE_TOME.get()) {
            return BOOKS_PURPLE;
        }
        if (item == EBItems.SCROLL.get() || item == EBItems.BLANK_SCROLL.get()) return SCROLLS_BLUE;
        if (item == EBItems.IDENTIFICATION_SCROLL.get()) return SCROLLS_PURPLE;
        if (item == EBItems.STORAGE_UPGRADE.get() || item == EBItems.SIPHON_UPGRADE.get()
                || item == EBItems.CONDENSER_UPGRADE.get() || item == EBItems.RANGE_UPGRADE.get()
                || item == EBItems.DURATION_UPGRADE.get() || item == EBItems.COOLDOWN_UPGRADE.get()
                || item == EBItems.BLAST_UPGRADE.get() || item == EBItems.ATTUNEMENT_UPGRADE.get()
                || item == EBItems.MELEE_UPGRADE.get()) {
            return SCROLLS_WOODEN;
        }
        // Unknown registered books use the original default brown book texture.
        return BOOKS_BROWN;
    }

    private static void renderBook(PoseStack poseStack, VertexConsumer consumer, float x1, float y1, float z1,
                                   float x2, float y2, float z2, int textureRow, boolean rear, int light) {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float v1 = textureRow * 5 / 16.0F;
        float v2 = (textureRow * 5 + 5) / 16.0F;
        float topV2 = (textureRow * 5 + 4) / 16.0F;

        float spineU1 = rear ? 6 / 16.0F : 0;
        float spineU2 = rear ? 8 / 16.0F : 2 / 16.0F;
        float backU1 = rear ? 0 : 6 / 16.0F;
        float backU2 = rear ? 2 / 16.0F : 8 / 16.0F;
        float westU1 = rear ? 6 / 16.0F : 2 / 16.0F;
        float westU2 = rear ? 2 / 16.0F : 6 / 16.0F;
        float eastU1 = rear ? 2 / 16.0F : 6 / 16.0F;
        float eastU2 = rear ? 6 / 16.0F : 2 / 16.0F;
        float topU1 = 8 / 16.0F;
        float topU2 = 10 / 16.0F;

        // North face (spine)
        quad(consumer, pose, normal, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1,
                spineU1, v2, spineU1, v1, spineU2, v1, spineU2, v2, 0, 0, -1, light);
        // South face (back cover)
        quad(consumer, pose, normal, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2,
                backU1, v2, backU1, v1, backU2, v1, backU2, v2, 0, 0, 1, light);
        // West face (page edges)
        quad(consumer, pose, normal, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1,
                westU1, v2, westU1, v1, westU2, v1, westU2, v2, -1, 0, 0, light);
        // East face (page edges)
        quad(consumer, pose, normal, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2,
                eastU1, v2, eastU1, v1, eastU2, v1, eastU2, v2, 1, 0, 0, light);
        // Top face (the rear copy is rotated 180 degrees in the original model)
        if (rear) {
            quad(consumer, pose, normal, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1,
                    topU2, topV2, topU2, v1, topU1, v1, topU1, topV2, 0, 1, 0, light);
        } else {
            quad(consumer, pose, normal, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1,
                    topU1, v1, topU1, topV2, topU2, topV2, topU2, v1, 0, 1, 0, light);
        }
    }

    private static void quad(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4,
                             float nx, float ny, float nz, int light) {
        vertex(consumer, pose, normal, x1, y1, z1, u1, v1, nx, ny, nz, light);
        vertex(consumer, pose, normal, x2, y2, z2, u2, v2, nx, ny, nz, light);
        vertex(consumer, pose, normal, x3, y3, z3, u3, v3, nx, ny, nz, light);
        vertex(consumer, pose, normal, x4, y4, z4, u4, v4, nx, ny, nz, light);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float x, float y, float z,
                               float u, float v, float nx, float ny, float nz, int light) {
        consumer.vertex(pose, x, y, z).color(255, 255, 255, 255).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(normal, nx, ny, nz).endVertex();
    }
}
