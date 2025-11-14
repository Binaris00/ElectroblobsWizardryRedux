package com.electroblob.wizardry.content.entity.construct;

import com.electroblob.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.necromancy.Entrapment;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class BubbleConstruct extends MagicConstructEntity {
    public boolean isDarkOrb;
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
        if (entity.getVehicle() instanceof BubbleConstruct bubble && !bubble.isDarkOrb) {
            entity.getVehicle().playSound(EBSounds.ENTITY_BUBBLE_POP.get(), 1.5f, 1.0f);
            entity.getVehicle().discard();
        }
    }

    public void tick() {
        super.tick();

        if ((this.rider == null || this.rider.get() == null) && EntityUtil.getRider(this) instanceof LivingEntity && EntityUtil.getRider(this).isAlive()) {
            this.rider = new WeakReference<>((LivingEntity) EntityUtil.getRider(this));
        }

        if (EntityUtil.getRider(this) == null && this.rider != null && this.rider.get() != null && this.rider.get().isAlive()) {
            this.rider.get().startRiding(this);
        }

        if (this.tickCount < 1 && !isDarkOrb) ((LivingEntity) EntityUtil.getRider(this)).hurtTime = 0;

        this.move(MoverType.SELF, new Vec3(0, 0.03, 0));

        if (isDarkOrb) {
            if (EntityUtil.getRider(this) != null && EntityUtil.getRider(this).tickCount
                    % Spells.ENTRAPMENT.property(Entrapment.DAMAGE_INTERVAL) == 0) {
                EBMagicDamageSource.causeMagicDamage(this, EntityUtil.getRider(this), 1 * damageMultiplier, EBDamageSources.SORCERY, false);
            }

            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.PORTAL,
                        this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        this.getY() + this.random.nextDouble() * (double) this.getBbHeight() + 0.5d,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth(),
                        (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
                        (this.random.nextDouble() - 0.5D) * 2.0D);
            }

            if (lifetime - this.tickCount == 75) {
                this.playSound(EBSounds.ENTITY_ENTRAPMENT_VANISH.get(), 1.5f, 1.0f);
            } else if (this.tickCount % 100 == 1 && this.tickCount < 150) {
                this.playSound(EBSounds.ENTITY_ENTRAPMENT_VANISH.get(), 1.5f, 1.0f);
            }
        }

        if (EntityUtil.getRider(this) == null && this.tickCount > 1) {
            if (!this.isDarkOrb) this.playSound(EBSounds.ENTITY_BUBBLE_POP.get(), 1.5f, 1.0f);
            this.discard();
        }
    }

    @Override
    public void despawn() {
        if (EntityUtil.getRider(this) != null) {
            EntityUtil.getRider(this).stopRiding();
        }
        if (!this.isDarkOrb) this.playSound(EBSounds.ENTITY_BUBBLE_POP.get(), 1.5f, 1.0f);
        super.despawn();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        isDarkOrb = tag.getBoolean("isDarkOrb");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("isDarkOrb", isDarkOrb);
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
