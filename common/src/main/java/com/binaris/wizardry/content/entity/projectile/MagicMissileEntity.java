package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MagicMissileEntity extends MagicArrowEntity {

    public MagicMissileEntity(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public MagicMissileEntity(Level world) {
        super(EBEntities.MAGIC_MISSILE.get(), world);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        this.playSound(EBSounds.ENTITY_MAGIC_MISSILE_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

        if (this.level().isClientSide()) {
            ParticleBuilder.create(EBParticles.FLASH).pos(this.xo, this.yo, this.zo).color(1, 1, 0.65f).spawn(this.level());
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        Vec3 vec = blockHitResult.getLocation();
        if (this.level().isClientSide)
            ParticleBuilder.create(EBParticles.FLASH).pos(vec).color(1, 1, 0.65f).fade(0.85f, 0.5f, 0.8f).spawn(this.level());
    }

    @Override
    public void ticksInAir() {
        if (!this.level().isClientSide) return;

        double x = getX() - getDeltaMovement().x / 2;
        double y = getY() - getDeltaMovement().y / 2;
        double z = getZ() - getDeltaMovement().z / 2;

        if (WizardryMainMod.IS_THE_SEASON) {
            ParticleBuilder.create(EBParticles.SPARKLE, random, getX(), getY(), getZ(), 0.03, true).color(0.8f, 0.15f, 0.15f).time(20 + random.nextInt(10)).spawn(this.level());

            ParticleBuilder.create(EBParticles.SNOW).pos(getX(), getY(), getZ()).spawn(this.level());

            if (this.tickCount > 1) {
                ParticleBuilder.create(EBParticles.SPARKLE, random, x, y, z, 0.03, true).color(0.15f, 0.7f, 0.15f).time(20 + random.nextInt(10)).spawn(this.level());
            }
        } else {
            ParticleBuilder.create(EBParticles.SPARKLE, random, getX(), getY(), getZ(), 0.03, true).color(1, 1, 0.65f).fade(0.7f, 0, 1).time(20 + random.nextInt(10)).spawn(this.level());

            if (this.tickCount > 1) {
                ParticleBuilder.create(EBParticles.SPARKLE, level().getRandom(), x, y, z, 0.03, true)
                        .color(1, 1, 0.65f).time(20 + random.nextInt(10)).fade(0.7f, 0, 1).spawn(this.level());
            }
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public double getDamage() {
        return Spells.MAGIC_MISSILE.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return 12;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/magic_missile.png");
    }
}
