package com.electroblob.wizardry.api.common.entity.construct;

import com.electroblob.wizardry.api.common.util.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * This class is for all inanimate magical constructs that are not projectiles. Generally speaking, subclasses of this
 * class are areas of effect which deal with damage or apply effects over time, including black hole, blizzard, tornado and
 * a few others. The caster UUID, lifetime and damage multiplier are stored here, and lifetime is also synced here.
 */
public abstract class MagicConstructEntity extends Entity implements OwnableEntity {
    private UUID casterUUID;

    /** The time in ticks this magical construct lasts for; defaults to 600 (30 seconds). If this is -1 the construct
     * doesn't despawn. */
    public int lifetime = 600;

    /** The damage multiplier for this construct, determined by the wand with which it was cast. */
    public float damageMultiplier = 1.0f;
    public MagicConstructEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.noPhysics = true;
    }

    @Override
    public @NotNull InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand interactionHand) {
        // TODO: player.getItemInHand(interactionHand).getItem() instanceof SpellCastingItem
        if(lifetime == -1 && getCaster() == player && player.isShiftKeyDown()){
            this.despawn();
            return InteractionResult.SUCCESS;
        }

        return super.interactAt(player, vec3, interactionHand);
    }

    /**
     * Defaults to just setDead() in EntityMagicConstruct, but is provided to allow subclasses to override this e.g.
     * bubble uses it to dismount the entity inside it and play the 'pop' sound before calling super(). You should
     * always call super() when overriding this method, in case it changes. There is no need, therefore, to call
     * setDead() when overriding.
     */
    public void despawn() {
        this.discard();
    }


    @Override
    public void tick() {
        if (this.tickCount > lifetime && lifetime != -1) {
            this.discard();
        }

        super.tick();
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if(compoundTag.contains("casterUUID")) casterUUID = compoundTag.getUUID("casterUUID");
        lifetime = compoundTag.getInt("lifetime");
        damageMultiplier = compoundTag.getFloat("damageMultiplier");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if(casterUUID != null){
            compoundTag.putUUID("casterUUID", casterUUID);
        }
        compoundTag.putInt("lifetime", lifetime);
        compoundTag.putFloat("damageMultiplier", damageMultiplier);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return casterUUID;
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return getCaster();
    }

    @Nullable
    public LivingEntity getCaster() {
        Entity entity = EntityUtil.getEntityByUUID(level(), getOwnerUUID());

        if (entity != null && !(entity instanceof LivingEntity)) {
            entity = null;
        }

        return (LivingEntity) entity;
    }

    public void setCaster(@Nullable Player caster) {
        this.casterUUID = caster == null ? null : caster.getUUID();
    }

    public boolean isValidTarget(Entity target) {
        return true;

        // TODO: AllyDesignationSystem
        //return AllyDesignationSystem.isValidTarget(this.getCaster(), target);
    }

    // TODO: Wizardry Sound
//    @Override
//    public SoundSource getSoundSource() {
//        return WizardrySounds.SPELLS;
//    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
}
