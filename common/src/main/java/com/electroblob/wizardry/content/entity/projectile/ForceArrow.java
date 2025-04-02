package com.electroblob.wizardry.content.entity.projectile;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ForceArrow extends MagicArrowEntity {
    public ForceArrow(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public ForceArrow(Level world) {
        super(EBEntities.FORCE_ARROW.get(), world);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        this.playSound(EBSounds.ENTITY_FORCE_ARROW_HIT.get(), 1.0F, 1.0F);

        if (this.level().isClientSide()) {
            ParticleBuilder.create(EBParticles.FLASH)
                    .pos(getX(), getY(), getZ())
                    .scale(1.3f)
                    .color(0.75f, 1.0f, 0.85f)
                    .spawn(level());
        }

        super.onHitEntity(hitResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        this.playSound(EBSounds.ENTITY_FORCE_ARROW_HIT.get(), 1.0F, 1.0F);
        super.onHitBlock(blockHitResult);

        if (this.level().isClientSide()) {
            Vec3 pos = blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.15));
            ParticleBuilder.create(EBParticles.FLASH)
                    .pos(pos)
                    .scale(1.3f)
                    .color(0.75f, 1.0f, 0.85f)
                    .spawn(level());
        }

    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return EBSounds.ENTITY_FORCE_ARROW_HIT.get();
    }

    @Override
    public void tickInGround() {
        this.discard();
    }

    @Override
    public double getDamage() {
        return Spells.FORCE_ARROW.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return 20;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/force_arrow.png");
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return EBDamageSources.FORCE;
    }
}
