package com.electroblob.wizardry.client.renderer.entity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.client.model.WizardModel;
import com.electroblob.wizardry.content.entity.living.AbstractWizard;
import com.electroblob.wizardry.content.entity.living.Wizard;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class WizardRenderer extends HumanoidMobRenderer<Wizard, HumanoidModel<Wizard>> {
    static final ResourceLocation[] TEXTURES = new ResourceLocation[6];

    public WizardRenderer(Context context) {
        super(context, new WizardModel<>(context.bakeLayer(WizardModel.LAYER_LOCATION)), 0.5f);
        for (int i = 0; i < 6; i++) {
            TEXTURES[i] = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/wizard/wizard_" + i + ".png");
        }
        this.addLayer(new HumanoidArmorLayer<>(this, new WizardModel<>(context.bakeLayer(WizardModel.LAYER_LOCATION)),
                new WizardModel<>(context.bakeLayer(WizardModel.LAYER_LOCATION)), context.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Wizard wizard) {
        return TEXTURES[wizard.getTextureIndex()];
    }
}
