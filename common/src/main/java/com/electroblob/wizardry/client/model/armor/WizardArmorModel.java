package com.electroblob.wizardry.client.model.armor;

import com.electroblob.wizardry.WizardryMainMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class WizardArmorModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(WizardryMainMod.location("wizard_armor"), "main");
    public final ModelPart hat;
    public final ModelPart right_arm;
	public final ModelPart left_arm;
	public final ModelPart right_shoe;
	public final ModelPart left_shoe;
	public final ModelPart armorBody;
	public final ModelPart robe;

    public WizardArmorModel(ModelPart root) {
        super(root);
		this.right_arm = root.getChild("right_arm");
		this.hat = root.getChild("hat");
		this.left_arm = root.getChild("left_arm");
		this.right_shoe = root.getChild("right_shoe");
		this.left_shoe = root.getChild("left_shoe");
		this.armorBody = root.getChild("body");
		this.robe = root.getChild("robe");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.75F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.501F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
		PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(-18, 46).addBox(-9.0F, -5.4F, -9.0F, 18.0F, 0.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.51F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		hat.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(48, 9).addBox(-2.0F, -13.1342F, -8.6177F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, -0.6109F, 0.0F, 0.0F));
		hat.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(56, 20).addBox(-1.0F, -10.2253F, -14.6971F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, -1.309F, 0.0F, 0.0F));
		hat.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(36, 10).addBox(-1.5F, -10.5364F, -13.8181F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, -1.1345F, 0.0F, 0.0F));
		hat.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(44, 37).addBox(-2.5F, -12.2005F, -6.7192F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, -0.3927F, 0.0F, 0.0F));
		hat.addOrReplaceChild("head_r5", CubeListBuilder.create().texOffs(40, 45).addBox(-3.0F, -10.6334F, -5.9848F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, -0.2618F, 0.0F, 0.0F));
		hat.addOrReplaceChild("head_r6", CubeListBuilder.create().texOffs(36, 54).addBox(-3.5F, -8.6451F, -5.5392F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, -0.1571F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.501F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_shoe", CubeListBuilder.create().texOffs(0, 16).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.501F)), PartPose.offset(-2.0F, 12.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_shoe", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.501F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));
		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 9.0F, 4.0F, new CubeDeformation(0.51F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("robe", CubeListBuilder.create().texOffs(16, 29).addBox(-4.0F, -14.0F, -2.0F, 8.0F, 11.0F, 4.0F, new CubeDeformation(0.51F)), PartPose.offset(0.0F, 24.0F, 0.0F));

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
        hat.copyFrom(head);
        right_arm.copyFrom(rightArm);
        left_arm.copyFrom(leftArm);
        right_shoe.copyFrom(rightLeg);
        left_shoe.copyFrom(leftLeg);
        
        armorBody.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_shoe.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_shoe.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        robe.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
