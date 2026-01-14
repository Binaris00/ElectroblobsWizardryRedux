package com.binaris.wizardry.api.client.particle;

import com.binaris.wizardry.api.EBLogger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ParticleTargeted extends ParticleWizardry {
    private static final double THIRD_PERSON_AXIAL_OFFSET = 1.2;

    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected double targetVelX;
    protected double targetVelY;
    protected double targetVelZ;

    protected double length;

    @Nullable
    protected Entity target = null;

    public ParticleTargeted(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider, boolean updateTextureOnTick) {
        super(world, x, y, z, spriteProvider, updateTextureOnTick);
    }

    @Override
    public void setTargetPosition(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    @Override
    public void setTargetVelocity(double vx, double vy, double vz) {
        this.targetVelX = vx;
        this.targetVelY = vy;
        this.targetVelZ = vz;
    }

    @Override
    public void setTargetEntity(Entity target) {
        this.target = target;
    }

    @Override
    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public void tick() {
        super.tick();

        if (!Double.isNaN(targetVelX) && !Double.isNaN(targetVelY) && !Double.isNaN(targetVelZ)) {
            this.targetX += this.targetVelX;
            this.targetY += this.targetVelY;
            this.targetZ += this.targetVelZ;
        }
    }

    private Vec3 getPosition(Entity entity, double yOffset, float partialTick) {
        double d0 = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double d1 = Mth.lerp(partialTick, entity.yOld, entity.getY()) + yOffset;
        double d2 = Mth.lerp(partialTick, entity.zOld, entity.getZ());
        return new Vec3(d0, d1, d2);
    }

    @Override
    protected void updateEntityLinking(float partialTicks) {
        // No need to do anything here since we handle entity linking in render()
    }

    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Entity viewer = camera.getEntity();
        PoseStack stack = new PoseStack();
        double originX, originY, originZ;

        if (this.entity != null) {
            Vec3 entityPos = this.getPosition(entity, 0, tickDelta);
            originX = entityPos.x + relativeX;
            originY = entityPos.y + relativeY;
            originZ = entityPos.z + relativeZ;
        } else {
            originX = Mth.lerp(tickDelta, this.xo, this.x);
            originY = Mth.lerp(tickDelta, this.yo, this.y);
            originZ = Mth.lerp(tickDelta, this.zo, this.z);
        }

        if (this.entity != null && this.shouldApplyOriginOffset()) {
            boolean isFirstPerson = (this.entity == viewer && Minecraft.getInstance().options.getCameraType() != CameraType.THIRD_PERSON_FRONT);

            if (!isFirstPerson || this.shouldApplyOriginOffsetInFirstPerson()) {
                Vec3 look = entity.getViewVector(tickDelta).scale(THIRD_PERSON_AXIAL_OFFSET);
                originX += look.x;
                originY += look.y;
                originZ += look.z;
            }
        }

        double finalTargetX, finalTargetY, finalTargetZ;

        if (this.target != null) {
            Vec3 targetPos = this.getPosition(target, (double) target.getBbHeight() * 0.5D, tickDelta);
            finalTargetX = targetPos.x;
            finalTargetY = targetPos.y;
            finalTargetZ = targetPos.z;
        } else if (this.entity != null && this.length > 0) {
            Vec3 look = entity.getViewVector(tickDelta).scale(length);
            finalTargetX = originX + look.x;
            finalTargetY = originY + look.y;
            finalTargetZ = originZ + look.z;
        } else {
            finalTargetX = this.targetX;
            finalTargetY = this.targetY;
            finalTargetZ = this.targetZ;

            if (!Double.isNaN(targetVelX) && !Double.isNaN(targetVelY) && !Double.isNaN(targetVelZ)) {
                finalTargetX += tickDelta * this.targetVelX;
                finalTargetY += tickDelta * this.targetVelY;
                finalTargetZ += tickDelta * this.targetVelZ;
            }
        }

        if (Double.isNaN(finalTargetX) || Double.isNaN(finalTargetY) || Double.isNaN(finalTargetZ)) {
            EBLogger.error("Attempted to render a targeted particle, but neither its target entity nor target position was set, and it either had no length assigned or was not linked to an entity.");
            return;
        }

        stack.pushPose();
        stack.translate(originX - camera.getPosition().x, originY - camera.getPosition().y, originZ - camera.getPosition().z);

        double dx = finalTargetX - originX;
        double dy = finalTargetY - originY;
        double dz = finalTargetZ - originZ;

        float beamLength = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));
        Vec3 direction = new Vec3(dx, dy, dz).normalize();

        float yaw = (float) (Math.atan2(direction.x, direction.z) * 180.0D / Math.PI);
        float pitch = (float) (Math.asin(-direction.y) * 180.0D / Math.PI);

        stack.mulPose(Axis.YP.rotationDegrees(yaw));
        stack.mulPose(Axis.XP.rotationDegrees(pitch));

        Tesselator tesselator = Tesselator.getInstance();
        this.draw(stack, tesselator, beamLength, tickDelta);

        stack.popPose();
    }

    protected boolean shouldApplyOriginOffset() {
        return true;
    }


    protected boolean shouldApplyOriginOffsetInFirstPerson() {
        return false;
    }

    protected abstract void draw(PoseStack stack, Tesselator tesselator, float length, float tickDelta);
}