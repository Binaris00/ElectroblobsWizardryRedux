package com.electroblob.wizardry.api.client.particle;

import com.electroblob.wizardry.api.EBLogger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
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

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Entity viewer = camera.getEntity();
        PoseStack stack = new PoseStack();
        updateEntityLinking(camera.getEntity(), tickDelta);

        float x = (float) (this.xo + (this.x - this.xo) * (double) tickDelta);
        float y = (float) (this.yo + (this.y - this.yo) * (double) tickDelta);
        float z = (float) (this.zo + (this.z - this.zo) * (double) tickDelta);

        if (this.entity != null && this.shouldApplyOriginOffset()) {
            if (this.entity != viewer || Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                Vec3 look = entity.getViewVector(tickDelta).scale(THIRD_PERSON_AXIAL_OFFSET);
                x += (float) look.x;
                y += (float) look.y;
                z += (float) look.z;
            }
        }

        if (this.target != null) {
            this.targetX = this.target.xo + (this.target.getX() - this.target.xo) * tickDelta;
            double correction = this.target.getY() - this.target.yo;
            this.targetY = this.target.yo + (this.target.getY() - this.target.yo) * tickDelta + target.getBbHeight() / 2 + correction;
            this.targetZ = this.target.zo + (this.target.getZ() - this.target.zo) * tickDelta;
        } else if (this.entity != null && this.length > 0) {
            Vec3 look = entity.getViewVector(tickDelta).scale(length);
            this.targetX = x + look.x;
            this.targetY = y + look.y;
            this.targetZ = z + look.z;
        }

        if (Double.isNaN(targetX) || Double.isNaN(targetY) || Double.isNaN(targetZ)) {
            EBLogger.error("Attempted to render a targeted particle, but neither its target entity nor target position was set, and it either had no length assigned or was not linked to an entity!");
            return;
        }

        stack.pushPose();

        stack.translate(x - camera.getPosition().x, y - camera.getPosition().y, z - camera.getPosition().z);

        double dx = this.targetX - x;
        double dy = this.targetY - y;
        double dz = this.targetZ - z;

        if (!Double.isNaN(targetVelX) && !Double.isNaN(targetVelY) && !Double.isNaN(targetVelZ)) {
            dx += tickDelta * this.targetVelX;
            dy += tickDelta * this.targetVelY;
            dz += tickDelta * this.targetVelZ;
        }

        float length = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));

        float yaw = (float) (180d / Math.PI * Math.atan2(dx, dz));
        float pitch = (float) (180f / (float) Math.PI * Math.atan(-dy / Math.sqrt(dz * dz + dx * dx)));

        stack.mulPose(Axis.YP.rotationDegrees(yaw));
        stack.mulPose(Axis.XN.rotationDegrees(pitch));

        Tesselator tessellator = Tesselator.getInstance();

        this.draw(stack, tessellator, length, tickDelta);

        stack.popPose();
    }

    protected boolean shouldApplyOriginOffset() {
        return true;
    }

    protected abstract void draw(PoseStack stack, Tesselator tessellator, float length, float tickDelta);
}
