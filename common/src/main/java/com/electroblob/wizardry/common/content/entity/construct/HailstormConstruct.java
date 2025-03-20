package com.electroblob.wizardry.common.content.entity.construct;

import com.electroblob.wizardry.common.content.entity.abstr.ScaledConstructEntity;
import com.electroblob.wizardry.common.content.entity.projectile.IceShard;
import com.electroblob.wizardry.setup.registries.EBEntities;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HailstormConstruct extends ScaledConstructEntity {

	public HailstormConstruct(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	public HailstormConstruct(Level level) {
		super(EBEntities.HAILSTORM.get(), level);
	}

	@Override
	public void tick(){

		super.tick();

		if(!this.level().isClientSide){

			double x = getX() + (level().random.nextDouble() - 0.5D) * (double)getBbWidth();
			double y = getY() + level().random.nextDouble() * (double)getBbHeight();
			double z = getZ() + (level().random.nextDouble() - 0.5D) * (double)getBbWidth();

			IceShard iceshard = new IceShard(level());
			iceshard.setPos(x, y, z);

			iceshard.setDeltaMovement(Mth.cos((float)Math.toRadians(this.getYRot() + 90)), -0.6,
					Mth.sin((float)Math.toRadians(this.getYRot() + 90)));

			//iceshard.setCaster(this.getCaster());
			//iceshard.damageMultiplier = this.damageMultiplier;

			this.level().addFreshEntity(iceshard);
		}
	}

	@Override
	public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
		return EntityDimensions.scalable(2 * 2, 5);
	}

	@Override
	protected boolean shouldScaleHeight(){
		return false;
	}
}
