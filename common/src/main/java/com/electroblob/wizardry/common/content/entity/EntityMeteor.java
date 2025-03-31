package com.electroblob.wizardry.common.content.entity;

import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityMeteor extends FallingBlockEntity {
	public float blastMultiplier;
	private boolean damageBlocks;
	
	public EntityMeteor(EntityType<? extends FallingBlockEntity> p_31950_, Level p_31951_) {
		super(p_31950_, p_31951_);
	}
	
	public EntityMeteor(Level p_31951_) {
		super(EBEntities.METEOR.get(), p_31951_);
	}
	
	public EntityMeteor(Level p_31953_, double p_31954_, double p_31955_, double p_31956_, BlockState p_31957_) {
		this(EBEntities.METEOR.get(), p_31953_);
		//this.blockState = p_31957_;
		this.blocksBuilding = true;
		this.setPos(p_31954_, p_31955_, p_31956_);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = p_31954_;
		this.yo = p_31955_;
		this.zo = p_31956_;
		this.setStartPos(this.blockPosition());
	}

	public EntityMeteor(Level world, double x, double y, double z, float blastMultiplier, boolean damageBlocks){
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

		if(this.tickCount % 16 == 1 && level().isClientSide){
			// TODO ENTITY MOVING SOUND
			//Wizardry.proxy.playMovingSound(this, WizardrySounds.ENTITY_METEOR_FALLING.get(), SoundSource.PLAYERS, 3.0f, 1.0f, false);
		}

		this.xo = this.getX();
		this.yo = this.getY();
		this.zo = this.getZ();
		++this.time;
		this.setDeltaMovement(this.getDeltaMovement().x, this.getDeltaMovement().y - 0.1d, this.getDeltaMovement().z);
		this.move(MoverType.SELF, this.getDeltaMovement());
		this.setDeltaMovement(this.getDeltaMovement().multiply(0.9800000190734863D, 0.9800000190734863D, 0.9800000190734863D));

		if(this.onGround()){
			
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
	public void readAdditionalSaveData(@NotNull CompoundTag compoundTag){
		super.readAdditionalSaveData(compoundTag);
		blastMultiplier = compoundTag.getFloat("blastMultiplier");
		damageBlocks = compoundTag.getBoolean("damageBlocks");
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag compoundTag){
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putFloat("blastMultiplier", blastMultiplier);
		compoundTag.putBoolean("damageBlocks", damageBlocks);
	}
	
	@Override
	public @NotNull SoundSource getSoundSource(){
		return SoundSource.PLAYERS;
	}
}
