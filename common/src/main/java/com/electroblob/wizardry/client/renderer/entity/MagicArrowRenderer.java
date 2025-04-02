package com.electroblob.wizardry.client.renderer.entity;


import com.electroblob.wizardry.api.content.entity.projectile.MagicArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * This class, MagicArrowRenderer, extends the ProjectileEntityRenderer class.
 * It is used to render the EntityMagicArrow in the game, see {@link MagicArrowEntity} and {@link MagicArrowEntity#getTexture()}.
 *
 * @param <T> This represents any object that extends the EntityMagicArrow class.
 */
public class MagicArrowRenderer<T extends MagicArrowEntity> extends ArrowRenderer<T> {
    /**
     * This is the constructor for the MagicArrowRenderer class.
     * It calls the constructor of the superclass, ProjectileEntityRenderer.
     * <p>
     * If you don't understand what this does, don't touch it ^^
     * @param ctx context of the EntityRendererFactory.
     */
    public MagicArrowRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    /**
     * This method overrides the getTexture method in the superclass.
     * It returns the texture of the EntityMagicArrow.
     *
     * @param entity This is the EntityMagicArrow object.
     * @return Identifier object representing the texture of the EntityMagicArrow.
     */
    @Override
    public @NotNull ResourceLocation getTextureLocation(T entity) {
        return entity.getTexture();
    }
}
