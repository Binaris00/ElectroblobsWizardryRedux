package com.electroblob.wizardry.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ParticleTornado extends TerrainParticle {
    private float angle;
    private final double radius;
    private final double speed;
    private final double velX;
    private final double velZ;
    private boolean fullBrightness = false;

    public ParticleTornado(ClientLevel world, int maxAge, double originX, double originZ, double radius, double yPos, double velX, double velZ, BlockState block) {
        super(world, 0, 0, 0, 0, 0, 0, block);
        this.angle = this.random.nextFloat() * (float) Math.PI * 2;
        double x = originX - Mth.cos(angle) * radius;
        double z = originZ + radius * Mth.sin(angle);
        this.radius = radius;
        this.setPos(x, yPos, z);
        this.xo = x;
        this.yo = yPos;
        this.zo = z;
        this.lifetime = maxAge;
        this.hasPhysics = false;
        if (block.getLightEmission() == 0) {
            this.rCol *= 0.75;
            this.gCol *= 0.75;
            this.bCol *= 0.75;
        } else {
            this.fullBrightness = true;
        }

        speed = random.nextDouble() * 2 + 1;
        this.velX = velX;
        this.velZ = velZ;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        double omega = Math.signum(speed) * ((Math.PI * 2) / 20 - speed / (20 * radius));

        this.angle += omega;

        this.zd = radius * omega * Mth.cos(angle);
        this.xd = radius * omega * Mth.sin(angle);
        this.move(xd + velX, 0, zd + velZ);

        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime);
        }

    }

    @Override
    public int getLightColor(float partialTicks) {
        return fullBrightness ? 15728880 : super.getLightColor(partialTicks);
    }


    public static void spawnTornadoParticle(Level world, double x, double y, double z, double velX, double velZ, double radius, int maxAge, BlockState block, BlockPos pos) {
        Minecraft.getInstance().particleEngine.add(new ParticleTornado((ClientLevel) world, maxAge, x, z, radius, y, velX, velZ, block));//.setPos(pos));
    }
}
