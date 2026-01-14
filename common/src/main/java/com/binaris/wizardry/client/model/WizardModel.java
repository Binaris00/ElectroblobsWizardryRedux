package com.binaris.wizardry.client.model;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.entity.living.AbstractWizard;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class WizardModel<T extends AbstractWizard> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(WizardryMainMod.location("wizard"), "main");

    public WizardModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.0F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.getChild("head").addOrReplaceChild("beard", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 8.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0F, -4.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}