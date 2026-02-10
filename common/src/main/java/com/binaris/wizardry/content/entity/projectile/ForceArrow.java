package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.integrations.accessories.EBAccessoriesIntegration;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
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

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        this.playSound(EBSounds.ENTITY_FORCE_ARROW_HIT.get(), 1.0F, 1.0F);

        if (this.level().isClientSide()) {
            ParticleBuilder.create(EBParticles.FLASH)
                    .pos(getX(), getY(), getZ())
                    .scale(1.3f)
                    .color(0.75f, 1.0f, 0.85f)
                    .spawn(level());
        }

        super.onHitEntity(hitResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        this.playSound(EBSounds.ENTITY_FORCE_ARROW_HIT.get(), 1.0F, 1.0F);
        super.onHitBlock(blockHitResult);

        if (this.level().isClientSide()) {
            Vec3 pos = blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.15));
            ParticleBuilder.create(EBParticles.FLASH)
                    .pos(pos)
                    .scale(1.6f)
                    .color(0.75f, 1.0f, 0.85f)
                    .spawn(level());
        }
    }

    @Override
    public void tick() {
        if (getLifetime() >= 0 && this.tickCount >= getLifetime()) returnManaToCaster();
        super.tick();
    }

    private void returnManaToCaster() {
        if (mana <= 0 || !(getOwner() instanceof Player player)) return;

        if (!player.isCreative() && EBAccessoriesIntegration.isEquipped(player, EBItems.RING_MANA_RETURN.get())) {
            InventoryUtil.getHotBarAndOffhand(player)
                    .stream().filter(st -> st.getItem() instanceof IManaStoringItem)
                    .findAny()
                    .ifPresent(st -> ((IManaStoringItem) st.getItem()).rechargeMana(st, mana));
        }
    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return EBSounds.ENTITY_FORCE_ARROW_HIT.get();
    }

    @Override
    public void tickInGround() {
        returnManaToCaster();
        this.discard();
    }

    @Override
    public double getDamage() {
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
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/force_arrow.png");
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return EBDamageSources.FORCE;
    }
}
