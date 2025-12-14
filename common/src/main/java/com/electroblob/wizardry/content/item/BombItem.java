package com.electroblob.wizardry.content.item;

import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.entity.projectile.BombEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BombItem<T extends BombEntity> extends Item {
    private final DeferredObject<EntityType<T>> entityType;
    private final DeferredObject<SoundEvent> sound;

    public BombItem(DeferredObject<EntityType<T>> entityType, DeferredObject<SoundEvent> sound) {
        super(new Properties().stacksTo(16));
        this.entityType = entityType;
        this.sound = sound;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        player.getCooldowns().addCooldown(this, 20);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), sound.get(), player.getSoundSource(), 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            T bombEntity = entityType.get().create(level);
            if (bombEntity != null) {
                bombEntity.setPos(player.getX(), player.getY() + player.getEyeHeight() - 0.1, player.getZ());
                bombEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.7F, 1.0F);
                level.addFreshEntity(bombEntity);
            }
        }

        if (!player.isCreative()) stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
