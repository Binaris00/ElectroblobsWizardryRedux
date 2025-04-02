package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.client.renderer.entity.*;
import com.electroblob.wizardry.content.entity.projectile.*;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

public final class EBRenderers {

    private static final Map<DeferredObject<EntityType<? extends Entity>>, EntityRendererProvider<?>> providers = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    private static <T extends Entity> void register(DeferredObject<EntityType<T>> entityType, EntityRendererProvider provider) {
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
        register(EBEntities.MAGIC_MISSILE, MagicArrowRenderer<MagicMissileEntity>::new);
        register(EBEntities.DART, MagicArrowRenderer<DartEntity>::new);
        register(EBEntities.FIRE_BOLT, BlankRenderer::new);
        register(EBEntities.ICE_SHARD, MagicArrowRenderer<IceShardEntity>::new);
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
        register(EBEntities.ICE_LANCE, MagicArrowRenderer<IceLanceEntity>::new);
        register(EBEntities.FORCE_ARROW, ForceArrowRenderer::new);
        register(EBEntities.CONJURED_ARROW, ConjureArrowRenderer::new);
        register(EBEntities.FLAME_CATCHER_ARROW, MagicArrowRenderer<FlamecatcherArrow>::new);

        register(EBEntities.FORCE_ORB, (ctx -> new MagicProjectileRenderer<>(ctx,
                new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/force_orb.png")))
        );

        register(EBEntities.BLIZZARD, BlankRenderer::new);

        register(EBEntities.FIRE_SIGIL, (ctx ->
                new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/fire_sigil.png"),
                        0, true)));

        register(EBEntities.FROST_SIGIL, (ctx ->
                new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/frost_sigil.png"),
                        0, true)));

        register(EBEntities.LIGHTNING_SIGIL, (ctx ->
                new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/lightning_sigil.png"),
                        0, true)));

        register(EBEntities.HEAL_AURA, (ctx ->
                new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/healing_aura.png"),
                        0.3F, true)));

        register(EBEntities.RING_OF_FIRE, (ctx ->
                new FireRingRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/ring_of_fire.png"))));

        register(EBEntities.ICE_SPICKES, IceSpikeRenderer::new);

        register(EBEntities.TORNADO, BlankRenderer::new);

        register(EBEntities.COMBUSTION_RUNE, (ctx) ->
        {
            return new SigilRenderer(ctx, new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/combustion_rune.png"), 0, true);
        });

        register(EBEntities.BUBBLE, BubbleRenderer::new);

        register(EBEntities.HAILSTORM, BlankRenderer::new);

        register(EBEntities.DECAY, DecayRenderer::new);
    }

    public static Map<DeferredObject<EntityType<? extends Entity>>, EntityRendererProvider<?>> getRenderers() {
        return providers;
    }

    private EBRenderers() {}
}
