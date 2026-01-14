package com.binaris.wizardry.content.spell.earth;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.blockentity.BlockEntityTimer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.core.EBConstants;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Cobwebs extends RaySpell {

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        boolean flag = false;
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());

        int blastUpgradeCount = (int) ((ctx.modifiers().get(EBItems.BLAST_UPGRADE.get()) - 1) / EBConstants.RANGE_INCREASE_PER_LEVEL + 0.5f);

        float radius = property(DefaultProperties.BLAST_RADIUS) + 0.73f * blastUpgradeCount;

        List<BlockPos> sphere = BlockUtil.getBlockSphere(pos, radius);

        for (BlockPos pos1 : sphere) {
            if (!ctx.world().isEmptyBlock(pos1)) continue;

            if (!ctx.world().isClientSide) {
                ctx.world().setBlockAndUpdate(pos1, EBBlocks.VANISHING_COBWEB.get().defaultBlockState());
                if (ctx.world().getBlockEntity(pos1) instanceof BlockEntityTimer timer) {
                    timer.setLifetime((int) (property(DefaultProperties.DURATION).doubleValue() * ctx.modifiers().get(EBItems.DURATION_UPGRADE.get())));
                }
            }
            flag = true;
        }

        return flag;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellType.ATTACK, SpellAction.POINT, 30, 0, 70)
                .add(DefaultProperties.RANGE, 12F)
                .add(DefaultProperties.BLAST_RADIUS, 1.23F)
                .add(DefaultProperties.DURATION, 400)
                .build();
    }
}
