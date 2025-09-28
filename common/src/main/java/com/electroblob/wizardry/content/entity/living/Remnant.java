package com.electroblob.wizardry.content.entity.living;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Remnant extends Monster {
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(Remnant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ELEMENT = SynchedEntityData.defineId(Remnant.class, EntityDataSerializers.STRING);
    private @Nullable BlockPos boundOrigin;
    private ResourceLocation lootTable;

    public Remnant(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new RemnantMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new RemnantChargeAttackGoal());
        this.goalSelector.addGoal(8, new RemnantRandomMoveGoal());

        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (e) -> isAttacking()));
    }

    @Override
    public void tick() {
        // Vex trick here too
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);

        if(!level().isClientSide) return;


        Vec3 centre = new Vec3(getX(), getY(), getZ()).add(0, getBbHeight()/2, 0);

        Element element = Services.REGISTRY_UTIL.getElement(ResourceLocation.tryParse(this.getElement()));
        if(element == null) return;
        int[] colours = element.getColors();

        double r = getBbHeight()/3;

        double x = r * (random.nextDouble() * 2 - 1);
        double y = r * (random.nextDouble() * 2 - 1);
        double z = r * (random.nextDouble() * 2 - 1);

        if(this.deathTime > 0){
            // Spew out particles on death
            for(int i = 0; i < 8; i++){
                ParticleBuilder.create(EBParticles.DUST, random, centre.x + x, centre.y + y, centre.z + z, 0.1, true)
                        .time(12).color(colours[1]).fade(colours[2]).spawn(level());
            }
        }else{
            ParticleBuilder.create(EBParticles.DUST).pos(centre.x + x, centre.y + y, centre.z + z)
                    .velocity(x * -0.03, 0.02, z * -0.03).time(24 + random.nextInt(8)).color(colours[1]).fade(colours[2]).spawn(level());
        }

    }

    // ===============================
    // Entity data stuff
    // ===============================

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(ELEMENT, Elements.FIRE.getLocation().toString()); // Fire by default
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Element", this.getElement());
        if(boundOrigin != null) compound.put("BoundOrigin", NbtUtils.writeBlockPos(boundOrigin));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setElement(compound.getString("Element"));
        if(compound.contains("BoundOrigin")) boundOrigin = NbtUtils.readBlockPos(compound.getCompound("BoundOrigin"));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        List<Element> elements = new ArrayList<>(Services.REGISTRY_UTIL.getElements().stream().toList());
        elements.remove(Elements.MAGIC);

        this.setElement(elements.get(random.nextInt(elements.size())).getLocation().toString()); // Exclude MAGIC
        this.setBoundOrigin(BlockPos.containing(this.getX(), this.getY(), this.getZ()));
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    public String getElement() {
        return this.entityData.get(ELEMENT);
    }

    public void setElement(String element) {
        this.entityData.set(ELEMENT, element);
        this.lootTable = WizardryMainMod.location("entities/remnant/" + ResourceLocation.tryParse(element).getPath()); // ?
    }

    @Override
    protected @NotNull ResourceLocation getDefaultLootTable() {
        return lootTable != null ? lootTable : super.getDefaultLootTable();
    }


    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public void setBoundOrigin(@Nullable BlockPos boundOrigin) {
        this.boundOrigin = boundOrigin;
    }

    public @Nullable BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    @Override
    public int getExperienceReward() {
        return 8;
    }

    @Override
    protected SoundEvent getAmbientSound(){
        return EBSounds.ENTITY_REMNANT_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound(){
        return EBSounds.ENTITY_REMNANT_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source){
        return EBSounds.ENTITY_REMNANT_HURT.get();
    }

    class RemnantChargeAttackGoal extends Goal {
        public RemnantChargeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            LivingEntity livingentity = Remnant.this.getTarget();
            if (livingentity != null && livingentity.isAlive() && !Remnant.this.getMoveControl().hasWanted() && Remnant.this.random.nextInt(reducedTickDelay(7)) == 0) {
                return Remnant.this.distanceToSqr(livingentity) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return Remnant.this.getMoveControl().hasWanted() && Remnant.this.isAttacking() && Remnant.this.getTarget() != null && Remnant.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity livingentity = Remnant.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                Remnant.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
            }

            Remnant.this.setAttacking(true);
            Remnant.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
        }

        public void stop() {
            Remnant.this.setAttacking(false);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = Remnant.this.getTarget();
            if (livingentity != null) {
                if (Remnant.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    Remnant.this.doHurtTarget(livingentity);
                    Remnant.this.setAttacking(false);
                } else {
                    double d0 = Remnant.this.distanceToSqr(livingentity);
                    if (d0 < 9.0D) {
                        Vec3 vec3 = livingentity.getEyePosition();
                        Remnant.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
                    }
                }

            }
        }
    }


    class RemnantMoveControl extends MoveControl {
        public RemnantMoveControl(Remnant remnant) {
            super(remnant);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vec3 = new Vec3(this.wantedX - Remnant.this.getX(), this.wantedY - Remnant.this.getY(), this.wantedZ - Remnant.this.getZ());
                double d0 = vec3.length();
                if (d0 < Remnant.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Remnant.this.setDeltaMovement(Remnant.this.getDeltaMovement().scale(0.5D));
                } else {
                    Remnant.this.setDeltaMovement(Remnant.this.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05D / d0)));
                    if (Remnant.this.getTarget() == null) {
                        Vec3 vec31 = Remnant.this.getDeltaMovement();
                        Remnant.this.setYRot(-((float) Mth.atan2(vec31.x, vec31.z)) * (180F / (float)Math.PI));
                        Remnant.this.yBodyRot = Remnant.this.getYRot();
                    } else {
                        double d2 = Remnant.this.getTarget().getX() - Remnant.this.getX();
                        double d1 = Remnant.this.getTarget().getZ() - Remnant.this.getZ();
                        Remnant.this.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
                        Remnant.this.yBodyRot = Remnant.this.getYRot();
                    }
                }

            }
        }
    }

    class RemnantRandomMoveGoal extends Goal {
        public RemnantRandomMoveGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !Remnant.this.getMoveControl().hasWanted() && Remnant.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = Remnant.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = Remnant.this.blockPosition();
            }

            for(int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(Remnant.this.random.nextInt(15) - 7, Remnant.this.random.nextInt(11) - 5, Remnant.this.random.nextInt(15) - 7);
                if (Remnant.this.level().isEmptyBlock(blockpos1)) {
                    Remnant.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
                    if (Remnant.this.getTarget() == null) {
                        Remnant.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }
}
