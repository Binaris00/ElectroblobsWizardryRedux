package com.binaris.wizardry.client.renderer.entity;


import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * This class is used to render entities that don't have a texture,
 *
 */
public class BlankRenderer<T extends Entity> extends EntityRenderer<T> {

    public BlankRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return null;
    }
}
