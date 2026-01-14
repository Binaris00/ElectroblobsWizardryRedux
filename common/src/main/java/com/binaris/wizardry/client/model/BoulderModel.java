package com.binaris.wizardry.client.model;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.entity.construct.BoulderConstruct;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class BoulderModel extends EntityModel<BoulderConstruct> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(WizardryMainMod.location("boulder"), "main");
    private final ModelPart mainGroup;

    public BoulderModel(ModelPart root) {
        this.mainGroup = root.getChild("mainGroup");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition mainGroup = partdefinition.addOrReplaceChild("mainGroup", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        mainGroup.addOrReplaceChild("box1", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -11.0F, 11.0F, 22.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -19.0F, 0.0F));

        mainGroup.addOrReplaceChild("box2", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -11.0F, 11.0F, 22.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        mainGroup.addOrReplaceChild("box3", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -11.0F, 11.0F, 22.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        mainGroup.addOrReplaceChild("box4", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -11.0F, 11.0F, 22.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        mainGroup.addOrReplaceChild("box5", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -11.0F, 11.0F, 22.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        mainGroup.addOrReplaceChild("box6", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -11.0F, 11.0F, 22.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -19.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(@NotNull BoulderConstruct entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        mainGroup.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}