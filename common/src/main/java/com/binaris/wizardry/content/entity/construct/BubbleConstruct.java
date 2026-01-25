package com.binaris.wizardry.content.entity.construct;

import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.necromancy.Entrapment;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class BubbleConstruct extends MagicConstructEntity {

    private static final EntityDataAccessor<Boolean> IS_DARK_ORB =
            SynchedEntityData.defineId(BubbleConstruct.class, EntityDataSerializers.BOOLEAN);

    private WeakReference<LivingEntity> rider;

    public BubbleConstruct(EntityType<?> type, Level world) {
        super(type, world);
    }

    public BubbleConstruct(Level world) {
        super(EBEntities.BUBBLE.get(), world);
    }

    public static void onLivingHurt(EBLivingHurtEvent event) {
        if (event.isCanceled()) return;

        LivingEntity entity = event.getDamagedEntity();
        if (entity.getVehicle() instanceof BubbleConstruct bubble && !bubble.isDarkOrb()) {
            bubble.playSound(EBSounds.ENTITY_BUBBLE_POP.get(), 1.5f, 1.0f);
            bubble.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        Entity currentRider = EntityUtil.getRider(this);

        // Maintain weak reference to rider
        if ((rider == null || rider.get() == null) && currentRider instanceof LivingEntity living && living.isAlive()) {
            rider = new WeakReference<>(living);
        }

        // If rider disappeared but weak ref still points to a living entity, reattach it
        if (currentRider == null && rider != null) {
            LivingEntity ref = rider.get();
            if (ref != null && ref.isAlive()) ref.startRiding(this);
        }

        // Prevent initial hurt animation for non-dark bubbles
        if (this.tickCount < 1 && !isDarkOrb() && currentRider instanceof LivingEntity) {
            ((LivingEntity) currentRider).hurtTime = 0;
        }

        // Float upward slowly
        this.move(MoverType.SELF, new Vec3(0, 0.03, 0));

        if (isDarkOrb()) {
            if (currentRider != null && currentRider.tickCount % Spells.ENTRAPMENT.property(Entrapment.DAMAGE_INTERVAL) == 0) {
                MagicDamageSource.causeMagicDamage(this, currentRider, 1 * damageMultiplier, EBDamageSources.SORCERY);
            }

            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.PORTAL,
                        this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * this.getBbHeight() + 0.5d,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                        (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
                        (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

        if (currentRider == null && this.tickCount > 1) {
            if (!isDarkOrb()) this.playSound(EBSounds.ENTITY_BUBBLE_POP.get(), 1.5f, 1.0f);
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_DARK_ORB, false);
    }

    public boolean isDarkOrb() {
        return this.entityData.get(IS_DARK_ORB);
    }

    public void setDarkOrb(boolean isDarkOrb) {
        this.entityData.set(IS_DARK_ORB, isDarkOrb);
    }

    @Override
    public void despawn() {
        Entity rider = EntityUtil.getRider(this);
        if (rider instanceof LivingEntity) rider.stopRiding();
        if (!isDarkOrb()) this.playSound(EBSounds.ENTITY_BUBBLE_POP.get(), 1.5f, 1.0f);
        super.despawn();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setDarkOrb(tag.getBoolean("isDarkOrb"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("isDarkOrb", isDarkOrb());
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.1;
    }

    @Override
    protected boolean canRide(@NotNull Entity entity) {
        if (EntityUtil.getRider(this) != null) return false;
        return super.canRide(entity);
    }
}
