package com.electroblob.wizardry.content.entity;

import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.SpellSoundManager;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MeteorEntity extends FallingBlockEntity {
	public float blastMultiplier;
	private boolean damageBlocks;
	
	public MeteorEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
		super(entityType, level);
	}
	
	public MeteorEntity(Level level) {
		super(EBEntities.METEOR.get(), level);
	}
	
	public MeteorEntity(Level level, double x, double y, double z, BlockState state) {
		this(EBEntities.METEOR.get(), level);
		this.blocksBuilding = true;
		this.setPos(x, y, z);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setStartPos(this.blockPosition());
	}

	public MeteorEntity(Level world, double x, double y, double z, float blastMultiplier, boolean damageBlocks){
		this(world, x, y, z, EBBlocks.METEOR.get().defaultBlockState());
		this.setDeltaMovement(this.getDeltaMovement().x, this.getDeltaMovement().y - 0.1D, this.getDeltaMovement().z);
		this.setSecondsOnFire(200);
		this.blastMultiplier = blastMultiplier;
		this.damageBlocks = damageBlocks;
		this.noCulling = true;
	}

	@Override
	public double getMyRidingOffset(){
		return this.getBbHeight() / 2.0F;
	}

	@Override
	public void tick() {

		if(this.tickCount % 16 == 1 && level().isClientSide)
			SpellSoundManager.playMovingSound(this, EBSounds.ENTITY_METEOR_FALLING.get(), SoundSource.PLAYERS, 3.0f, 1.0f, false);

		this.xo = this.getX();
		this.yo = this.getY();
		this.zo = this.getZ();
		++this.time;
		this.setDeltaMovement(this.getDeltaMovement().x, this.getDeltaMovement().y - 0.1d, this.getDeltaMovement().z);
		this.move(MoverType.SELF, this.getDeltaMovement());
		this.setDeltaMovement(this.getDeltaMovement().multiply(0.9800000190734863D, 0.9800000190734863D, 0.9800000190734863D));

		if(!this.onGround()) return;

		if(!this.level().isClientSide){
			this.setDeltaMovement(this.getDeltaMovement().multiply(0.699999988079071D, -0.5D, 0.699999988079071D));

			this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, Level.ExplosionInteraction.MOB);
			this.level().explode(this, this.getX(), this.getY(), this.getZ(),
					Spells.METEOR.property(DefaultProperties.DAMAGE) * blastMultiplier,
					damageBlocks, damageBlocks ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
			this.discard();

		}else{
			// TODO SHAKE SHADER
//				EntityUtil.getEntitiesWithinRadius(15, getX(), getY(), getZ(), level(), Player.class)
//						.forEach(p -> Wizardry.proxy.shakeScreen(p, 10));
		}

	}

	@Override
	public boolean causeFallDamage(float v1, float v, @NotNull DamageSource source) {
		return false;
	}

	@Override
	public boolean displayFireAnimation(){
		return true;
	}

	@Override
	public @NotNull BlockState getBlockState() {
		return EBBlocks.METEOR.get().defaultBlockState();
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag tag){
		super.readAdditionalSaveData(tag);
		blastMultiplier = tag.getFloat("blastMultiplier");
		damageBlocks = tag.getBoolean("damageBlocks");
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag tag){
		super.addAdditionalSaveData(tag);
		tag.putFloat("blastMultiplier", blastMultiplier);
		tag.putBoolean("damageBlocks", damageBlocks);
	}
	
	@Override
	public @NotNull SoundSource getSoundSource(){
		return SoundSource.PLAYERS;
	}
}
