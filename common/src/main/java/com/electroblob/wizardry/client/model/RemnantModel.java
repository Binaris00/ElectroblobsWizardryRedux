package com.electroblob.wizardry.client.model;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.content.entity.living.Remnant;
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

public class RemnantModel extends EntityModel<Remnant> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(WizardryMainMod.MOD_ID, "remnant"), "main");
    private final ModelPart cube;

    public RemnantModel(ModelPart root) {
        this.cube = root.getChild("cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        part.addOrReplaceChild("cube", CubeListBuilder.create()
                        .texOffs(0,0)
                        .addBox(-4, -4, -4, 8, 8, 8),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 64, 32);
    }


    @Override
    public void setupAnim(@NotNull Remnant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(@NotNull PoseStack pStack, @NotNull VertexConsumer pBuffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        cube.render(pStack, pBuffer, packedLight, packedOverlay);
    }
}
