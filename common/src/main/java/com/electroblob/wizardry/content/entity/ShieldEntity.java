package com.electroblob.wizardry.content.entity;

import com.electroblob.wizardry.setup.registries.EBEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.lang.ref.WeakReference;

// TODO SHIELD ENTITY
public class ShieldEntity extends Entity {
    public WeakReference<Player> player;

    public ShieldEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

//    public ShieldEntity(Level par1World, Player player) {
//        super(EBEntities.SHIELD.get(), par1World);
//        this.player = new WeakReference<Player>(player);
//        this.noPhysics = true;
//        this.moveTo(player.getX() + player.getLookAngle().x,
//                player.getY() + 1 + player.getLookAngle().y, player.getZ() + player.getLookAngle().z,
//                player.yHeadRot, player.getXRot());
//        this.setBoundingBox(new AABB(this.getX() - 0.6f, this.getY() - 0.7f, this.getZ() - 0.6f, this.getX() + 0.6f, this.getY() + 0.7f, this.getZ() + 0.6f));
//    }

    @Override
    public void tick() {
//        Player entityplayer = player != null ? player.get() : null;
//        if (entityplayer != null) {
//            this.moveTo(entityplayer.getX() + entityplayer.getLookAngle().x * 0.3,
//                    entityplayer.getY() + 1 + entityplayer.getLookAngle().y * 0.3,
//                    entityplayer.getZ() + entityplayer.getLookAngle().z * 0.3, entityplayer.yHeadRot,
//                    entityplayer.getXRot());
//            if (!entityplayer.isUsingItem() || !(entityplayer.getItemInHand(entityplayer.getUsedItemHand()).getItem() instanceof ISpellCastingItem)) {
//                WizardData.get(entityplayer).setVariable(Shield.SHIELD_KEY, null);
//                this.discard();
//            }
//        } else if (!level.isClientSide) {
//            this.discard();
//        }
    }

    @Override
    public void lerpTo(double par1, double par3, double par5, float par7, float par8, int par9, boolean p_19902_) {
        this.setPos(par1, par3, par5);
        this.setRot(par7, par8);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (source != null && source.getDirectEntity() instanceof Projectile) {
            //level().playSound(null, source.getDirectEntity().getX(), source.getDirectEntity().getY(), source.getDirectEntity().getZ(), WizardrySounds.ENTITY_SHIELD_DEFLECT.get(), SoundSource.PLAYERS, 0.3f, 1.3f);
        }
        super.hurt(source, damage);
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

//    @Override
//    public Packet<?> getAddEntityPacket() {
//        return NetworkHooks.getEntitySpawningPacket(this);
//    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}
