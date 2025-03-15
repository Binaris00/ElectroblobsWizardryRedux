package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.DeferredObject;
import com.electroblob.wizardry.client.renderer.entity.*;
import com.electroblob.wizardry.common.content.entity.EntityMeteor;
import com.electroblob.wizardry.common.content.entity.projectile.*;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

public final class EBRenderers {

    private static Map<DeferredObject<EntityType<? extends Entity>>, EntityRendererProvider<?>> providers = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void register(DeferredObject<EntityType<T>> entityType, EntityRendererProvider<T> provider) {
        providers.put((DeferredObject<EntityType<? extends Entity>>) (Object)entityType, provider);
    }

    public static void register() {
        register(EBEntities.METEOR, MeteorRenderer::new);
        register(EBEntities.ARROW_RAIN, BlankRenderer::new);
        register(EBEntities.FIRE_BOMB, ThrownItemRenderer::new);
        register(EBEntities.FIRE_BOMB, ThrownItemRenderer::new);
        register(EBEntities.SPARK_BOMB, ThrownItemRenderer::new);
        register(EBEntities.POISON_BOMB, ThrownItemRenderer::new);
        register(EBEntities.SMOKE_BOMB, ThrownItemRenderer::new);
        register(EBEntities.THUNDERBOLT, BlankRenderer::new);
        register(EBEntities.MAGIC_MISSILE, MagicArrowRenderer<MagicMissile>::new);
        register(EBEntities.DART, MagicArrowRenderer<Dart>::new);
        register(EBEntities.FIRE_BOLT, BlankRenderer::new);
        register(EBEntities.ICE_SHARD, MagicArrowRenderer<IceShard>::new);
        register(EBEntities.SPARK, BlankRenderer::new);
        register(EBEntities.LIGHTNING_ARROW, MagicArrowRenderer<LightningArrow>::new);
        register(EBEntities.ICE_CHARGE, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/ice_charge.png"))));
        register(EBEntities.MAGIC_FIREBALL, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/magic_fireball.png"))));
        register(EBEntities.ICE_BALL, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/iceball.png"))));
        register(EBEntities.DARKNESS_ORB, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/darkness_orb.png")))
        );
        register(EBEntities.ICE_LANCE, MagicArrowRenderer<IceLance>::new);
        register(EBEntities.FORCE_ARROW, ForceArrowRenderer::new);
        register(EBEntities.CONJURED_ARROW, ConjureArrowRenderer::new);
        register(EBEntities.FLAME_CATCHER_ARROW, MagicArrowRenderer<FlamecatcherArrow>::new);

        register(EBEntities.FORCE_ORB, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/force_orb.png")))
        );
    }

    public static Map<DeferredObject<EntityType<? extends Entity>>, EntityRendererProvider<?>> getRenderers() {
        return providers;
    }

    private EBRenderers() {}
}
