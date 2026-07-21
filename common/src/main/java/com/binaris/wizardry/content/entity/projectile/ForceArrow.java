package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ForceArrow extends MagicArrowEntity {
    private int mana = 0;

    public ForceArrow(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public ForceArrow(Level world) {
        super(EBEntities.FORCE_ARROW.get(), world);
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return EBSounds.ENTITY_FORCE_ARROW_HIT.get();
    }

    @Override
    public void tick() {
        if (this.tickCount >= getLifetime()) returnManaToCaster();
        super.tick();
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) return;
        Vec3 pos = result instanceof BlockHitResult blockHitResult ?
                blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.15)) :
                result.getLocation();
        ParticleBuilder.create(EBParticles.FLASH)
                .pos(pos)
                .scale(1.6f)
                .color(0.75f, 1.0f, 0.85f)
                .spawn(level());
    }

    private void returnManaToCaster() {
        if (mana <= 0 || !(getOwner() instanceof Player player)) return;

        if (!player.isCreative() && ArtifactChannel.isEquipped(player, EBItems.RING_MANA_RETURN.get())) {
            EntityUtil.getHotBarAndHandItems(player)
                    .stream().filter(st -> st.getItem() instanceof IManaItem)
                    .findAny()
                    .ifPresent(st -> ((IManaItem) st.getItem()).rechargeMana(st, mana));
        }
    }

    @Override
    public void tickInGround() {
        returnManaToCaster();
        this.discard();
    }

    @Override
    public double getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.FORCE_ARROW.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return 20;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.FORCE;
    }
}
