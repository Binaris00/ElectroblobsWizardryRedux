package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.client.model.RemnantModel;
import com.electroblob.wizardry.client.model.armor.RobeArmorModel;
import com.electroblob.wizardry.client.model.armor.WizardArmorModel;
import com.electroblob.wizardry.client.renderer.entity.*;
import com.electroblob.wizardry.content.entity.projectile.*;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.google.common.collect.Maps;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class EBRenderers {
    private static final Map<DeferredObject<EntityType<? extends Entity>>, EntityRendererProvider<?>> providers = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void registerEntityRender(DeferredObject<EntityType<T>> entityType, EntityRendererProvider provider) {
        providers.put((DeferredObject<EntityType<? extends Entity>>) (Object)entityType, provider);
    }

    public static void createEntityLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
        consumer.accept(WizardArmorModel.LAYER_LOCATION, WizardArmorModel::createBodyLayer);
        consumer.accept(RobeArmorModel.LAYER_LOCATION, RobeArmorModel::createBodyLayer);
        consumer.accept(RemnantModel.LAYER_LOCATION, RemnantModel::createBodyLayer);
    }

    public static void registerRenderers() {
        registerEntityRender(EBEntities.METEOR, MeteorRenderer::new);
        registerEntityRender(EBEntities.ARROW_RAIN, BlankRenderer::new);
        registerEntityRender(EBEntities.FIRE_BOMB, ThrownItemRenderer::new);
        registerEntityRender(EBEntities.FIRE_BOMB, ThrownItemRenderer::new);
        registerEntityRender(EBEntities.SPARK_BOMB, ThrownItemRenderer::new);
        registerEntityRender(EBEntities.POISON_BOMB, ThrownItemRenderer::new);
        registerEntityRender(EBEntities.SMOKE_BOMB, ThrownItemRenderer::new);
        registerEntityRender(EBEntities.THUNDERBOLT, BlankRenderer::new);
        registerEntityRender(EBEntities.MAGIC_MISSILE, MagicArrowRenderer<MagicMissileEntity>::new);
        registerEntityRender(EBEntities.DART, MagicArrowRenderer<DartEntity>::new);
        registerEntityRender(EBEntities.FIRE_BOLT, BlankRenderer::new);
        registerEntityRender(EBEntities.ICE_SHARD, MagicArrowRenderer<IceShardEntity>::new);
        registerEntityRender(EBEntities.SPARK, BlankRenderer::new);
        registerEntityRender(EBEntities.LIGHTNING_ARROW, MagicArrowRenderer<LightningArrow>::new);
        registerEntityRender(EBEntities.ICE_CHARGE, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/ice_charge.png"))));
        registerEntityRender(EBEntities.MAGIC_FIREBALL, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/magic_fireball.png"))));
        registerEntityRender(EBEntities.ICE_BALL, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/iceball.png"))));
        registerEntityRender(EBEntities.DARKNESS_ORB, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/darkness_orb.png")))
        );
        registerEntityRender(EBEntities.ICE_LANCE, MagicArrowRenderer<IceLanceEntity>::new);
        registerEntityRender(EBEntities.FORCE_ARROW, ForceArrowRenderer::new);
        registerEntityRender(EBEntities.CONJURED_ARROW, ConjureArrowRenderer::new);
        registerEntityRender(EBEntities.FLAME_CATCHER_ARROW, MagicArrowRenderer<FlamecatcherArrow>::new);

        registerEntityRender(EBEntities.FORCE_ORB, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/force_orb.png")))
        );

        registerEntityRender(EBEntities.BLIZZARD, BlankRenderer::new);

        registerEntityRender(EBEntities.FIRE_SIGIL, (ctx ->
                new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/fire_sigil.png"),
                        0, true)));

        registerEntityRender(EBEntities.FROST_SIGIL, (ctx ->
                new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/frost_sigil.png"),
                        0, true)));

        registerEntityRender(EBEntities.LIGHTNING_SIGIL, (ctx ->
                new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/lightning_sigil.png"),
                        0, true)));

        registerEntityRender(EBEntities.HEAL_AURA, (ctx ->
                new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/healing_aura.png"),
                        0.3F, true)));

        registerEntityRender(EBEntities.RING_OF_FIRE, (ctx ->
                new FireRingRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/ring_of_fire.png"))));

        registerEntityRender(EBEntities.ICE_SPICKES, IceSpikeRenderer::new);

        registerEntityRender(EBEntities.TORNADO, BlankRenderer::new);

        registerEntityRender(EBEntities.COMBUSTION_RUNE, (ctx) ->
        {
            return new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/combustion_rune.png"), 0, true);
        });

        registerEntityRender(EBEntities.BUBBLE, BubbleRenderer::new);

        registerEntityRender(EBEntities.HAILSTORM, BlankRenderer::new);

        registerEntityRender(EBEntities.DECAY, DecayRenderer::new);

        registerEntityRender(EBEntities.REMNANT, RemnantRenderer::new);
    }

    public static Map<DeferredObject<EntityType<? extends Entity>>, EntityRendererProvider<?>> getRenderers() {
        return providers;
    }

    private EBRenderers() {}
}
