package com.binaris.wizardry.client.model;

import com.binaris.wizardry.content.entity.living.IceGiant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.binaris.wizardry.WizardryMainMod.MOD_ID;

public class IceGiantModel extends EntityModel<IceGiant> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(MOD_ID, "ice_giant"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public IceGiantModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Head
        PartDefinition head = partdefinition.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 10)
                        .addBox(-6.0F, -14.0F, -6.5F, 12, 12, 12),
                PartPose.offset(0.0F, -7.0F, -1.0F));

        // Head spikes
        head.addOrReplaceChild("head_spike1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, 0.0F, 4, 4, 4),
                PartPose.offsetAndRotation(-4.0F, -10.0F, -5.0F,
                        -0.1047198F, -0.5235988F, 0.9599311F));

        head.addOrReplaceChild("head_spike2",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, -4.0F, 0.0F, 4, 4, 4),
                PartPose.offsetAndRotation(4.0F, -16.0F, 0.0F,
                        0.5585054F, 0.9250245F, -0.5235988F));

        head.addOrReplaceChild("head_spike3",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(4.0F, -13.0F, 4.0F,
                        0.7853982F, -1.396263F, 0.7853982F));

        head.addOrReplaceChild("head_spike4",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, 0.0F, 4, 4, 4),
                PartPose.offsetAndRotation(-4.0F, -16.0F, 0.0F,
                        0.5585054F, -0.9250245F, 0.5235988F));

        head.addOrReplaceChild("head_spike5",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, -4.0F, 0.0F, 4, 4, 4),
                PartPose.offsetAndRotation(4.0F, -10.0F, -5.0F,
                        0.5235988F, -0.9599311F, 0.1047198F));

        head.addOrReplaceChild("head_spike6",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(-4.0F, -13.0F, 4.0F,
                        0.7853982F, -1.745329F, 0.7853982F));

        head.addOrReplaceChild("head_spike7",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(0.0F, -20.0F, 4.0F,
                        0.7853982F, 1.570796F, 0.7853982F));

        // Body
        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 40)
                        .addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11)
                        .texOffs(0, 70)
                        .addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6),
                PartPose.offset(0.0F, -7.0F, 0.0F));

        // Body spikes
        body.addOrReplaceChild("body_spike1",
                CubeListBuilder.create()
                        .texOffs(32, 69)
                        .addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6),
                PartPose.offsetAndRotation(-4.0F, -4.0F, 3.0F,
                        0.2808018F, 0.8096675F, 0.8339369F));

        body.addOrReplaceChild("body_spike2",
                CubeListBuilder.create()
                        .texOffs(32, 69)
                        .addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6),
                PartPose.offsetAndRotation(4.0F, -4.0F, 3.0F,
                        0.2808018F, -0.8096757F, -0.8339358F));

        body.addOrReplaceChild("body_spike3",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(6.0F, -2.0F, -5.0F,
                        1.120006F, -1.347726F, -0.8969422F));

        body.addOrReplaceChild("body_spike4",
                CubeListBuilder.create()
                        .texOffs(32, 69)
                        .addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6),
                PartPose.offsetAndRotation(0.0F, -4.0F, -4.0F,
                        0.7853982F, -1.570796F, 0.9599311F));

        body.addOrReplaceChild("body_spike5",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(-6.0F, -2.0F, -5.0F,
                        1.120006F, 1.347725F, 0.896934F));

        // Right arm
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(60, 21)
                        .addBox(-13.0F, -2.5F, -3.0F, 4, 30, 6),
                PartPose.offset(0.0F, -7.0F, 0.0F));

        // Right arm spikes
        rightArm.addOrReplaceChild("right_arm_spike1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(-11.0F, 0, 0.0F,
                        0.7853982F, 1.134464F, 0.9599311F));

        rightArm.addOrReplaceChild("right_arm_spike2",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(-12.0F, 4, 0.0F,
                        0.7853982F, 0.0F, 0.9599311F));

        // Left arm
        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(60, 58)
                        .addBox(9.0F, -2.5F, -3.0F, 4, 30, 6),
                PartPose.offset(0.0F, -7.0F, 0.0F));

        // Left arm spikes
        leftArm.addOrReplaceChild("left_arm_spike1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(11.0F, 0, 0.0F,
                        0.7853982F, -1.134464F, -0.9599311F));

        leftArm.addOrReplaceChild("left_arm_spike2",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4),
                PartPose.offsetAndRotation(12.0F, 4, 0.0F,
                        0.7853982F, 0.0F, -0.9599311F));

        // Left leg
        partdefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(37, 0)
                        .addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5),
                PartPose.offset(-4.0F, 11.0F, 0.0F));

        // Right leg
        partdefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(60, 0)
                        .mirror()
                        .addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5),
                PartPose.offset(5.0F, 11.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(IceGiant entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // Head rotation
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        // Leg animation
        this.leftLeg.xRot = -1.5F * triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.rightLeg.xRot = 1.5F * triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.leftLeg.yRot = 0.0F;
        this.rightLeg.yRot = 0.0F;

        // Arm animation
        int attackTimer = entity.getAttackAnimationTick();
        if (attackTimer > 0) {
            float partialTicks = 1.0F; // You might want to pass this as a parameter
            this.rightArm.xRot = -2.0F + 1.5F * triangleWave((float) attackTimer - partialTicks, 10.0F);
            this.leftArm.xRot = -2.0F + 1.5F * triangleWave((float) attackTimer - partialTicks, 10.0F);
        } else {
            this.rightArm.xRot = (-0.2F + 1.5F * triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
            this.leftArm.xRot = (-0.2F - 1.5F * triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
        }
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.rightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    /**
     * Triangle wave function for smooth animation cycles
     */
    private float triangleWave(float value, float period) {
        return (Math.abs(value % period - period * 0.5F) - period * 0.25F) / (period * 0.25F);
    }
}