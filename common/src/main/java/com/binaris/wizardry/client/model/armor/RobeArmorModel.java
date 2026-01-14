package com.binaris.wizardry.client.model.armor;

import com.binaris.wizardry.WizardryMainMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class RobeArmorModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(WizardryMainMod.location("robe_armor"), "main");

    public final ModelPart armorBody;
    public final ModelPart left_shoe;
    public final ModelPart right_shoe;
    public final ModelPart left_arm;
    public final ModelPart right_arm;
    public final ModelPart armorHead;
    public final ModelPart robe;

    private boolean showHeadWithBody;

    public RobeArmorModel(ModelPart root, boolean showHeadWithBody) {
        super(root);
        this.showHeadWithBody = showHeadWithBody;
        this.armorBody = root.getChild("body");
        this.left_shoe = root.getChild("left_shoe");
        this.right_shoe = root.getChild("right_shoe");
        this.left_arm = root.getChild("left_arm");
        this.right_arm = root.getChild("right_arm");
        this.armorHead = root.getChild("head");
        this.robe = root.getChild("robe");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.75F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 10.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_shoe = partdefinition.addOrReplaceChild("left_shoe", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));
        left_shoe.addOrReplaceChild("left_wing2_r1", CubeListBuilder.create().texOffs(0, 24).addBox(4.725F, -4.0F, -2.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(-0.975F, -4.0F, -2.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.975F, 9.0F, 2.0F, 0.2618F, 0.0F, 0.0F));
        PartDefinition right_shoe = partdefinition.addOrReplaceChild("right_shoe", CubeListBuilder.create().texOffs(0, 16).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.offset(-2.0F, 12.0F, 0.0F));
        right_shoe.addOrReplaceChild("right_wing2_r1", CubeListBuilder.create().texOffs(0, 24).addBox(0.925F, -4.0F, -2.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(-4.725F, -4.0F, -2.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.025F, 9.0F, 2.0F, 0.2618F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)).mirror(false)
                .texOffs(0, 37).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2501F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F))
                .texOffs(0, 37).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2501F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.501F))
                        .texOffs(32, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("robe", CubeListBuilder.create().texOffs(16, 34).addBox(-4.0F, -12.5F, -2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.75F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.crouching) {
            this.robe.z = 4;
        } else {
            this.robe.z = 0;
        }

        this.robe.xRot = (this.leftLeg.xRot + this.rightLeg.xRot) / 2f;
        this.robe.yRot = this.body.yRot;
        this.robe.zRot = (this.leftLeg.zRot + this.rightLeg.zRot) / 2f;

        armorBody.copyFrom(body);
        armorHead.copyFrom(head);
        right_arm.copyFrom(rightArm);
        left_arm.copyFrom(leftArm);
        right_shoe.copyFrom(rightLeg);
        left_shoe.copyFrom(leftLeg);

        armorBody.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        armorHead.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_shoe.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_shoe.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        robe.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
