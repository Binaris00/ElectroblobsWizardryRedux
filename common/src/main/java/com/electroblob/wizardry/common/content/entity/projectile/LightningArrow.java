package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import com.electroblob.wizardry.setup.registries.client.EBSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LightningArrow extends MagicArrowEntity {

    public LightningArrow(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public LightningArrow(Level world) {
        super(null, world);
        //super(EBEntities.LIGHTNING_ARROW.get(), world);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public double getDamage() {
        return 7;
    }

    @Override
    public int getLifetime() {
        return 20;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation("ebwizardry", "textures/entity/lightning_arrow.png");
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        for (int i = 0; i < 8; i++) {
            if (this.level().isClientSide()) {
//                ParticleBuilder.create(EBParticles.SPARK, new Random(), this.xo, this.yo + this.getBbHeight() / 2, this.zo, 1, false)
//                        .spawn(this.level());
            }
        }
        //this.playSound(EBSounds.ENTITY_LIGHTNING_ARROW_HIT.get(), 1.0F, 1.0F);
        super.onHitEntity(entityHitResult);
    }

    @Override
    public void tick() {
//        if (!this.inGround) {
//            if (this.tickCount > 1) {
//                ParticleBuilder.create(EBParticles.SPARK)
//                        .pos(this.xo, this.yo, this.zo)
//                        .spawn(this.level());
//            }
//        }
        super.tick();
    }
}
