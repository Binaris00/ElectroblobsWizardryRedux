package com.binaris.wizardry.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Base class for layers that overlay a tiled texture onto the entity model. Handles dynamic tiling of the texture,
/// scaling to preserve in-world pixel size (1/16 of a block), and the necessary state changes.
///
/// @param <T> The type of entity this layer applies to
/// @param <M> The type of model used for the entity
public abstract class LayerTiledOverlay<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final int GL_TEXTURE_2D = 3553;
    private static final int GL_TEXTURE_WRAP_S = 10242;
    private static final int GL_TEXTURE_WRAP_T = 10243;
    private static final int GL_REPEAT = 10497;

    private final int textureWidth;
    private final int textureHeight;

    private final Set<ResourceLocation> repeatWrapConfigured = new HashSet<>();
    private float tileScaleX = -1;
    private float tileScaleY = -1;

    /// Creates a new `LayerTiledOverlay` with the given renderer parameters.
    public LayerTiledOverlay(RenderLayerParent<T, M> renderer, int textureWidth, int textureHeight) {
        super(renderer);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    /// Creates a new `LayerTiledOverlay` with the given renderer. Texture width and height default to 16.
    public LayerTiledOverlay(RenderLayerParent<T, M> renderer) {
        this(renderer, 16, 16);
    }

    /// Returns true if this layer should be rendered for the given entity, false if not.
    ///
    /// @param entity       The entity being rendered
    /// @param partialTicks The current partial tick time
    /// @return true if this layer should be rendered, false if not
    public abstract boolean shouldRender(T entity, float partialTicks);

    /// Returns the texture to use for the overlay.
    ///
    /// @param entity       The entity being rendered
    /// @param partialTicks The current partial tick time
    /// @return A `ResourceLocation` specifying the texture to use
    public abstract ResourceLocation getTexture(T entity, float partialTicks);

    /// Returns the model to use for the overlay. Defaults to the renderer's main model.
    ///
    /// @param entity       The entity being rendered
    /// @param partialTicks The current partial tick time
    /// @return The model to use. Swapping different models in dynamically is fine, but don't create new ones each time
    public M getModel(T entity, float partialTicks) {
        return getParentModel();
    }

    /// Returns whether to render the second layer of mob/player skins (which is not an _actual_ render layer, but part
    /// of the model) as part of this layer. Defaults to false.
    ///
    /// @param entity       The entity being rendered
    /// @param partialTicks The current partial tick time
    /// @return true to render any non-hidden second layer boxes, false to always hide them from this layer
    public boolean renderSecondLayer(T entity, float partialTicks) {
        return false; // Normally it looks kind of weird because second layers typically have holes in them
    }

    /// Returns the color used to tint the overlay, as an RGB int. Defaults to white. Subclasses may override this to
    /// color the overlay per entity.
    ///
    /// @param entity       The entity being rendered
    /// @param partialTicks The current partial tick time
    /// @return The color of the overlay, as an RGB int
    protected int getColor(T entity, float partialTicks) {
        return 0xFFFFFF;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {

        if (!shouldRender(entity, partialTicks)) return;

        M model = getModel(entity, partialTicks);
        ResourceLocation texture = getTexture(entity, partialTicks);

        ensureRepeatWrap(texture);

        VertexConsumer vertexConsumer = new UvScalingVertexConsumer(
                bufferSource.getBuffer(RenderType.entityTranslucent(texture)),
                getTileScaleX(), getTileScaleY());

        List<ModelPart> hiddenParts = new ArrayList<>();
        try {
            if (!renderSecondLayer(entity, partialTicks)) {
                hideSecondLayerParts(model, hiddenParts);
            }

            model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            int colour = getColor(entity, partialTicks);
            model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                    (colour >> 16 & 255) / 255f, (colour >> 8 & 255) / 255f, (colour & 255) / 255f, 1.0f);
        } finally {
            for (ModelPart part : hiddenParts) {
                part.visible = true;
            }
        }
    }

    /// Makes sure the overlay texture repeats (instead of clamping) once the UVs are scaled beyond 1. The wrap mode is
    /// a per-texture-object state, so this only needs to happen once per texture.
    private void ensureRepeatWrap(ResourceLocation texture) {
        if (repeatWrapConfigured.add(texture)) {
            RenderSystem.setShaderTexture(0, texture);
            GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        }
    }

    private float getTileScaleX() {
        if (tileScaleX < 0) computeTileScale();
        return tileScaleX;
    }

    private float getTileScaleY() {
        if (tileScaleY < 0) computeTileScale();
        return tileScaleY;
    }

    /// Works out the scale needed to keep the overlay texture at a consistent in-world pixel size (1/16 of a block)
    /// when applied to this renderer's model. The model's texture dimensions are derived from the first cube found in
    /// the model: in Minecraft models, 1 model unit equals 1 texture pixel, so the ratio of a cube's world size to its
    /// (normalized) UV span gives the model's texture size in pixels.
    private void computeTileScale() {
        tileScaleX = 1;
        tileScaleY = 1;

        ModelPart.Cube cube = findFirstCube(getParentModel());
        if (cube == null) return;

        UvCapturingConsumer consumer = new UvCapturingConsumer();
        cube.compile(new PoseStack().last(), consumer, 0, 0, 1, 1, 1, 1);

        float uSpan = consumer.maxU - consumer.minU;
        float vSpan = consumer.maxV - consumer.minV;
        if (uSpan <= 0 || vSpan <= 0) return;

        float modelTextureWidth = (cube.maxX - cube.minX) / uSpan;
        float modelTextureHeight = (cube.maxY - cube.minY) / vSpan;
        if (modelTextureWidth <= 0 || modelTextureHeight <= 0) return;

        tileScaleX = modelTextureWidth / textureWidth;
        tileScaleY = modelTextureHeight / textureHeight;
    }

    @Nullable
    private static ModelPart.Cube findFirstCube(EntityModel<?> model) {
        Iterable<ModelPart> parts = null;
        if (model instanceof ListModel<?> listModel) {
            parts = listModel.parts();
        } else if (model instanceof HierarchicalModel<?> hierarchicalModel) {
            parts = hierarchicalModel.root().getAllParts().toList();
        }
        if (parts == null) return null;

        RandomSource random = RandomSource.create(0);
        for (ModelPart part : parts) {
            try {
                if (!part.isEmpty()) return part.getRandomCube(random);
            } catch (IllegalArgumentException ignored) {
                // Part has children but no cubes of its own; keep looking.
            }
        }
        return null;
    }

    private static void hideSecondLayerParts(EntityModel<?> model, List<ModelPart> hiddenParts) {
        if (model instanceof HumanoidModel<?> humanoid) {
            hidePart(humanoid.hat, hiddenParts);
            if (model instanceof PlayerModel<?> player) {
                hidePart(player.jacket, hiddenParts);
                hidePart(player.leftSleeve, hiddenParts);
                hidePart(player.rightSleeve, hiddenParts);
                hidePart(player.leftPants, hiddenParts);
                hidePart(player.rightPants, hiddenParts);
            }
        }
    }

    private static void hidePart(ModelPart part, List<ModelPart> hiddenParts) {
        if (part.visible) {
            part.visible = false;
            hiddenParts.add(part);
        }
    }

    /// A [VertexConsumer] that scales the UV coordinates written by the model, tiling the texture across it. All other
    /// calls are delegated straight through, so the model's geometry, lighting and overlay coordinates are unaffected.
    private static final class UvScalingVertexConsumer implements VertexConsumer {

        private final VertexConsumer delegate;
        private final float scaleX;
        private final float scaleY;

        UvScalingVertexConsumer(VertexConsumer delegate, float scaleX, float scaleY) {
            this.delegate = delegate;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
        }

        @Override
        public @NotNull VertexConsumer vertex(double x, double y, double z) {
            delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public @NotNull VertexConsumer color(int r, int g, int b, int a) {
            delegate.color(r, g, b, a);
            return this;
        }

        @Override
        public @NotNull VertexConsumer uv(float u, float v) {
            delegate.uv(u * scaleX, v * scaleY);
            return this;
        }

        @Override
        public @NotNull VertexConsumer overlayCoords(int u, int v) {
            delegate.overlayCoords(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer uv2(int u, int v) {
            delegate.uv2(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer normal(float x, float y, float z) {
            delegate.normal(x, y, z);
            return this;
        }

        @Override
        public void endVertex() {
            delegate.endVertex();
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
            delegate.defaultColor(r, g, b, a);
        }

        @Override
        public void unsetDefaultColor() {
            delegate.unsetDefaultColor();
        }
    }

    /// A [VertexConsumer] that records the UV coordinates passed to it, used to measure a cube's UV span. Since
    /// `VertexConsumer#vertex` is a default method that funnels through [VertexConsumer#uv], this captures the
    /// normalized UVs the cube would write when rendered.
    private static final class UvCapturingConsumer implements VertexConsumer {
        float minU = Float.MAX_VALUE, maxU = -Float.MAX_VALUE;
        float minV = Float.MAX_VALUE, maxV = -Float.MAX_VALUE;

        @Override
        public @NotNull VertexConsumer vertex(double x, double y, double z) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer color(int r, int g, int b, int a) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer uv(float u, float v) {
            minU = Math.min(minU, u);
            maxU = Math.max(maxU, u);
            minV = Math.min(minV, v);
            maxV = Math.max(maxV, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer overlayCoords(int u, int v) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer uv2(int u, int v) {
            return this;
        }

        @Override
        public @NotNull VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        @Override
        public void endVertex() {
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
        }

        @Override
        public void unsetDefaultColor() {
        }
    }
}