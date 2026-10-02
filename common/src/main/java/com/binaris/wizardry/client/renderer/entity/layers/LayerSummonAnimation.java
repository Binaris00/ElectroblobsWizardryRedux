package com.binaris.wizardry.client.renderer.entity.layers;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

/// Layer used to render the appear/disappear animation for summoned creatures (minions).
///
/// Unlike the original mod, minions are not dedicated entity classes but arbitrary mobs carrying [`MinionData`]
/// (attached per-loader via `Services.OBJECT_DATA`), so this layer works on any living entity renderer.
///
/// @param <T> The type of entity this layer applies to
/// @param <M> The type of model used for the entity
public class LayerSummonAnimation<T extends LivingEntity, M extends EntityModel<T>> extends LayerTiledOverlay<T, M> {

    private static final int ANIMATION_TICKS = 19;
    private static final int HIDE_MODEL_TICKS = 9;

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[ANIMATION_TICKS];

    static {
        for (int i = 0; i < ANIMATION_TICKS; i++) {
            TEXTURES[i] = WizardryMainMod.location("textures/entity/summon_overlay/summon_overlay_" + i + ".png");
        }
    }

    public LayerSummonAnimation(RenderLayerParent<T, M> renderer) {
        super(renderer, 32, 32);
    }

    @Override
    public boolean shouldRender(T entity, float partialTicks) {
        MinionData data = getMinionData(entity);
        return data != null && data.hasAnimation() && data.getLifetime() > 0
                && getFrameNumber(entity, data) < ANIMATION_TICKS;
    }

    @Override
    public ResourceLocation getTexture(T entity, float partialTicks) {
        MinionData data = getMinionData(entity);
        if (data == null) return TEXTURES[0];
        return TEXTURES[Math.min(getFrameNumber(entity, data), ANIMATION_TICKS - 1)];
    }

    @Override
    protected int getColor(T entity, float partialTicks) {
        MinionData data = getMinionData(entity);
        return data != null ? data.getAnimationColor((float) getFrameNumber(entity, data) / ANIMATION_TICKS) : 0xFFFFFF;
    }

    private static int getFrameNumber(LivingEntity entity, MinionData data) {
        return Math.min(entity.tickCount, Math.max(data.getLifetime() - entity.tickCount - 1, 0));
    }

    @Nullable
    private static MinionData getMinionData(LivingEntity entity) {
        if (!(entity instanceof Mob mob)) return null;
        if (!Services.OBJECT_DATA.isMinion(mob)) return null;
        return Services.OBJECT_DATA.getMinionData(mob);
    }

    /// Whether the entity's normal model should be hidden this frame so only the overlay is visible. This happens
    /// during the first and last few ticks of a minion's life, while it is appearing or disappearing. Called from the
    /// `LivingEntityRenderer` mixin, which suppresses the model's render type entirely (so no "ghost" render remains).
    ///
    /// @param entity The entity being rendered
    /// @return true if the normal model should be hidden, false if it should render as usual
    public static boolean shouldHideModel(LivingEntity entity) {
        MinionData data = getMinionData(entity);
        if (data == null || !data.hasAnimation() || data.getLifetime() <= 0) return false;
        return entity.tickCount < HIDE_MODEL_TICKS || data.getLifetime() - entity.tickCount < HIDE_MODEL_TICKS;
    }
}