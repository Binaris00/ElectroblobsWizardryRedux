package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Telekinesis extends RaySpell {

    public Telekinesis() {
        this.aimAssist(0.4f);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        Entity target = entityHit.getEntity();

        if (ctx.caster() instanceof Player && target instanceof Player playerTarget) {
            if (!ctx.world().isClientSide()) {
                ItemEntity item = playerTarget.spawnAtLocation(playerTarget.getMainHandItem(), 0);
                item.setDeltaMovement((origin.x - playerTarget.getX()) / 20, item.getDeltaMovement().y, (origin.z - playerTarget.getZ()));
            }
            playerTarget.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            return true;
        }

        if (target instanceof ItemEntity) {
            target.setDeltaMovement((origin.x - target.getX()) / 6, (origin.y - target.getY()) / 6, (origin.z - target.getZ()) / 6);
            return true;

        }

        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (ctx.caster() instanceof Player player) {
            BlockState blockstate = ctx.world().getBlockState(blockHit.getBlockPos());
            return blockstate.use(ctx.world(), player, InteractionHand.MAIN_HAND, blockHit).equals(InteractionResult.SUCCESS);
        }

        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }


    @Override
    public boolean canCastByEntity() {
        return false;
    }

    @Override
    public boolean canCastByLocation() {
        return false;
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.SORCERY, SpellType.UTILITY, SpellAction.POINT, 5, 0, 5)
                .add(DefaultProperties.RANGE, 8F)
                .build();
    }
}
