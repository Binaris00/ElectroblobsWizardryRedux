package com.electroblob.wizardry.common.content.entity.projectile;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.api.common.util.InventoryUtil;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceLocation;
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
    /** The mana used to cast this force arrow, used for artifacts. */
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
    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        // TODO ENTITY SOUND
        //this.playSound(EBSounds.ENTITY_FORCE_ARROW_HIT.get(), 1.0F, 1.0F);

        if (this.level().isClientSide()) {
            ParticleBuilder.create(EBParticles.FLASH)
                    .pos(getX(), getY(), getZ())
                    .scale(1.3f)
                    .color(0.75f, 1.0f, 0.85f)
                    .spawn(level());
        }

        super.onHitEntity(entityHitResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        // TODO ENTITY SOUND
        //this.playSound(EBSounds.ENTITY_FORCE_ARROW_HIT.get(), 1.0F, 1.0F);
        super.onHitBlock(blockHitResult);

        if (this.level().isClientSide()) {
            Vec3 pos = blockHitResult.getLocation().add(Vec3.atLowerCornerOf(blockHitResult.getDirection().getNormal()).scale(0.15));
            ParticleBuilder.create(EBParticles.FLASH)
                    .pos(pos)
                    .scale(1.3f)
                    .color(0.75f, 1.0f, 0.85f)
                    .spawn(level());
        }

    }

    @Override
    public void tickInGround() {
        returnManaToCaster();
        this.discard();
    }

    @Override
    public void tick() {
        if (getLifetime() >= 0 && this.tickCount > getLifetime()) {
            returnManaToCaster();
        }
        super.tick();
    }

    private void returnManaToCaster() {
        if (mana > 0 && getOwner() instanceof Player player) {
            // TODO: Implement Item Artifacts
            if (!player.isCreative()) {
                for (ItemStack stack : InventoryUtil.getPrioritisedHotBarAndOffhand(player)) {
                    // TODO: Implement SpellCastingItem and ManaStoringItem
//                    if (stack.getItem() instanceof SpellCastingItem && stack.getItem() instanceof ManaStoringItem &&
//                            Arrays.asList(((SpellCastingItem) stack.getItem()).getSpells(stack)).contains(Spells.FORCE_ARROW)) {
//
//                        ((ManaStoringItem) stack.getItem()).rechargeMana(stack, mana);
//                    }
                }
            }
        }
    }

    @Override
    public double getDamage() {
        return 5;
    }

    @Override
    public int getLifetime() {
        return 20;
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/force_arrow.png");
    }
}
