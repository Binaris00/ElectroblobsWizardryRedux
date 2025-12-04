package com.electroblob.wizardry.content.spell.sorcery;

import com.electroblob.wizardry.api.content.data.ArcaneLockData;
import com.electroblob.wizardry.api.content.event.EBPlayerBreakBlockEvent;
import com.electroblob.wizardry.api.content.event.EBPlayerUseBlockEvent;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ArcaneLockSpell extends RaySpell {
    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!(ctx.caster() instanceof Player player)) return false;

        if (!ctx.world().isClientSide) {
            BlockPos pos = blockHit.getBlockPos();

            if (toggleLock(ctx, pos, player)) {
                // Handle double chests
                BlockPos otherHalf = BlockUtil.getConnectedChest(ctx.world(), pos);
                if (otherHalf != null) {
                    toggleLock(ctx, otherHalf, player);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    private boolean toggleLock(CastContext ctx, BlockPos pos, Player player) {
        BlockEntity blockEntity = ctx.world().getBlockEntity(pos);
        if (!(blockEntity instanceof BaseContainerBlockEntity containerBlock)) return false;

        ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(containerBlock);

        if (data.isArcaneLocked()) {
            if (data.getArcaneLockOwnerUUID().equals(player.getUUID())) {
                // Unlocking
                data.setArcaneLockOwner(null);
                player.displayClientMessage(Component.translatable("spell.ebwizardry.arcane_lock.unlocking")
                        .withStyle(ChatFormatting.LIGHT_PURPLE), true);
                return true;
            } else {
                player.displayClientMessage(Component.translatable("spell.ebwizardry.arcane_lock.not_owning").withStyle(this.getElement().getColor()), true);
                return false;
            }
        } else {
            // Locking
            data.setArcaneLockOwner(player.getUUID().toString());
            blockEntity.setChanged();
            ctx.world().sendBlockUpdated(pos, ctx.world().getBlockState(pos), ctx.world().getBlockState(pos), 3);
            player.displayClientMessage(Component.translatable("spell.ebwizardry.arcane_lock.locking")
                    .withStyle(ChatFormatting.LIGHT_PURPLE), true);
            return true;
        }
    }

    /**
     * Checks if a player is allowed to use a block with arcane lock.
     * Called from the block use event listener.
     *
     * @param event The player use block event
     */
    public static void onPlayerUseBlock(EBPlayerUseBlockEvent event) {
        if (event.getLevel().isClientSide()) return;

        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (!(blockEntity instanceof BaseContainerBlockEntity containerBlock)) return;

        ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(containerBlock);

        if (data.isArcaneLocked()) {
            // Check if the player is the owner
            if (!data.getArcaneLockOwnerUUID().equals(event.getPlayer().getUUID())) {
                event.getPlayer().displayClientMessage(Component.translatable("spell.ebwizardry.arcane_lock.not_owning")
                        .withStyle(ChatFormatting.LIGHT_PURPLE), true);
                event.setCanceled(true);
            }
        }
    }

    /**
     * Checks if a player is allowed to break a block with arcane lock.
     * Called from the block break event listener.
     *
     * @param event The player break block event
     */
    public static void onPlayerBreakBlock(EBPlayerBreakBlockEvent event) {
        if (event.getLevel().isClientSide()) return;

        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (!(blockEntity instanceof BaseContainerBlockEntity containerBlock)) return;

        ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(containerBlock);

        if (data.isArcaneLocked()) {
            // Check if the player is the owner
            if (!data.getArcaneLockOwnerUUID().equals(event.getPlayer().getUUID())) {
                event.getPlayer().displayClientMessage(Component.translatable("spell.ebwizardry.arcane_lock.not_owning")
                        .withStyle(ChatFormatting.LIGHT_PURPLE), true);
                event.setCanceled(true);
            }
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.SORCERY, SpellType.UTILITY, SpellAction.POINT, 50, 0, 100)
                .add(DefaultProperties.RANGE, 8.0F)
                .build();
    }
}

