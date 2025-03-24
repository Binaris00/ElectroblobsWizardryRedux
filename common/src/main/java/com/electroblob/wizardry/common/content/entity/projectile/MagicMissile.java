package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MagicMissile extends MagicArrowEntity {

    public MagicMissile(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public MagicMissile(Level world) {
        super(EBEntities.MAGIC_MISSILE.get(), world);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public double getDamage() {
        return 3;
    }

    @Override
    public int getLifetime() {
        return 12;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/magic_missile.png");
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        this.playSound(EBSounds.ENTITY_MAGIC_MISSILE_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

        if (this.level().isClientSide()) {
            ParticleBuilder.create(EBParticles.FLASH).pos(this.xo, this.yo, this.zo).color(1, 1, 0.65f).spawn(this.level());
        }
        super.onHitEntity(entityHitResult);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        Vec3 vec = blockHitResult.getLocation();
        if(this.level().isClientSide) ParticleBuilder.create(EBParticles.FLASH).pos(vec).color(1, 1, 0.65f).fade(0.85f, 0.5f, 0.8f).spawn(this.level());
        super.onHitBlock(blockHitResult);
    }

    @Override
    public void tick() {
        if (!this.inGround && this.level().isClientSide) {
            if (this.tickCount > 1) {
                ParticleBuilder.create(EBParticles.SPARKLE, level().getRandom(), this.xo, this.yo, this.zo, 0.03, true)
                        .color(1, 1, 0.65f).time(20 + random.nextInt(10)).fade(0.7f, 0, 1).spawn(this.level());

                double x = this.xo - this.getDeltaMovement().x / 2;
                double y = this.yo - this.getDeltaMovement().y / 2;
                double z = this.zo - this.getDeltaMovement().z / 2;

                ParticleBuilder.create(EBParticles.SPARKLE, level().getRandom(), x, y, z, 0.03, true)
                        .color(1, 1, 0.65f).time(20 + random.nextInt(10)).fade(0.7f, 0, 1).spawn(this.level());
            }
        }
        super.tick();
    }
}
