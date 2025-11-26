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
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class LightningArrow extends MagicArrowEntity {

    public LightningArrow(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public LightningArrow(Level world) {
        super(EBEntities.LIGHTNING_ARROW.get(), world);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        for (int i = 0; i < 8; i++) {
            if (this.level().isClientSide()) {
                ParticleBuilder.create(EBParticles.SPARK, level().getRandom(), this.xo, this.yo + this.getBbHeight() / 2, this.zo, 1, false)
                        .spawn(this.level());
            }
        }
        this.playSound(EBSounds.ENTITY_LIGHTNING_ARROW_HIT.get(), 1.0F, 1.0F);
        super.onHitEntity(hitResult);
    }

    @Override
    public void tickInGround() {
        if (this.tickCount > 1 && level().isClientSide)
            ParticleBuilder.create(EBParticles.SPARK).pos(this.xo, this.yo, this.zo).spawn(this.level());
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public double getDamage() {
        return Spells.LIGHTNING_ARROW.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return 20;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/lightning_arrow.png");
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return EBDamageSources.SHOCK;
    }
}
