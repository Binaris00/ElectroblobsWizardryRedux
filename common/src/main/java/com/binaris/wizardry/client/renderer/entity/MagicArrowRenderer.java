package com.binaris.wizardry.client.renderer.entity;

import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/// This class, MagicArrowRenderer, extends the ProjectileEntityRenderer class.
/// It is used to render the EntityMagicArrow in the game.
///
/// @param <T> This represents any object that extends the EntityMagicArrow class.
public class MagicArrowRenderer<T extends MagicArrowEntity> extends ArrowRenderer<T> {
    private final ResourceLocation texture;

    public MagicArrowRenderer(EntityRendererProvider.Context ctx, ResourceLocation location) {
        super(ctx);
        this.texture = location;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return this.texture;
    }
}
