package com.binaris.wizardry.content.spell.sorcery;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
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

        if (!(target instanceof ItemEntity item)) return false;

        // This check is thanks to Physics mod, let see if it will fix item despawning
        if (!ctx.world().isClientSide() && ctx.caster() instanceof Player caster) {
            Vec3 toCaster = origin.subtract(item.position());
            double distSq = toCaster.lengthSqr();

            // if the item is really close it will add it to the inventory directly
            if (distSq < 4.0) {
                ItemStack stack = item.getItem();
                if (caster.getInventory().add(stack)) {
                    item.discard();
                } else {
                    item.setItem(stack);
                }
                return true;
            }

            // set limit of pull speed
            double pullSpeed = Math.min(toCaster.length() / 6.0, 1.0);
            item.setDeltaMovement(toCaster.normalize().scale(pullSpeed));
        }
        return true;
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
                .assignBaseProperties(SpellTiers.NOVICE, Elements.SORCERY, SpellTypes.UTILITY, SpellAction.POINT, 5, 0, 5)
                .add(DefaultProperties.RANGE, 10F)
                .build();
    }
}
